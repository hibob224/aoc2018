package day16

import utils.then
import java.io.File

fun main(args: Array<String>) {
    println("Part one: ${Day16.solvePartOne()}")
    println("Parse two: ${Day16.solvePartTwo()}")
}

private val pattern = """\[(\d+), (\d+), (\d+), (\d+)\]""".toRegex()
private val opPattern = """(\d+) (\d+) (\d+) (\d+)""".toRegex()
private val opcodes = mapOf(
        "addr" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] + inp[op.b] },
        "addi" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] + op.b },
        "mulr" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] * inp[op.b] },
        "muli" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] * op.b },
        "banr" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] and inp[op.b] },
        "bani" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] and op.b },
        "borr" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] or inp[op.b] },
        "bori" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] or op.b },
        "setr" to { inp: Registers, op: Registers -> inp[op.c] = inp[op.a] },
        "seti" to { inp: Registers, op: Registers -> inp[op.c] = op.a },
        "gtir" to { inp: Registers, op: Registers -> inp[op.c] = (op.a > inp[op.b]) then 1 ?: 0 },
        "gtri" to { inp: Registers, op: Registers -> inp[op.c] = (inp[op.a] > op.b) then 1 ?: 0 },
        "gtrr" to { inp: Registers, op: Registers -> inp[op.c] = (inp[op.a] > inp[op.b]) then 1 ?: 0 },
        "eqir" to { inp: Registers, op: Registers -> inp[op.c] = (op.a == inp[op.b]) then 1 ?: 0 },
        "eqri" to { inp: Registers, op: Registers -> inp[op.c] = (inp[op.a] == op.b) then 1 ?: 0 },
        "eqrr" to { inp: Registers, op: Registers -> inp[op.c] = (inp[op.a] == inp[op.b]) then 1 ?: 0 })

object Day16 {

    private val file = File("src/day16/sample.txt")

    fun solvePartOne(): Int {
        val lines = file.readLines().toMutableList()
        val sampleOps = mutableListOf<Sample>()
        var index = 0

        while (index + 4 < lines.size) {
            sampleOps.add(Sample(lines.subList(index, index + 4)))
            index += 4
        }

        val samples = IntArray(sampleOps.size)
        sampleOps.forEachIndexed { index, sample ->
            opcodes.forEach { _, func ->
                if (sample.testSample(func)) {
                    samples[index] += 1
                }
            }
        }

        return samples.count { it >= 3 }
    }

    fun solvePartTwo(): Int {
        val lines = file.readLines().toMutableList()
        val sampleOps = mutableListOf<Sample>()
        val validOptions = mutableMapOf<Int, MutableMap<String, Boolean>>()
        var index = 0

        while (index + 4 < lines.size) {
            sampleOps.add(Sample(lines.subList(index, index + 4)))
            index += 4
        }

        // Work out what all the options are for each op number
        sampleOps.forEach { sample ->
            opcodes.filter {
                validOptions.getOrPut(sample.op.op) { mutableMapOf() }.getOrDefault(it.key, true)
            }.forEach { opString, func ->
                validOptions.getOrPut(sample.op.op) { mutableMapOf() }[opString] = sample.testSample(func)
            }
        }

        // Create mapping of int to operation
        val opMapping = mutableMapOf<Int, String>().also { map ->
            // Keep looping until we've mapped every int code. Each loop the numbers with only 1 option will be added
            while (map.size < validOptions.size) {
                validOptions.filter { it.value.count { it.value } == 1 }.forEach {
                    val opString = it.value.entries.find { it.value }!!.key
                    map[it.key] = opString
                    validOptions.forEach { it.value[opString] = false }
                }
            }
        }

        // Now run all the operations on [0, 0, 0, 0]
        val inputLines = File("src/day16/input.txt").readLines()
        val instructions = inputLines.map {
            val (op, a, b, c) = opPattern.find(it)!!.destructured
            Registers(op.toInt(), a.toInt(), b.toInt(), c.toInt())
        }

        val inp = Registers(0, 0, 0, 0)
        instructions.forEach { opcodes[opMapping[it.op]]?.invoke(inp, it) }
        return inp.op
    }
}

class Sample(lines: List<String>) {
    val inp: Registers
    val op: Registers
    val out: Registers

    init {
        val (opInp, aInp, bInp, cInp) = pattern.find(lines[0])!!.destructured
        inp = Registers(opInp.toInt(), aInp.toInt(), bInp.toInt(), cInp.toInt())
        val (opOp, aOp, bOp, cOp) = opPattern.find(lines[1])!!.destructured
        op = Registers(opOp.toInt(), aOp.toInt(), bOp.toInt(), cOp.toInt())
        val (opOut, aOut, bOut, cOut) = pattern.find(lines[2])!!.destructured
        out = Registers(opOut.toInt(), aOut.toInt(), bOut.toInt(), cOut.toInt())
    }

    /**
     * Returns true if running [sampleFunction] on [inp] and [op] results in [out]
     */
    fun testSample(sampleFunction: (Registers, Registers) -> Unit): Boolean {
        val inpCopy = Registers(inp)
        sampleFunction.invoke(inpCopy, op)
        return inpCopy == out
    }
}

data class Registers(var op: Int, var a: Int, var b: Int, var c: Int) {

    constructor(reg: Registers) : this(reg.op, reg.a, reg.b, reg.c)

    operator fun get(i: Int): Int {
        return when (i) {
            0 -> op
            1 -> a
            2 -> b
            3 -> c
            else -> throw IllegalArgumentException("Out-of-bounds")
        }
    }

    operator fun set(i: Int, value: Int) {
        when (i) {
            0 -> op = value
            1 -> a = value
            2 -> b = value
            3 -> c = value
        }
    }
}