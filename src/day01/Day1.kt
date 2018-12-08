package day01

import utils.repeat
import java.io.File
import java.lang.IllegalStateException

object Day1 {

    fun main(args: Array<String>) {
        println("Part one: ${solvePartOne()}")
        println("Parse two: ${solvePartTwo()}")
    }

    /**
     * Will sum all the deltas to return the final frequency
     */
    fun solvePartOne(): Int = File("src/day01/input.txt").readLines().asSequence().map { it.toInt() }.sum()

    /**
     * Will return the first duplicated frequency
     */
    fun solvePartTwo(): Int {
        val frequencies = mutableSetOf<Int>()
        var total = 0
        File("src/day01/input.txt").readLines().map { it.toInt() }.asSequence().repeat().forEach {
            total += it
            if (!frequencies.add(total)) {
                return total
            }
        }
        throw IllegalStateException("Escaped infinite loop")
    }
}