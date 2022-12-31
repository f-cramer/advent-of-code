package de.cramer.adventofcode.year2021.day1

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 7)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 5)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Long>): Int {
    return input.asSequence()
        .zipWithNext { a, b -> a < b }
        .count { it }
}

private fun problem02(input: List<Long>): Int {
    return input.asSequence()
        .windowed(3) { (a, b, c) -> a + b + c }
        .zipWithNext { a, b -> a < b }
        .count { it }
}

private fun String.parse(): List<Long> {
    return lineSequence()
        .map { it.toLong() }
        .toList()
}
