package day07

import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day7.solvePartOne()}")
    println("Parse two: ${Day7.solvePartTwo()}")
}

object Day7 {

    private val pattern = """Step (\w) must be finished before step (\w) can begin""".toRegex()
    private val alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase()
    private const val numberOfWorkers = 5 // 2 for example
    private const val extraTime = 60 // 0 for example

    fun solvePartOne(): String {
        val instructions = File("src/day07/input.txt").readLines().map {
            val (one, two) = pattern.find(it)!!.destructured
            one to two
        }

        val requirementMap = mutableMapOf<String, MutableList<String>>()

        instructions.forEach {
            requirementMap.putIfAbsent(it.first, mutableListOf())
            requirementMap.putIfAbsent(it.second, mutableListOf())
            requirementMap.getOrPut(it.second) { mutableListOf() }.add(it.first)
        }

        var instructionOrder = ""

        do {
            val availableOrders = requirementMap.filter { it.value.size == 0 }.entries.sortedBy { it.key }
            availableOrders.first().let {
                instructionOrder += it.key
                requirementMap.remove(it.key)
                requirementMap.forEach { _, reqs -> reqs.remove(it.key) }
            }
        } while (requirementMap.isNotEmpty())

        return instructionOrder
    }

    fun solvePartTwo(): Int {
        val instructions = File("src/day07/input.txt").readLines().map {
            val (one, two) = pattern.find(it)!!.destructured
            one to two
        }

        val requirementMap = mutableMapOf<String, MutableList<String>>()

        instructions.forEach {
            requirementMap.putIfAbsent(it.first, mutableListOf())
            requirementMap.putIfAbsent(it.second, mutableListOf())
            requirementMap.getOrPut(it.second) { mutableListOf() }.add(it.first)
        }

        var instructionLength = 0
        var workers = mutableMapOf<String, Int>()

        do {
            workers.forEach { key, value ->
                workers[key] = value - 1
                if (workers[key]!! <= 0) {
                    requirementMap.forEach { _, reqs -> reqs.remove(key) }
                }
            }
            workers = workers.filterNot { it.value <= 0 }.toMutableMap()

            if (workers.size < numberOfWorkers && requirementMap.isNotEmpty()) {
                // Put those elves to work
                val availableOrders = requirementMap.filter { it.value.size == 0 }.entries.sortedBy { it.key }
                availableOrders.take(numberOfWorkers - workers.size).forEach {
                    workers[it.key] = alphabet.indexOf(it.key) + extraTime + 1
                    requirementMap.remove(it.key)
                }
            }

            instructionLength++
        } while (workers.isNotEmpty())

        return instructionLength - 1
    }
}