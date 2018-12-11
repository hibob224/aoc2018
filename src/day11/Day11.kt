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
        return ""
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
                val rackId = x + 10
                var powerLevel = y * rackId
                powerLevel += gridSerialNumber
                powerLevel *= rackId
                val finalPowerLevel = if (powerLevel > 99) {
                    powerLevel.toString().takeLast(3).take(1).toInt()
                } else {
                    0
                }
                grid[Pair(x, y)] = finalPowerLevel - 5
            }
        }

        return grid
    }
}