package day17

import utils.then
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    println("Part one: ${Day17.solvePartOne()}")
    println("Parse two: ${Day17.solvePartTwo()}")
}

/**
 * TODO Fix part 1 edge case
 * Part 1 does not quite give the correct answer. Managed to get the correct answer by finding the edge case bug in
 * Google Sheets and subtracting them from the answer given by part one here. In two spots a second flow is coming off
 * the edge of another flow and causing two streams to go down next to each other.
 */
object Day17 {

    private val file = File("src/day17/input.txt")
    private val pattern = """(\w)=(\d+), \w=(\d+)..(\d+)""".toRegex()
    private val walls = mutableSetOf<Pair<Int, Int>>()
    private val water = mutableSetOf<Pair<Int, Int>>()
    private val topWater = mutableSetOf<Pair<Int, Int>>()
    private val flow = ArrayDeque<Pair<Int, Int>>().also { it.add(500 to 0) }
    private val permenantWater = mutableSetOf<Pair<Int, Int>>()
    private var targetY = -1

    fun solvePartOne(): Int {
        file.readLines().forEach {
            val (axis1, axis1Loc, axis2From, axis2To) = pattern.find(it)!!.destructured
            if (axis1 == "x") {
                (axis2From.toInt()..axis2To.toInt()).forEach {
                    walls.add(axis1Loc.toInt() to it)
                }
            } else if (axis1 == "y") {
                (axis2From.toInt()..axis2To.toInt()).forEach {
                    walls.add(it to axis1Loc.toInt())
                }
            }
        }

        targetY = walls.maxBy { it.second }!!.second

        while (flow.isNotEmpty()) {
            val (x, y) = flow.pop()

            if (topWater.contains(x to y.inc())) {
                water.add(x to y)
//                continue
            }

            if (walls.contains(x to y.inc()) || water.contains(x to y.inc())) {
                var leftWall: Int? = null
                var rightWall: Int? = null
                var leftDrop: Int? = null
                var rightDrop: Int? = null
                var index = 1

                while (leftWall == null && leftDrop == null) {
                    if (walls.contains(x - index to y)) {
                        leftWall = x - index
                        break
                    }
                    if (!walls.contains(x - index to y.inc())
                            && !water.contains(x - index to y.inc())) {
                        leftDrop = x - index
                        break
                    }
                    index++
                }
                index = 1
                while (rightWall == null && rightDrop == null) {
                    if (walls.contains(x + index to y)) {
                        rightWall = x + index
                        break
                    }
                    if (!walls.contains(x + index to y.inc())
                            && !water.contains(x + index to y.inc())) {
                        rightDrop = x + index
                        break
                    }
                    index++
                }

                if (topWater.contains(x to y.inc()) && (leftWall == null || rightWall == null)) {
                    continue
                }

                val left = leftWall ?: leftDrop!!
                val right = rightWall ?: rightDrop!!

                (left.inc()..right.dec()).forEach {
                    flow.remove(it to y)
                    water.add(it to y)
                    if (leftWall != null && rightWall != null) {
                        permenantWater.add(it to y)
                    }
                    if (leftDrop != null || rightDrop != null) {
                        topWater.add(it to y)
                    }
                }

                if (leftWall != null && rightWall != null) {
                    // Wall on both sides, increase water level
                    addFlow(x to y.dec())
                }

                leftDrop?.let {
                    addFlow(left to y)
                }
                rightDrop?.let {
                    addFlow(right to y)
                }

                continue
            }

            water.add(x to y)
                addFlow(x to y.inc())
//            println("Lowest Flow: ${water.maxBy { it.second }!!.second} Flow size: ${flow.size}")
        }

        printCsv()
//        printGrid()

        return water.filter { it.second in 1..targetY }.size
    }

    fun solvePartTwo(): Int {
        return permenantWater.size
    }

    private fun addFlow(coord: Pair<Int, Int>) {
        if (coord.second < targetY) {
//            println("Adding flow $coord")
            flow.add(coord)
        }
    }

    fun printGrid() {
        val smallX = walls.minBy { it.first }!!.first
        val largeX = walls.maxBy { it.first }!!.first
        val largeY = walls.maxBy { it.second }!!.second

        (0..largeY).forEach { y ->
            (smallX..largeX).forEach { x ->
                when {
                    walls.contains(x to y) -> print("#")
                    water.contains(x to y) -> print("~")
                    else -> print(".")
                }
            }
            println()
        }
    }

    fun printCsv() {
        val largeX = maxOf(walls.maxBy { it.first }!!.first, water.maxBy { it.first }!!.first)
        val largeY = maxOf(walls.maxBy { it.second }!!.second, water.maxBy { it.second }!!.second)

        File("src/day17/output.csv").printWriter().apply {
            (0..largeY).forEach {  y ->
                (0..largeX).forEach { x ->
                    val prefix = (x >= 0) then "," ?: ""
                    when {
                        walls.contains(x to y) -> print("$prefix#")
                        water.contains(x to y) -> print("$prefix~")
                        else -> print("$prefix.")
                    }
                }
                println()
            }
        }
    }
}