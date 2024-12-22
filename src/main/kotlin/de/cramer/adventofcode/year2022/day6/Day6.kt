package de.cramer.adventofcode.year2022.day6

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, listOf(7, 5, 6, 10, 11))
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, listOf(19, 23, 23, 29, 26))

    val input = readInput()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: String): List<Int> {
    return input.lines()
        .map { it.getStartOfHeader() }
}

private fun problem02(input: String): List<Int> {
    return input.lines()
        .map { it.getStartOfMessage() }
}

private fun String.getStartOfHeader(): Int = getFirstIndexOfUniqueCharacters(4)

private fun String.getStartOfMessage(): Int = getFirstIndexOfUniqueCharacters(14)

private fun String.getFirstIndexOfUniqueCharacters(numberOfUniqueCharacters: Int): Int {
    return numberOfUniqueCharacters + windowedSequence(numberOfUniqueCharacters, 1)
        .indexOfFirst { it.toSet().size == numberOfUniqueCharacters }
}
