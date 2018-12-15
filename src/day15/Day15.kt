package day15

import utils.then
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

fun main(args: Array<String>) {
    println("Part one: ${Day15.solvePartOne()}")
    println("Parse two: ${Day15.solvePartTwo()}")
}

object Day15 {

    private val file = File("src/day15/input.txt")

    fun solvePartOne(): String {
        val battleField = mutableListOf<MutableList<Tile>>()

        file.readLines().forEachIndexed { y, row ->
            val rowList = mutableListOf<Tile>()
            row.forEachIndexed { x, it ->
                rowList.add(when (it) {
                    '.' -> Empty(x, y)
                    '#' -> Wall(x, y)
                    'G' -> Unit(x, y, Faction.GOBLIN)
                    'E' -> Unit(x, y, Faction.ELF)
                    else -> throw IllegalArgumentException("Unknown tile type")
                })
            }
            battleField.add(rowList)
        }

        var rounds = 0

        while (battleField.flatMap { it.filter { it is Unit && it.faction == Faction.ELF } }.isNotEmpty()) {
            rounds++

            val units = battleField.flatMap { row -> row.filter { it is Unit } }
            units.forEach { unit ->
                unit as Unit
                if (unit.hitPoints <= 0) return@forEach // Skip unit, it dead
                // Check every space around us
                val surrounding = listOf(
                        battleField[unit.y - 1][unit.x],
                        battleField[unit.y][unit.x - 1],
                        battleField[unit.y][unit.x + 1],
                        battleField[unit.y + 1][unit.x])

                // Attempt an attack if we're already within range
                surrounding
                        .filter { it is Unit && unit.faction.enemy() == it.faction }
                        .takeIf { it.isNotEmpty() }
                        ?.let {
                            val lowestHp = (it.minBy { (it as Unit).hitPoints } as Unit).hitPoints
                            it.filter { (it as Unit).hitPoints == lowestHp }
                        }
                        ?.sortedBy { it.x + it.y * battleField.first().size }
                        ?.firstOrNull()
                        ?.let {
                            it as Unit
                            it.hitPoints -= unit.attackPower
                            if (it.hitPoints <= 0) {
                                battleField[it.y][it.x] = Empty(it.x, it.y)
                            }
                            return@forEach
                        }

                // Make a move instead
                val enemies = battleField.flatMap { it.filter { it is Unit && unit.faction.enemy() == it.faction } }
                val leesGrid = (0..battleField.size).map { (0..battleField.first().size).map { -1 }.toMutableList() }
                var exitLoop = false
                leesGrid[unit.y][unit.x] = 0
                val waveFront = mutableListOf(Pair(unit.x, unit.y))
                val returnTargets = mutableMapOf<Pair<Int, Int>, Boolean>()

                if (enemies.isEmpty()) {
                    return@forEach
                }

                while (!exitLoop) {
                    val newFront = mutableListOf<Pair<Int, Int>>()

                    waveFront.forEach {
                        val currentVal = leesGrid[it.second][it.first]

                        val tileAbove = battleField[it.second.dec()][it.first]
                        if ((tileAbove.canStepOn || (tileAbove is Unit && tileAbove.faction.enemy() == unit.faction))
                                && leesGrid[it.second.dec()][it.first] == -1) {
                            leesGrid[it.second.dec()][it.first] = currentVal.inc()
                            newFront.add(Pair(it.first, it.second.dec()))
                        }

                        val tileBelow = battleField[it.second.inc()][it.first]
                        if ((tileBelow.canStepOn || (tileBelow is Unit && tileBelow.faction.enemy() == unit.faction))
                                && leesGrid[it.second.inc()][it.first] == -1) {
                            leesGrid[it.second.inc()][it.first] = currentVal.inc()
                            newFront.add(Pair(it.first, it.second.inc()))
                        }

                        val tileLeft = battleField[it.second][it.first.dec()]
                        if ((tileLeft.canStepOn || (tileLeft is Unit && tileLeft.faction.enemy() == unit.faction))
                                && leesGrid[it.second][it.first.dec()] == -1) {
                            leesGrid[it.second][it.first.dec()] = currentVal.inc()
                            newFront.add(Pair(it.first.dec(), it.second))
                        }

                        val tileRight = battleField[it.second][it.first.inc()]
                        if ((tileRight.canStepOn || (tileRight is Unit && tileRight.faction.enemy() == unit.faction))
                                && leesGrid[it.second][it.first.inc()] == -1) {
                            leesGrid[it.second][it.first.inc()] = currentVal.inc()
                            newFront.add(Pair(it.first.inc(), it.second))
                        }
                    }

                    waveFront.clear()
                    waveFront.addAll(newFront)

                    if (returnTargets.isEmpty()) {
                        newFront.forEach { returnTargets[it] = false }
                    }

                    exitLoop = enemies.any { newFront.contains(Pair(it.x, it.y)) || newFront.isEmpty() }
                }

                val targetEnemy = battleField.flatMap {
                    it.filter { it is Unit && unit.faction.enemy() == it.faction }
                }.filter { waveFront.contains(Pair(it.x, it.y)) }.minBy { it.x + it.y * battleField.first().size }

                targetEnemy?.let {
                    // Track back
                    val trackBackPositions = mutableListOf<Pair<Int, Int>>().also { it.add(Pair(targetEnemy.x, targetEnemy.y)) }

                    while (!returnTargets.any { it.value }) {
                        val newPositions = mutableListOf<Pair<Int, Int>>()
                        trackBackPositions.forEach {
                            val targetValue = leesGrid[it.second][it.first].dec()
                            if (leesGrid[it.second.dec()][it.first] == targetValue) {
                                newPositions.add(Pair(it.first, it.second.dec()))
                            }
                            if (leesGrid[it.second.inc()][it.first] == targetValue) {
                                newPositions.add(Pair(it.first, it.second.inc()))
                            }
                            if (leesGrid[it.second][it.first.dec()] == targetValue) {
                                newPositions.add(Pair(it.first.dec(), it.second))
                            }
                            if (leesGrid[it.second][it.first.inc()] == targetValue) {
                                newPositions.add(Pair(it.first.inc(), it.second))
                            }
                        }

                        newPositions.forEach {
                            if (returnTargets.keys.contains(it)) {
                                returnTargets[it] = true
                            }
                        }

                        trackBackPositions.clear()
                        trackBackPositions.addAll(newPositions)
                    }

                    val nextPosition = returnTargets.filter { it.value }
                            .keys
                            .minBy { it.first + it.second * battleField.first().size }!!

                    battleField[nextPosition.second][nextPosition.first] = unit
                    battleField[unit.y][unit.x] = Empty(unit.x, unit.y)
                    unit.x = nextPosition.first
                    unit.y = nextPosition.second

                    val movedSurrounding = listOf(
                            battleField[unit.y - 1][unit.x],
                            battleField[unit.y][unit.x - 1],
                            battleField[unit.y][unit.x + 1],
                            battleField[unit.y + 1][unit.x])

                    // Attempt an attack if we're already within range
                    movedSurrounding
                            .filter { it is Unit && unit.faction.enemy() == it.faction }
                            .takeIf { it.isNotEmpty() }
                            ?.let {
                                val lowestHp = (it.minBy { (it as Unit).hitPoints } as Unit).hitPoints
                                it.filter { (it as Unit).hitPoints == lowestHp }
                            }
                            ?.sortedBy { it.x + it.y * battleField.first().size }
                            ?.firstOrNull()
                            ?.let {
                                it as Unit
                                it.hitPoints -= unit.attackPower
                                if (it.hitPoints <= 0) {
                                    battleField[it.y][it.x] = Empty(it.x, it.y)
                                }
                                return@forEach
                            }
                }
            }
        }

        val units = battleField.flatMap { it.filter { it is Unit } } as List<Unit>
        val goblinHealth = units.filter { it.faction == Faction.GOBLIN }.sumBy { it.hitPoints }
        val elfHealth = units.filter { it.faction == Faction.ELF }.sumBy { it.hitPoints }
        return (rounds.dec() * maxOf(goblinHealth, elfHealth)).toString()
    }

