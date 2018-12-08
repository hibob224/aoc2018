package day06

import java.io.File
import java.lang.Math.abs

fun main(args: Array<String>) {
    println("Part one: ${Day6.solvePartOne()}")
    println("Parse two: ${Day6.solvePartTwo()}")
}

object Day6 {

    fun solvePartOne(): Int {
        val coords = File("src/day06/input.txt").readLines().map {
            val split = it.split(", ")
            Pair(split[0].toInt(), split[1].toInt())
        }

        val leftMost = coords.minBy { it.first }!!.first
        val rightMost = coords.maxBy { it.first }!!.first
        val topMost = coords.minBy { it.second }!!.second
        val bottomMost = coords.maxBy { it.second }!!.second

        val coordList = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Int, Int>>>()

        for (x in leftMost..rightMost) {
            for (y in topMost..bottomMost) {
                val coordClosest = coordList.getOrPut(Pair(x, y)) { mutableListOf() }
                coordClosest.add(coords.minBy { abs(it.first - x) + abs(it.second - y) }!!)
            }
        }

        val groups = coordList.filter { it.value.size == 1 }
                .entries
                .groupBy { it.value.first() }

        return groups.filterNot {
            it.key.first == leftMost || it.key.first == rightMost || it.key.second == topMost || it.key.second == bottomMost
        }.maxBy { it.value.size }!!.value.size
    }

    fun solvePartTwo(): Int {
        val coords = File("src/day06/input.txt").readLines().map {
            val split = it.split(", ")
            Pair(split[0].toInt(), split[1].toInt())
        }

        val leftMost = coords.minBy { it.first }!!.first
        val rightMost = coords.maxBy { it.first }!!.first
        val topMost = coords.minBy { it.second }!!.second
        val bottomMost = coords.maxBy { it.second }!!.second

        var regionSize = 0

        for (x in leftMost..rightMost) {
            for (y in topMost..bottomMost) {
                var total = 0
                coords.forEach {
                    total += (abs(it.first - x) + abs(it.second - y))
                }
                if (total < 10000) {
                    regionSize++
                }
            }
        }

        return regionSize
    }
}