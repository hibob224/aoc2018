import day11.Day11
import org.junit.Test

import org.junit.Assert.*

class Day11Test {

    @Test
    fun solvePartOne() {
        assertEquals("(235, 22, 30)", Day11.solvePartOne())
    }

    @Test
    fun solvePartTwo() {
        assertEquals("(231, 135, 8)", Day11.solvePartTwo())
    }
}