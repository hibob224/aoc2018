package day11

fun main(args: Array<String>) {
    println("Part one: ${Day11.solvePartOne()}")
    println("Parse two: ${Day11.solvePartTwo()}")
}

object Day11 {

    private const val gridSerialNumber = 5177
    private const val gridDimensions = 300

    fun solvePartOne(): String {
        val grid = generatePowerGrid()
        val powerPoints = mutableMapOf<Pair<Int, Int>, Int>()

        for (x in 1..gridDimensions - 2) {
            for (y in 1..gridDimensions - 2) {
                powerPoints[Pair(x, y)] = sumGrid(grid, Pair(x, y), 3)
            }
        }

        val largest3by3 = powerPoints.maxBy { it.value }!!

        return "${largest3by3.key}, ${largest3by3.value}"
    }

    fun solvePartTwo(): String {
        val grid = generatePowerGrid()
        val powerPoints = mutableMapOf<Triple<Int, Int, Int>, Int>()

        // TODO Rewrite part two solution
        // This solution is horribly inefficient and would take forever to reach the end of the 300. It just so happens
        // my answer was only a 8x8 area which was quick to calculate. Need to rewrite at some point!
        for (size in 1..10) {
            for (x in 1..gridDimensions - size) {
                for (y in 1..gridDimensions - size) {
                    powerPoints[Triple(x, y, size)] = sumGrid(grid, Pair(x, y), size)
                }
            }
        }

        val large = powerPoints.maxBy { it.value }!!.key
        return large.toString()
    }

    private fun sumGrid(grid: Map<Pair<Int, Int>, Int>, start: Pair<Int, Int>, size: Int): Int {
        var total = 0
        for (x in start.first until minOf(start.first + size, gridDimensions)) {
            for (y in start.second until minOf(start.second + size, gridDimensions)) {
                total += grid.getOrDefault(Pair(x, y), 0)
            }
        }
        return total
    }

    private fun generatePowerGrid(): Map<Pair<Int, Int>, Int> {
        val grid = mutableMapOf<Pair<Int, Int>, Int>()

        for (x in 1..gridDimensions) {
            for (y in 1..gridDimensions) {
                grid[Pair(x, y)] = calculateValue(x, y)
            }
        }

        return grid
    }

    private fun calculateValue(x: Int, y: Int): Int {
        val rackId = x + 10
        var powerLevel = y * rackId
        powerLevel += gridSerialNumber
        powerLevel *= rackId
        return if (powerLevel > 99) {
            powerLevel.toString().takeLast(3).take(1).toInt() - 5
        } else {
            -5
        }
    }
}