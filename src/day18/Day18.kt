package day18

import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day18.solvePartOne()}")
    println("Parse two: ${Day18.solvePartTwo()}")
}

object Day18 {

    private val file = File("src/day18/input.txt")
    private val area = mutableMapOf<Pair<Int, Int>, Tile>()
    private val states = mutableListOf<MutableMap<Pair<Int, Int>, Tile>>()
    private var width = 0
    private var height = 0

    fun solvePartOne(): Int {
        readInput()
        width = area.keys.maxBy { it.first }!!.first
        height = area.keys.maxBy { it.second }!!.second

        repeat(10) {
            performMinute()
        }
        return area.values.count { it is Lumberyard } * area.values.count { it is Trees }
    }

    fun solvePartTwo(): Int {
        readInput()

        for (iteration in 1..1000000000) {
            performMinute()
            if (area in states) {
                val firstResult = states.indexOf(area)
                val cycle = states.size - firstResult
                val result = states[((1000000000 - iteration) % cycle) + firstResult]
                return result.values.count { it is Lumberyard } * result.values.count { it is Trees }
            }
            states.add(mutableMapOf<Pair<Int, Int>, Tile>().also { it.putAll(area) })
        }
        return area.values.count { it is Lumberyard } * area.values.count { it is Trees }
    }

    private fun readInput() {
        area.clear()
        file.readLines().forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '.' -> area[x to y] = Open()
                    '#' -> area[x to y] = Lumberyard()
                    '|' -> area[x to y] = Trees()
                }
            }
        }
    }

    private fun performMinute() {
        val startArea = mutableMapOf<Pair<Int, Int>, Tile>().also { it.putAll(area) }
        (0..height).forEach { y ->
            (0..width).forEach { x ->
                val surroundingTiles = mutableListOf<Tile>()
                startArea[x.inc() to y]?.let { surroundingTiles.add(it) }
                startArea[x.dec() to y]?.let { surroundingTiles.add(it) }
                startArea[x.inc() to y.inc()]?.let { surroundingTiles.add(it) }
                startArea[x.dec() to y.dec()]?.let { surroundingTiles.add(it) }
                startArea[x.inc() to y.dec()]?.let { surroundingTiles.add(it) }
                startArea[x.dec() to y.inc()]?.let { surroundingTiles.add(it) }
                startArea[x to y.inc()]?.let { surroundingTiles.add(it) }
                startArea[x to y.dec()]?.let { surroundingTiles.add(it) }

                when (startArea[x to y]) {
                    is Open -> {
                        if (surroundingTiles.count { it is Trees } >= 3) {
                            area[x to y] = Trees()
                        }
                    }
                    is Trees -> {
                        if (surroundingTiles.count { it is Lumberyard } >= 3) {
                            area[x to y] = Lumberyard()
                        }
                    }
                    is Lumberyard -> {
                        if (surroundingTiles.count { it is Lumberyard } < 1
                                || surroundingTiles.count { it is Trees } < 1) {
                            area[x to y] = Open()
                        }
                    }
                }
            }
        }
    }
}

abstract class Tile
class Open : Tile() {
    override fun equals(other: Any?): Boolean = other is Open
}
class Trees : Tile() {
    override fun equals(other: Any?): Boolean = other is Trees
}
class Lumberyard : Tile() {
    override fun equals(other: Any?): Boolean = other is Lumberyard
}