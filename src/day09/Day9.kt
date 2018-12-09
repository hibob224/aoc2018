package day09

import utils.isMultipleOf
import utils.orZero
import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day9.solvePartOne()}")
    println("Parse two: ${Day9.solvePartTwo()}")
}

object Day9 {

    private val data = File("src/day09/input.txt").readLines().first()
    private val pattern = """(\d+) players; last marble is worth (\d+) points""".toRegex()
    private var players = 0
    private var lastMarble = 0L

    init {
        pattern.find(data)?.let {
            players = it.destructured.component1().toInt()
            lastMarble = it.destructured.component2().toLong()
        }
    }

    fun solvePartOne(): Long = solveBoard(players, lastMarble)

    fun solvePartTwo(): Long = solveBoard(players, lastMarble.times(100))

    private fun solveBoard(players: Int, lastMarble: Long): Long {
        var marble = Marble(0)
        val scoreBoard = LongArray(players)

        for (i in 1..lastMarble) {
            val player = (i % players).toInt()

            if (i.isMultipleOf(23)) {
                repeat(8) { marble = marble.previous }
                scoreBoard[player] += marble.next.value + i
                marble.next.next.previous = marble
                marble.next = marble.next.next
                marble = marble.next
            } else {
                marble = marble.next
                val newMarble = Marble(i, marble, marble.next)
                marble.next.previous = newMarble
                marble.next = newMarble
                marble = newMarble
            }
        }

        return scoreBoard.max().orZero()
    }
}

class Marble(val value: Long, _previous: Marble? = null, _next: Marble? = null) {
    var previous = _previous ?: this
    var next = _next ?: this
}