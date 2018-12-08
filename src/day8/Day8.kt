package day8

import utils.orZero
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    println("Part one: ${Day8.solvePartOne()}")
    println("Parse two: ${Day8.solvePartTwo()}")
}

object Day8 {

    private val data = File("src/day8/input.txt").readText().split(" ").map { it.toInt() }
    private val dataStack = ArrayDeque<Int>(data)
    private val nodes = mutableListOf<Node>()

    init {
        do {
            nodes.add(parseNode())
        } while (dataStack.isNotEmpty())
    }

    fun solvePartOne(): Int {
        return (nodes + nodes.flatMap { it.allChildren }).flatMap { it.metaData }.sum()
    }

    fun solvePartTwo(): Int {
        return nodes.first().value()
    }

    private fun parseNode(): Node {
        val childCount = dataStack.pop()
        val metadataCount = dataStack.pop()
        val node = Node()
        repeat(childCount) { node.childNodes.add(parseNode()) }
        repeat(metadataCount) { node.metaData.add(dataStack.pop()) }
        return node
    }
}

class Node {
    val childNodes = mutableListOf<Node>()
    var metaData = mutableListOf<Int>()
    val allChildren: List<Node>
        get() = childNodes + childNodes.flatMap { it.allChildren }

    fun value(): Int {
        if (childNodes.isEmpty()) {
            return metaData.sum()
        }
        var value = 0
        metaData.forEach { value += childNodes.getOrNull(it - 1)?.value().orZero() }
        return value
    }
}