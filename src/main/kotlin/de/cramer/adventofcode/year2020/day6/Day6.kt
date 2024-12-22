package de.cramer.adventofcode.year2020.day6

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 11)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 6)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Set<Char>>>): Int {
    return input.sumOf { it.flatten().distinct().count() }
}

private fun problem02(input: List<List<Set<Char>>>): Int {
    return input.sumOf {
        it.reduce { acc, chars -> acc intersect chars }.size
    }
}

private fun String.parse(): List<List<Set<Char>>> {
    return splitByEmptyLines()
        .map { e -> e.lineSequence().map { it.toSet() }.toList() }
        .toList()
}
