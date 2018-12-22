package day22

import java.io.File
import java.lang.IllegalStateException

fun main(args: Array<String>) {
    println("Part one: ${Day22.solvePartOne()}")
    println("Part two: ${Day22.solvePartTwo()}")
}

object Day22 {

    private val file = File("src/day22/input.txt")
    private val grid = mutableMapOf<Pair<Int, Int>, Pair<Int, Type>>()
    private var target = Pair(0, 0)
    private var depth = 0

    init {
        parse()
    }

    fun solvePartOne(): Int {
        var risk = 0

        (0..target.first).forEach { x ->
            (0..target.second).forEach { y ->
                risk += grid[x to y]!!.second.risk()
            }
        }

        return risk
    }

    fun solvePartTwo(): Int {
        return 0
    }

    private fun parse() {
        // Read file
        val lines = file.readLines()
        depth = lines.first().split("depth: ")[1].toInt()
        val coordPattern = """(\d+),(\d+)""".toRegex()
        val (xTarg, yTarg) = coordPattern.find(lines[1])!!.destructured
        target = xTarg.toInt() to yTarg.toInt()

        // Create grid
        (0..target.first).forEach { x ->
            (0..target.second).forEach { y ->
                val coord = x to y
                val geoIndex = when {
                    coord == 0 to 0 || coord == target -> 0
                    y == 0 -> x * 16807
                    x == 0 -> y * 48271
                    else -> grid[x.dec() to y]!!.first * grid[x to y.dec()]!!.first
                }

                val eroLevel = (geoIndex + depth) % 20183
                val type = when (eroLevel % 3) {
                    0 -> Type.ROCKY
                    1 -> Type.WET
                    2 -> Type.NARROW
                    else -> throw IllegalStateException("Illegal")
                }
                grid[coord] = eroLevel to type
            }
        }
    }
}

enum class Type {
    ROCKY, WET, NARROW;

    fun risk(): Int = when (this) {
        ROCKY -> 0
        WET -> 1
        NARROW -> 2
    }
}