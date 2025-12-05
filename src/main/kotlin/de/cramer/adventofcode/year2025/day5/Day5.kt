package de.cramer.adventofcode.year2025.day5

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 14)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Database): Int {
    return input.availableIngredients.count {
        input.freshIngredientRanges.any { range ->
            it in range
        }
    }
}

private fun problem02(input: Database): Long {
    val minimizedRanges = buildList {
        var currentRange: LongRange? = null
        for (range in input.freshIngredientRanges.sortedBy { it.first }) {
            if (currentRange == null) {
                currentRange = range
            } else if (range.first in currentRange || range.last in currentRange) {
                currentRange = minOf(currentRange.first, range.first)..maxOf(currentRange.last, range.last)
            } else {
                add(currentRange)
                currentRange = range
            }
        }

        if (currentRange != null) add(currentRange)
    }

    return minimizedRanges.sumOf { it.last - it.first + 1 }
}

private fun String.parse(): Database {
    val (ranges, available) = splitByEmptyLines()
    val freshIngredientRanges = ranges.lines()
        .map { it.split("-").map { i -> i.toLong() } }
        .map { (first, last) -> first..last }
    val availableIngredients = available.lines().map { it.toLong() }
    return Database(freshIngredientRanges, availableIngredients)
}

private data class Database(
    val freshIngredientRanges: List<LongRange>,
    val availableIngredients: List<Long>,
)
