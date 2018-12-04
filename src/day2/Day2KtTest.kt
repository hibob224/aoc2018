package day2

import org.junit.Test
import kotlin.test.assertEquals


internal class Day2KtTest {

    @Test
    fun solvePartOne() {
        assertEquals(6370, Day2.solvePartOne())
    }

    @Test
    fun solvePartTwo() {
        assertEquals("rmyxgdlihczskunpfijqcebtv", Day2.solvePartTwo())
    }
}