package de.cramer.adventofcode.year2020.day1

import de.cramer.adventofcode.cartesianProduct
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 514579)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 241861950)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Long>): Long {
    return input.cartesianProduct(input)
        .first { (a, b) -> a + b == 2020L }
        .let { (a, b) -> a * b }
}

private fun problem02(input: List<Long>): Long {
    return input.cartesianProduct(input).cartesianProduct(input)
        .first { (ab, c) -> ab.first + ab.second + c == 2020L }
        .let { (ab, c) -> ab.first * ab.second * c }
}

private fun String.parse(): List<Long> {
    return lineSequence()
        .map { it.toLong() }
        .toList()
}
