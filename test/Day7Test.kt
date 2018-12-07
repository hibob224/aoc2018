import day7.Day7
import org.junit.Test

import org.junit.Assert.*

class Day7Test {

    @Test
    fun solvePartOne() {
        assertEquals("OCPUEFIXHRGWDZABTQJYMNKVSL", Day7.solvePartOne())
    }

    @Test
    fun solvePartTwo() {
        assertEquals(991, Day7.solvePartTwo())
    }
}