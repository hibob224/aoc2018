import day12.Day12
import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    @Test
    fun solvePartOne() {
        assertEquals(1184, Day12.solvePartOne())
    }

    @Test
    fun solvePartTwo() {
        assertEquals(250000000219, Day12.solvePartTwo())
    }
}