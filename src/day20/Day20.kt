package day20

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    println("Part one: ${Day20.solvePartOne()}")
    println("Part two: ${Day20.solvePartTwo()}")
}

object Day20 {

    private val file = File("src/day20/input.txt")
    private val rooms = mutableMapOf<Pair<Int, Int>, Int>()

    init {
        parse()
    }

    fun solvePartOne(): Int = rooms.maxBy { it.value }!!.value

    fun solvePartTwo(): Int = rooms.count { it.value >= 1000 }

    private fun parse() {
        var current = 0 to 0
        rooms[current] = 0
        val branchQueue = ArrayDeque<Pair<Int, Int>>()

        val logRoom = { next: Pair<Int, Int> ->
            val inc = rooms.getOrDefault(current, 0).inc()
            rooms[next] = minOf(rooms[next] ?: inc, inc) // New rooms will increment the value from the previous room. Already seen rooms will keep the shortest distance from 0,0
            current = next
        }

        file.readText().forEach {
            when (it) {
                '(' -> branchQueue.add(current) // Met a branch, store where we currently are so we can retrieve it again later
                ')' -> current = branchQueue.pollLast() // Finished processing branches, pop where we branched from and continue
                '|' -> current = branchQueue.peekLast() // New branch, retrieve the start of the branch from the queue
                'N' -> logRoom.invoke(current.first to current.second.dec())
                'E' -> logRoom.invoke(current.first.inc() to current.second)
                'S' -> logRoom.invoke(current.first to current.second.inc())
                'W' -> logRoom.invoke(current.first.dec() to current.second)
            }
        }
    }
}