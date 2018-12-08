package day05

import utils.orZero
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    println("Part one: ${Day5.solvePartOne()}")
    println("Parse two: ${Day5.solvePartTwo()}")
}

object Day5 {
    private val actualInput = File("src/day05/input.txt").readText()

    fun solvePartOne(input: String = actualInput): Int {
        val result = ArrayDeque<Char>(actualInput.length)

        for (it in input) {
            if (result.isNotEmpty()) {
                val last = result.peek()
                if (it.equals(last, true) &&
                        ((it.isUpperCase() && last.isLowerCase()) || (it.isLowerCase() && last.isUpperCase()))) {
                    result.pop()
                    continue
                }
            }
            result.push(it)
        }
        return result.size
    }

    fun solvePartTwo(): Int {
        val results = mutableMapOf<Char, Int>()
        "abcdefghijklmnopqrstuvwxyz".forEach {
            results[it] = solvePartOne(actualInput.replace("$it".toRegex(RegexOption.IGNORE_CASE), ""))
        }
        return results.minBy { it.value }?.value.orZero()
    }
}