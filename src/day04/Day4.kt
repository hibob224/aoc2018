package day04

import utils.orZero
import java.io.File

object Day4 {

    fun main(args: Array<String>) {
        println("Part one: ${solvePartOne()}")
        println("Parse two: ${solvePartTwo()}")
    }

    fun solvePartOne(): Int {
        return guardStats().entries.maxBy {
            it.value.values.sum()
        }?.let { it.key * (it.value.maxBy { it.value }?.key ?: 0) }.orZero()
    }

    fun solvePartTwo(): Int {
        return guardStats().map {
            it.key to it.value.maxBy { it.value }
        }.maxBy { it.second?.value.orZero() }?.let {
            it.first * it.second?.key.orZero()
        }.orZero()
    }

    private fun guardStats(): HashMap<Int, HashMap<Int, Int>> {
        val timeMap = hashMapOf<Int, HashMap<Int, Int>>()
        var onDutyGuard = -1
        var sleepMinute = -1

        readAndSortLogs().forEach {
            when (it.logType) {
                LogType.START_SHIFT -> {
                    onDutyGuard = it.guardId?.toInt() ?: throw IllegalArgumentException("Invalid guard id")
                }
                LogType.FALL_ASLEEP -> {
                    sleepMinute = it.min
                }
                LogType.WAKE_UP -> {
                    val guardStats = timeMap.getOrPut(onDutyGuard) { hashMapOf() }
                    for (m in sleepMinute until it.min) {
                        guardStats[m] = guardStats.getOrDefault(m, 0) + 1
                    }
                }
            }
        }

        return timeMap
    }

    private fun readAndSortLogs(): List<LogLine> {
        val pattern = """\[\d+-\d+-\d+ \d+:(\d+)] \D+(\d+)?""".toRegex()
        return File("src/day04/input.txt").readLines().sorted().map { line ->
            pattern.find(line)?.let {
                val (min, guardId) = it.destructured
                val logType = when {
                    guardId.isNotEmpty() -> LogType.START_SHIFT
                    line.contains("wakes up") -> LogType.WAKE_UP
                    line.contains("falls") -> LogType.FALL_ASLEEP
                    else -> throw IllegalArgumentException("Unknown log type")
                }
                LogLine(guardId, min.toInt(), logType)
            } ?: throw IllegalArgumentException("Couldn't parse $line")
        }
    }
}

data class LogLine(val guardId: String?, val min: Int, val logType: LogType)

enum class LogType { START_SHIFT, FALL_ASLEEP, WAKE_UP }