    fun solvePartTwo(): String {
        return solveTwo(4)
    }

    private fun solveTwo(power: Int): String {
        val battleField = mutableListOf<MutableList<Tile>>()

        file.readLines().forEachIndexed { y, row ->
            val rowList = mutableListOf<Tile>()
            row.forEachIndexed { x, it ->
                rowList.add(when (it) {
                    '.' -> Empty(x, y)
                    '#' -> Wall(x, y)
                    'G' -> Unit(x, y, Faction.GOBLIN)
                    'E' -> Unit(x, y, Faction.ELF, power)
                    else -> throw IllegalArgumentException("Unknown tile type")
                })
            }
            battleField.add(rowList)
        }

        var rounds = 0

        while (battleField.flatMap { it.filter { it is Unit && it.faction == Faction.GOBLIN } }.isNotEmpty()) {
            rounds++

            val units = battleField.flatMap { row -> row.filter { it is Unit } }
            units.forEach { unit ->
                unit as Unit
                if (unit.hitPoints <= 0) return@forEach // Skip unit, it dead
                // Check every space around us
                val surrounding = listOf(
                        battleField[unit.y - 1][unit.x],
                        battleField[unit.y][unit.x - 1],
                        battleField[unit.y][unit.x + 1],
                        battleField[unit.y + 1][unit.x])

                // Attempt an attack if we're already within range
                surrounding
                        .filter { it is Unit && unit.faction.enemy() == it.faction }
                        .takeIf { it.isNotEmpty() }
                        ?.let {
                            val lowestHp = (it.minBy { (it as Unit).hitPoints } as Unit).hitPoints
                            it.filter { (it as Unit).hitPoints == lowestHp }
                        }
                        ?.sortedBy { it.x + it.y * battleField.first().size }
                        ?.firstOrNull()
                        ?.let {
                            it as Unit
                            it.hitPoints -= unit.attackPower
                            if (it.hitPoints <= 0) {
                                if (it.faction == Faction.ELF) {
                                    return solveTwo(power.inc())
                                }
                                battleField[it.y][it.x] = Empty(it.x, it.y)
                            }
                            return@forEach
                        }

                // Make a move instead
                val enemies = battleField.flatMap { it.filter { it is Unit && unit.faction.enemy() == it.faction } }
                val leesGrid = (0..battleField.size).map { (0..battleField.first().size).map { -1 }.toMutableList() }
                var exitLoop = false
                leesGrid[unit.y][unit.x] = 0
                val waveFront = mutableListOf(Pair(unit.x, unit.y))
                val returnTargets = mutableMapOf<Pair<Int, Int>, Boolean>()

                if (enemies.isEmpty()) {
                    return@forEach
                }

                while (!exitLoop) {
                    val newFront = mutableListOf<Pair<Int, Int>>()

                    waveFront.forEach {
                        val currentVal = leesGrid[it.second][it.first]

                        val tileAbove = battleField[it.second.dec()][it.first]
                        if ((tileAbove.canStepOn || (tileAbove is Unit && tileAbove.faction.enemy() == unit.faction))
                                && leesGrid[it.second.dec()][it.first] == -1) {
                            leesGrid[it.second.dec()][it.first] = currentVal.inc()
                            newFront.add(Pair(it.first, it.second.dec()))
                        }

                        val tileBelow = battleField[it.second.inc()][it.first]
                        if ((tileBelow.canStepOn || (tileBelow is Unit && tileBelow.faction.enemy() == unit.faction))
                                && leesGrid[it.second.inc()][it.first] == -1) {
                            leesGrid[it.second.inc()][it.first] = currentVal.inc()
                            newFront.add(Pair(it.first, it.second.inc()))
                        }

                        val tileLeft = battleField[it.second][it.first.dec()]
                        if ((tileLeft.canStepOn || (tileLeft is Unit && tileLeft.faction.enemy() == unit.faction))
                                && leesGrid[it.second][it.first.dec()] == -1) {
                            leesGrid[it.second][it.first.dec()] = currentVal.inc()
                            newFront.add(Pair(it.first.dec(), it.second))
                        }

                        val tileRight = battleField[it.second][it.first.inc()]
                        if ((tileRight.canStepOn || (tileRight is Unit && tileRight.faction.enemy() == unit.faction))
                                && leesGrid[it.second][it.first.inc()] == -1) {
                            leesGrid[it.second][it.first.inc()] = currentVal.inc()
                            newFront.add(Pair(it.first.inc(), it.second))
                        }
                    }

                    waveFront.clear()
                    waveFront.addAll(newFront)

                    if (returnTargets.isEmpty()) {
                        newFront.forEach { returnTargets[it] = false }
                    }

                    exitLoop = enemies.any { newFront.contains(Pair(it.x, it.y)) || newFront.isEmpty() }
                }

                val targetEnemy = battleField.flatMap {
                    it.filter { it is Unit && unit.faction.enemy() == it.faction }
                }.filter { waveFront.contains(Pair(it.x, it.y)) }.minBy { it.x + it.y * battleField.first().size }

                targetEnemy?.let {
                    // Track back
                    val trackBackPositions = mutableListOf<Pair<Int, Int>>().also { it.add(Pair(targetEnemy.x, targetEnemy.y)) }

                    while (!returnTargets.any { it.value }) {
                        val newPositions = mutableListOf<Pair<Int, Int>>()
                        trackBackPositions.forEach {
                            val targetValue = leesGrid[it.second][it.first].dec()
                            if (leesGrid[it.second.dec()][it.first] == targetValue) {
                                newPositions.add(Pair(it.first, it.second.dec()))
                            }
                            if (leesGrid[it.second.inc()][it.first] == targetValue) {
                                newPositions.add(Pair(it.first, it.second.inc()))
                            }
                            if (leesGrid[it.second][it.first.dec()] == targetValue) {
                                newPositions.add(Pair(it.first.dec(), it.second))
                            }
                            if (leesGrid[it.second][it.first.inc()] == targetValue) {
                                newPositions.add(Pair(it.first.inc(), it.second))
                            }
                        }

                        newPositions.forEach {
                            if (returnTargets.keys.contains(it)) {
                                returnTargets[it] = true
                            }
                        }

                        trackBackPositions.clear()
                        trackBackPositions.addAll(newPositions)
                    }

                    val nextPosition = returnTargets.filter { it.value }
                            .keys
                            .minBy { it.first + it.second * battleField.first().size }!!

                    battleField[nextPosition.second][nextPosition.first] = unit
                    battleField[unit.y][unit.x] = Empty(unit.x, unit.y)
                    unit.x = nextPosition.first
                    unit.y = nextPosition.second

                    val movedSurrounding = listOf(
                            battleField[unit.y - 1][unit.x],
                            battleField[unit.y][unit.x - 1],
                            battleField[unit.y][unit.x + 1],
                            battleField[unit.y + 1][unit.x])

                    // Attempt an attack if we're already within range
                    movedSurrounding
                            .filter { it is Unit && unit.faction.enemy() == it.faction }
                            .takeIf { it.isNotEmpty() }
                            ?.let {
                                val lowestHp = (it.minBy { (it as Unit).hitPoints } as Unit).hitPoints
                                it.filter { (it as Unit).hitPoints == lowestHp }
                            }
                            ?.sortedBy { it.x + it.y * battleField.first().size }
                            ?.firstOrNull()
                            ?.let {
                                it as Unit
                                it.hitPoints -= unit.attackPower
                                if (it.hitPoints <= 0) {
                                    if (it.faction == Faction.ELF) {
                                        return solveTwo(power.inc())
                                    }
                                    battleField[it.y][it.x] = Empty(it.x, it.y)
                                }
                                return@forEach
                            }
                }
            }
        }

        printBattleField(battleField, rounds)
        val units = battleField.flatMap { it.filter { it is Unit } } as List<Unit>
        val elfHealth = units.filter { it.faction == Faction.ELF }.sumBy { it.hitPoints }

        return (rounds.dec() * elfHealth).toString()
    }

    private fun printBattleField(battleField: MutableList<MutableList<Tile>>, rounds: Int) {
        println("Round: $rounds")
        battleField.forEach {
            it.forEach {
                print(it.toString())
            }
            print(" " + (it.filter { it is Unit } as List<Unit>).joinToString { "$it (${it.hitPoints})" })
            println()
        }

        println()
        println()
    }
}

abstract class Tile(var x: Int, var y: Int, val canStepOn: Boolean = false)

class Empty(x: Int, y: Int) : Tile(x, y, true) {

    override fun toString(): String {
        return "."
    }
}

class Wall(x: Int, y: Int) : Tile(x, y) {

    override fun toString(): String {
        return "#"
    }
}

class Unit(x: Int, y: Int, val faction: Faction, val attackPower: Int = 3, var hitPoints: Int = 200) : Tile(x, y) {

    override fun toString(): String {
        return (faction == Faction.ELF) then "E" ?: "G"
//        return "$faction - [$x, $y] - $hitPoints hp"
    }
}

enum class Faction {
    GOBLIN, ELF;

    fun enemy(): Faction {
        return when (this) {
            GOBLIN -> ELF
            ELF -> GOBLIN
        }
    }
}