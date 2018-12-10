package day10

import utils.then
import java.io.File

fun main(args: Array<String>) {
    Day10.runGrid()
}

object Day10 {

    private val data = File("src/day10/input.txt").readLines()
    private val pattern = """^position=<([\s|-]?\d+), ([\s|-]?\d+)> velocity=<([\s|-]?\d+), ([\s|-]?\d+)>${'$'}""".toRegex()

    fun runGrid() {
        val points = data.map {
            val (x, y, xVelo, yVelo) = pattern.find(it)!!.destructured
            Point(x.trim().toInt(), y.trim().toInt(), xVelo.trim().toInt(), yVelo.trim().toInt())
        }

        var seconds = 0

        while (true) {
            val grid = createGrid(points)
            val smallX = grid.minBy { it.key.first }!!.key.first
            val smallY = grid.minBy { it.key.second }!!.key.second
            val largeX = grid.maxBy { it.key.first }!!.key.first
            val largeY = grid.maxBy { it.key.second }!!.key.second
            if (largeY - smallY < 100 && largeX - smallX < 100) {
                println("Seconds: $seconds")
                printGrid(grid)
            }
            seconds++
            points.forEach { it.move() }
        }
    }

    private fun createGrid(points: List<Point>): Map<Pair<Int, Int>, List<Point>> {
        val grid = mutableMapOf<Pair<Int, Int>, MutableList<Point>>()
        points.forEach {
            val key = it.key()
            grid.getOrPut(key) { mutableListOf() }.add(it)
        }
        return grid
    }

    private fun printGrid(grid: Map<Pair<Int, Int>, List<Point>>) {
        val smallX = grid.minBy { it.key.first }!!.key.first
        val smallY = grid.minBy { it.key.second }!!.key.second
        val largeX = grid.maxBy { it.key.first }!!.key.first
        val largeY = grid.maxBy { it.key.second }!!.key.second

        for (y in smallY..largeY) {
            for (x in smallX..largeX) {
                print(grid.getOrDefault(Pair(x, y), emptyList()).isEmpty() then '.' ?: '#')
            }
            println()
        }
    }
}

class Point(var x: Int, var y: Int, val xVelo: Int, val yVelo: Int) {

    fun move() {
        x += xVelo
        y += yVelo
    }

    fun key(): Pair<Int, Int> = Pair(x, y)
}