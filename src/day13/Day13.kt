package day13

import utils.then
import java.io.File
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    println("Part one: ${Day13.solvePartOne()}")
    println("Parse two: ${Day13.solvePartTwo()}")
}

object Day13 {

    private val file = File("src/day13/input.txt")

    fun solvePartOne() {
        Network(file.readLines()).tick()
    }

    fun solvePartTwo(): String {
        return ""
    }
}

class Network(lines: List<String>) {

    val trackGrid = mutableMapOf<Pair<Int, Int>, Track>()
    val carts = mutableListOf<Cart>()
    var trackSize = 0

    init {
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, track ->
                val coord = Pair(x, y)

                val trackType = when (track) {
                    '-', '>', '<' -> TrackType.HORIZONTAL
                    '|', '^', 'v' -> TrackType.VERTICAL
                    '\\' -> TrackType.LEFT_CORNER
                    '/' -> TrackType.RIGHT_CORNER
                    '+' -> TrackType.INTERSECTION
                    else -> null
                }

                trackType?.let {
                    if (listOf('>', '<', '^', 'v').contains(track)) {
                        carts.add(Cart(x, y, track))
                    }

                    trackGrid[coord] = Track(x, y, it)
                }
                trackSize = maxOf(x, trackSize)
            }
        }
    }

    fun tick() {
        do {
            carts.filterNot { it.crashed }
                    .sortedBy { it.x + it.y *  trackSize}
                    .forEach { it.tick() }
        } while (carts.find { it.crashed } == null)

        val cart = carts.find { it.crashed }

        println("${cart!!.x},${cart.y}")
    }

    inner class Cart(var x: Int, var y: Int, cart: Char,
                     var nextIntersection: IntersectionTurn = IntersectionTurn.LEFT, var crashed: Boolean = false) {

        private var direction = when (cart) {
            '>' -> Direction.RIGHT
            '<' -> Direction.LEFT
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            else -> throw IllegalArgumentException("Invalid cart")
        }

        fun tick(): Boolean {
            if (crashed) return false

            when (direction) {
                Direction.RIGHT -> x++
                Direction.LEFT -> x--
                Direction.UP -> y--
                Direction.DOWN -> y++
            }

            when (trackGrid[Pair(x, y)]!!.type) {
                TrackType.INTERSECTION -> {
                    when (nextIntersection) {
                        IntersectionTurn.LEFT -> {
                            direction = when (direction) {
                                Direction.LEFT -> Direction.DOWN
                                Direction.RIGHT -> Direction.UP
                                Direction.UP -> Direction.LEFT
                                Direction.DOWN -> Direction.RIGHT
                            }
                            nextIntersection = IntersectionTurn.STRAIGHT
                        }
                        IntersectionTurn.STRAIGHT -> {
                            nextIntersection = IntersectionTurn.RIGHT
                        }
                        IntersectionTurn.RIGHT -> {
                            direction = when (direction) {
                                Direction.LEFT -> Direction.UP
                                Direction.RIGHT -> Direction.DOWN
                                Direction.UP -> Direction.RIGHT
                                Direction.DOWN -> Direction.LEFT
                            }
                            nextIntersection = IntersectionTurn.LEFT
                        }
                    }
                }
                TrackType.RIGHT_CORNER -> {
                    direction = when (direction) {
                        Direction.RIGHT -> Direction.UP
                        Direction.LEFT -> Direction.DOWN
                        Direction.UP -> Direction.RIGHT
                        Direction.DOWN -> Direction.LEFT
                    }
                }
                TrackType.LEFT_CORNER -> {
                    direction = when (direction) {
                        Direction.RIGHT -> Direction.DOWN
                        Direction.LEFT -> Direction.UP
                        Direction.UP -> Direction.LEFT
                        Direction.DOWN -> Direction.RIGHT
                    }
                }
            }

            carts.filter { it.x == x && it.y == y }
                    .takeIf { it.size > 1 }
                    ?.forEach {
                        println("Crash: $x, $y")
                        it.crashed = true
                    }?.let {
                        return false
                    }

            return true
        }
    }
}

enum class TrackType {
    HORIZONTAL, VERTICAL, LEFT_CORNER, RIGHT_CORNER, INTERSECTION
}

enum class Direction {
    UP, RIGHT, DOWN, LEFT
}

enum class IntersectionTurn {
    STRAIGHT, LEFT, RIGHT
}

class Track(val x: Int, val y: Int, val type: TrackType)