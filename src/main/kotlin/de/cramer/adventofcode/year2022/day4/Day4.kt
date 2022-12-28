package de.cramer.adventofcode.year2022.day4

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 2)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 4)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Pair<IntRange, IntRange>>): Int {
    return input.count {
        it.first in it.second || it.second in it.first
    }
}

private fun problem02(input: List<Pair<IntRange, IntRange>>): Int {
    return input.count {
        it.first isOverlapping it.second
    }
}

private fun String.parse(): List<Pair<IntRange, IntRange>> {
    return lineSequence()
        .map { it.split(",") }
        .map { (first, second) -> first.toRange() to second.toRange() }
        .toList()
}

private fun String.toRange(): IntRange {
    val (start, end) = split("-")
    return start.toInt()..end.toInt()
}

private operator fun IntRange.contains(other: IntRange): Boolean =
    other.first in this && other.last in this

private infix fun IntRange.isOverlapping(other: IntRange): Boolean =
    other.first in this || other.last in this || this.first in other || this.last in other
