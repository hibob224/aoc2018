package day12

import utils.orZero
import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day12.solvePartOne()}")
    println("Parse two: ${Day12.solvePartTwo()}")
}

object Day12 {

    private val pattern = """([#|.]{5}) => ([.|#])""".toRegex()
    private val file = File("src/day12/input.txt")
    private val state = mutableMapOf<Int, Char>()
    private val combinations = mutableMapOf<String, Char>()

    init {
        val inputLines = file.readLines()
        inputLines.first().split("initial state: ")[1].forEachIndexed { index, char ->
            state[index] = char
        }
        (2 until inputLines.size).forEach {
            val (combination, result) = pattern.find(inputLines[it])!!.destructured
            combinations[combination] = result.first()
        }
    }

    fun solvePartOne(): Int = performGenerations(20).filter { it.value == '#' }.keys.sum().orZero()

    fun solvePartTwo(): Long {
        val stateMap = performGenerations(200)

        var total = 0L
        stateMap.filter { it.value == '#' }.keys.forEach {
            total += it + (50000000000 - 200)
        }

        return total
    }

    private fun performGenerations(generations: Int): Map<Int, Char> {
        val stateMap = HashMap(state)

        for (generation in 0 until generations) {
            val currentState = HashMap(stateMap)

            (stateMap.keys.min()!! - 3..stateMap.keys.max()!! + 3).forEach { index ->
                var match = ""
                (index - 2..index + 2).forEach { match += currentState.getOrElse(it) { '.' } }
                stateMap[index] = combinations[match] ?: '.'
            }
        }

        return stateMap
    }
}