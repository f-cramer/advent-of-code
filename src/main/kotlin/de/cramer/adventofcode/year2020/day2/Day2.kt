package de.cramer.adventofcode.year2020.day2

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 2)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Input>): Int {
    return input.count {
        it.password.count { c -> c == it.policyChar } in it.policyRange
    }
}

private fun problem02(input: List<Input>): Int {
    return input.count {
        (it.password[it.policyRange.first - 1] == it.policyChar) xor (it.password[it.policyRange.last - 1] == it.policyChar)
    }
}

private fun String.parse(): List<Input> {
    fun String.toInput(): Input {
        val (rangeStart, rangeEnd, char, password) = split(": ", " ", "-")
        return Input(password, char.single(), rangeStart.toInt()..rangeEnd.toInt())
    }

    return lineSequence()
        .map { it.toInput() }
        .toList()
}

private data class Input(
    val password: String,
    val policyChar: Char,
    val policyRange: IntRange,
)
