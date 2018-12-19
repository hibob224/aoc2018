package day19

import utils.then
import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day19.solvePartOne()}")
    println("Parse two: ${Day19.solvePartTwo()}")
}

object Day19 {

    private val file = File("src/day19/input.txt")
    private val instructionPattern = """(\w+) (\d+) (\d+) (\d+)""".toRegex()
    private val opcodes = mapOf(
            "addr" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] + inp[op.b] },
            "addi" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] + op.b },
            "mulr" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] * inp[op.b] },
            "muli" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] * op.b },
            "banr" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] and inp[op.b] },
            "bani" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] and op.b },
            "borr" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] or inp[op.b] },
            "bori" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] or op.b },
            "setr" to { inp: IntArray, op: Instruction -> inp[op.c] = inp[op.a] },
            "seti" to { inp: IntArray, op: Instruction -> inp[op.c] = op.a },
            "gtir" to { inp: IntArray, op: Instruction -> inp[op.c] = (op.a > inp[op.b]) then 1 ?: 0 },
            "gtri" to { inp: IntArray, op: Instruction -> inp[op.c] = (inp[op.a] > op.b) then 1 ?: 0 },
            "gtrr" to { inp: IntArray, op: Instruction -> inp[op.c] = (inp[op.a] > inp[op.b]) then 1 ?: 0 },
            "eqir" to { inp: IntArray, op: Instruction -> inp[op.c] = (op.a == inp[op.b]) then 1 ?: 0 },
            "eqri" to { inp: IntArray, op: Instruction -> inp[op.c] = (inp[op.a] == op.b) then 1 ?: 0 },
            "eqrr" to { inp: IntArray, op: Instruction -> inp[op.c] = (inp[op.a] == inp[op.b]) then 1 ?: 0 })

    fun solvePartOne(): Int {
        return solve()
    }

    fun solvePartTwo(): Int {
        return solve(1)
    }

    private fun solve(reg: Int = 0): Int {
        val lines = file.readLines()
        val instructions = mutableListOf<Instruction>()

        (1 until lines.size).forEach {
            val (instruction, a, b, c) = instructionPattern.find(lines[it])!!.destructured
            instructions.add(Instruction(instruction, a.toInt(), b.toInt(), c.toInt()))
        }

        val ip = lines.first().split("#ip ")[1].toInt()
        val register = IntArray(6)
        register[0] = reg

        while (register[ip] in instructions.indices) {
            val instruction = instructions[register[ip]]
            opcodes[instruction.op]?.invoke(register, instruction)
            register[ip]++
        }

        return register.first()
    }
}


data class Instruction(val op: String, val a: Int, val b: Int, val c: Int)