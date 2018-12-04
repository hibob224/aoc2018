package day3

import java.io.File
import java.lang.IllegalArgumentException

object Day3 {

    fun main(args: Array<String>) {
        println("Part one: ${solvePartOne()}")
        println("Parse two: ${solvePartTwo()}")
    }

    fun solvePartOne(): Int {
        val claims = parseClaims()
        val board = hashMapOf<Pair<Int, Int>, Int>()

        claims.forEach {
            for (x in it.startX until (it.startX + it.width)) {
                for (y in it.startY until (it.startY + it.height)) {
                    board[Pair(x, y)] = (board[Pair(x, y)] ?: 0) + 1
                }
            }
        }

        return board.values.count { it >= 2 }
    }

    fun solvePartTwo(): Int {
        val claims = parseClaims()
        val board = hashMapOf<Pair<Int, Int>, MutableList<Claim>>()

        claims.forEach { claim ->
            for (x in claim.startX until (claim.startX + claim.width)) {
                for (y in claim.startY until (claim.startY + claim.height)) {
                    val list = board.getOrPut(Pair(x, y)) { mutableListOf() }
                    list.add(claim)
                    if (list.size > 1) {
                        list.forEach { it.overlap = true }
                    }
                }
            }
        }

        return claims.find { !it.overlap }?.id ?: -1
    }

    private val claimPattern = """^#(\d+) @ (\d+),(\d+): (\d+)x(\d+)$""".toRegex()

    /**
     * Parses the input file and creates a list of [Claim] for each row using regex to parse the required variables
     */
    private fun parseClaims(): List<Claim> {
        return File("src/day3/input.txt").readLines().map { line ->
            claimPattern.find(line)?.let {
                val (id, x, y, w, h) = it.destructured
                Claim(id.toInt(), x.toInt(), y.toInt(), w.toInt(), h.toInt())
            } ?: throw IllegalArgumentException("Couldn't parse claim $line")
        }
    }
}

data class Claim(val id: Int, val startX: Int, val startY: Int, val width: Int, val height: Int, var overlap: Boolean = false)