import day14.Day14
import org.junit.Test

import org.junit.Assert.*

class Day14Test {

    @Test
    fun solvePartOne() {
        assertEquals("7116398711", Day14.solvePartOne())
    }

    @Test
    fun solvePartTwo() {
        assertEquals(20316365, Day14.solvePartTwo())
    }
}