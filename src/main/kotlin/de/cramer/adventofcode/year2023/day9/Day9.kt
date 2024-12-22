package de.cramer.adventofcode.year2023.day9

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 114)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(lines: List<List<Int>>): Int {
    fun List<Int>.nextValue(): Int {
        val lastValues = mutableListOf<Int>()
        var currentSequence = this
        lastValues += currentSequence.last()
        while (!currentSequence.all { it == 0 }) {
            currentSequence = currentSequence.zipWithNext { a, b -> b - a }
            lastValues += currentSequence.last()
        }

        return lastValues.sum()
    }

    return lines.sumOf { it.nextValue() }
}

private fun problem02(lines: List<List<Int>>): Int {
    fun List<Int>.previousValue(): Int {
        val firstValues = mutableListOf<Int>()
        var currentSequence = this
        firstValues += currentSequence.first()
        while (!currentSequence.all { it == 0 }) {
            currentSequence = currentSequence.zipWithNext { a, b -> b - a }
            firstValues += currentSequence.first()
        }

        return firstValues.foldRight(0) { value, acc ->
            value - acc
        }
    }

    return lines.sumOf { it.previousValue() }
}

private fun String.parse(): List<List<Int>> {
    return lineSequence()
        .map { l -> l.split(" ").map { it.toInt() } }
        .toList()
}
