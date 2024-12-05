package de.cramer.adventofcode.year2024.day3

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 161)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 48)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Instruction>): Int {
    return input.sumOf {
        if (it is Multiplication) it.a * it.b else 0
    }
}

private fun problem02(input: List<Instruction>): Int {
    data class Result(val sum: Int, val enabled: Boolean)
    return input.fold(Result(0, true)) { acc, instruction ->
        when (instruction) {
            is Multiplication -> if (acc.enabled) acc.copy(sum = acc.sum + instruction.a * instruction.b) else acc
            is Enable -> acc.copy(enabled = true)
            is Disable -> acc.copy(enabled = false)
        }
    }.sum
}

private const val DO = "do()"
private const val DONT = "don't()"

private fun String.parse(): List<Instruction> {
    val pattern = """mul\((\d+),(\d+)\)|${Regex.escape(DO)}|${Regex.escape(DONT)}""".toRegex()
    val bytes = toByteArray()
    return pattern.findAll(this@parse)
        .map {
            val (instruction, f1, f2) = it.groupValues
            if (instruction == DO) {
                Enable
            } else if (instruction == DONT) {
                Disable
            } else {
                Multiplication(f1.toInt(), f2.toInt())
            }
        }
        .toList()
}

private sealed interface Instruction

private data class Multiplication(val a: Int, val b: Int) : Instruction

private data object Enable : Instruction

private data object Disable : Instruction
