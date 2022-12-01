package de.cramer.adventofcode.day1

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 24000)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 45000)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Long>) = input.max()

private fun problem02(input: List<Long>) = input.sortedDescending()
    .take(3).sum()

private fun String.parse() = splitByEmptyLines()
    .map { it.lineSequence().sumOf { i -> i.toLong() } }
