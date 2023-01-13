package de.cramer.adventofcode.year2020.day14

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 165)
//    val testResult02 = problem02(testInput)
//    checkTestResult(testResult02, 208)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Instruction>): Long {
    val computer = Computer(version = 1)
    input.forEach { it(computer) }
    return computer.memory.values.sum()
}

private fun problem02(input: List<Instruction>): Long {
    val computer = Computer(version = 2)
    input.forEach { it(computer) }
    return computer.memory.values.sum()
}

private fun String.parse(): List<Instruction> {
    val memory = """mem\[(\d+)] = (\d+)""".toRegex()

    fun String.toInstruction() = if (startsWith("mask")) {
        SetMaskInstruction(substringAfter("mask = "))
    } else {
        val (address, value) = memory.matchEntire(this)!!.destructured
        WriteMemoryInstruction(address.toLong(), value.toLong())
    }

    return lineSequence()
        .map { it.toInstruction() }
        .toList()
}

private data class Computer(
    val memory: MutableMap<Long, Long> = mutableMapOf(),
    var bitmask: String = "",
    val version: Int,
)

private sealed interface Instruction {
    operator fun invoke(computer: Computer)
}

private data class SetMaskInstruction(
    val mask: String,
) : Instruction {
    override fun invoke(computer: Computer) {
        computer.bitmask = mask
    }
}

private data class WriteMemoryInstruction(
    val address: Long,
    val value: Long,
) : Instruction {
    override fun invoke(computer: Computer) {
        when (computer.version) {
            1 -> computer.memory[address] = value.mask(computer.bitmask)
            2 -> {
                address.decode(computer.bitmask).forEach {
                    computer.memory[it] = value
                }
            }

            else -> error("cannot run mem for version ${computer.version}")
        }
    }
}

private fun Long.mask(mask: String): Long {
    return toString(2)
        .toExactLength(mask.length)
        .zip(mask) { d, m -> if (m == 'X') d else m }
        .joinToString(separator = "").toLong(2)
}

private fun Long.decode(mask: String): Sequence<Long> {
    val possibleCharsPerPosition = toString(2)
        .toExactLength(mask.length)
        .zip(mask) { d, m ->
            when (m) {
                '0' -> listOf(d)
                '1' -> listOf('1')
                'X' -> listOf('0', '1')
                else -> error("cannot happen")
            }
        }

    fun getStrings(index: Int = 0): Sequence<String> {
        if (index >= possibleCharsPerPosition.size) {
            return sequenceOf("")
        }

        val chars = possibleCharsPerPosition[index]
        return chars.asSequence()
            .flatMap { c -> getStrings(index + 1).map { c + it } }
    }

    return getStrings().map { it.toLong(2) }
}

private fun String.toExactLength(length: Int): String {
    return padStart(length, '0')
        .takeLast(length)
}
