package day23

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    println("Part one: ${Day23.solvePartOne()}")
    println("Part two: ${Day23.solvePartTwo()}")
}

object Day23 {

    private val file = File("src/day23/input.txt")
    private val botPattern = """pos=<([-]?\d+),([-]?\d+),([-]?\d+)>, r=([-]?\d+)""".toRegex() //pos=<0,0,0>, r=4
    private val bots = mutableListOf<NanoBot>()

    init {
        parse()
    }

    fun solvePartOne(): Int {
        val largestRadius = bots.maxBy { it.radius }!!
        return bots.count { it.distance(largestRadius) <= largestRadius.radius }
    }

    fun solvePartTwo(): Int {
        return 0
    }

    private fun parse() {
        file.readLines().forEach {
            val (x, y, z, radius) = botPattern.find(it)!!.destructured
            bots.add(NanoBot(x.toInt(), y.toInt(), z.toInt(), radius.toInt()))
        }
    }
}

data class NanoBot(val x: Int, val y: Int, val z: Int, val radius: Int) {

    fun distance(otherBot: NanoBot): Int = abs(otherBot.x - x) + abs(otherBot.y - y) + abs(otherBot.z - z)
}