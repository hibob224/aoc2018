package day09

import utils.isMultipleOf
import utils.orZero
import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day9.solvePartOne()}")
    println("Parse two: ${Day9.solvePartTwo()}")
}

object Day9 {

    private val data = File("src/day09/input.txt").readText()
    private val pattern = """(\d+) players; last marble is worth (\d+) points""".toRegex()
    private var players = 0
    private var lastMarble = 0

    init {
        pattern.find(data)?.let {
            players = it.destructured.component1().toInt()
            lastMarble = it.destructured.component2().toInt()
        }
    }

    fun solvePartOne(): Int = solveBoard(players, lastMarble)

    fun solvePartTwo(): Int = solveBoard(players, lastMarble.times(100))

    private fun solveBoard(players: Int, lastMarble: Int): Int {
        val board = mutableListOf(0)
        val scoreBoard = mutableMapOf<Int, Int>() // Player ID to score
        var currentMarble = 0

        for (it in 1..lastMarble) {
            val player = it % players
            if (it != 0 && it.isMultipleOf(23)) {
                var pos = currentMarble - 7
                if (pos < 0) {
                    pos += board.size
                }
                scoreBoard[player] = scoreBoard.getOrDefault(player, 0) + board[pos] + it
                board.removeAt(pos)
                currentMarble = pos
            } else {
                val pos1 = currentMarble + 1

                currentMarble = when {
                    pos1 > board.lastIndex -> {
                        board.add(1, it)
                        1
                    }
                    pos1 == board.lastIndex -> {
                        board.add(it)
                        board.lastIndex
                    }
                    else -> {
                        board.add(pos1.inc(), it)
                        pos1.inc()
                    }
                }
            }
        }

        return scoreBoard.values.max().orZero()
    }
}