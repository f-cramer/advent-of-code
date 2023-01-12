package de.cramer.adventofcode.year2020.day9

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput, 5)
    checkTestResult(testResult01, 127)
    val testResult02 = problem02(testInput, 5)
    checkTestResult(testResult02, 62)

    val input = readInput().parse()
    runProblem01 { problem01(input, 25) }
    runProblem02 { problem02(input, 25) }
}

private fun problem01(input: List<Long>, preambelLength: Int): Long {
    return getInvalidNumber(input, preambelLength)
}

private fun problem02(input: List<Long>, preambelLength: Int): Long {
    val invalidNumber = getInvalidNumber(input, preambelLength)
    return input.indices
        .flatMap { start -> input.indices.mapNotNull { end -> if (start >= end - 1) null else start..end } }
        .map { input.slice(it) }
        .first { it.sum() == invalidNumber }
        .let {
            it.min() + it.max()
        }
}

private fun getInvalidNumber(numbers: List<Long>, preambelLength: Int): Long {
    return (preambelLength..numbers.lastIndex)
        .first { index ->
            val value = numbers[index]
            val values = ((index - preambelLength) until index).map { numbers[it] }
            values.flatMapIndexed { indexA, a -> values.mapIndexedNotNull { indexB, b -> if (indexA == indexB) null else a + b } }.none {
                it == value
            }
        }.let { numbers[it] }
}

private fun String.parse(): List<Long> {
    return lineSequence()
        .map { it.toLong() }
        .toList()
}
