package day2

import utils.then
import java.io.File
import java.lang.IllegalStateException

fun main(args: Array<String>) {
    println("Part one: ${solvePartOne()}")
    println("Parse two: ${solvePartTwo()}")
}

fun solvePartOne(): Int {
    var appearsTwice = 0
    var appearsThrice = 0
    File("src/day2/input.txt").readLines().asSequence().map { it.groupingBy { it }.eachCount() }.forEach { group ->
        if (group.containsValue(2)) {
            appearsTwice++
        }
        if (group.containsValue(3)) {
            appearsThrice++
        }
    }
    return appearsThrice * appearsTwice
}

fun solvePartTwo(): String {
    val ids = File("src/day2/input.txt").readLines().asSequence()
    ids.forEach { idOne ->
        ids.forEach { idTwo ->
            val zipped = idOne.zip(idTwo)
            if (zipped.count { it.first == it.second } == idOne.count() - 1) {
                return zipped.map { (it.first == it.second) then it.first ?: "" }.joinToString(separator = "")
            }
        }
    }
    throw IllegalStateException("Fucked it")
}