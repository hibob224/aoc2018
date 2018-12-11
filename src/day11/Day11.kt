package day11

fun main(args: Array<String>) {
    println("Part one: ${Day11.solvePartOne()}")
    println("Parse two: ${Day11.solvePartTwo()}")
}

object Day11 {

    private const val gridSerialNumber = 5177
    private const val gridDimensions = 300
    private val grid = generateListGrid()

    fun solvePartOne(): String {
        var largestArea = Triple(0, 0, Int.MIN_VALUE)

        for (x in 0 until gridDimensions - 2) {
            for (y in 0 until gridDimensions - 2) {
                val total = (0..2).sumBy { dX ->
                    (0..2).sumBy { dY ->
                        grid[x + dX][y + dY]
                    }
                }
                if (total > largestArea.third) {
                    largestArea = Triple(x.inc(), y.inc(), total)
                }
            }
        }

        return "$largestArea"
    }

    fun solvePartTwo(): String {
        var largestArea = Triple(0, 0, 0)
        var largestValue = Int.MIN_VALUE

        for (x in 0 until gridDimensions) {
            for (y in 0 until gridDimensions) {
                var total = 0

                for (size in 1 until gridDimensions - maxOf(x, y)) {
                    total += (0 until size).sumBy { grid[x + it][y + size.dec()] }
                    total += (0 until size.dec()).sumBy { grid[x + size.dec()][y + it] }
                    if (total > largestValue) {
                        largestArea = Triple(x.inc(), y.inc(), size)
                        largestValue = total
                    }
                }
            }
        }

        return "$largestArea"
    }

    private fun generateListGrid(): List<List<Int>> {
        return (1..gridDimensions).map { x ->
            (1..gridDimensions).map { y ->
                calculateValue(x, y)
            }
        }
    }

    private fun calculateValue(x: Int, y: Int): Int {
        val rackId = x + 10
        val powerLevel = ((y * rackId) + gridSerialNumber) * rackId
        return if (powerLevel > 99) {
            powerLevel.toString().takeLast(3).take(1).toInt() - 5
        } else {
            -5
        }
    }
}