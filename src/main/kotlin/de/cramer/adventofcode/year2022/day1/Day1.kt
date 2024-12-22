package de.cramer.adventofcode.year2022.day1

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

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
