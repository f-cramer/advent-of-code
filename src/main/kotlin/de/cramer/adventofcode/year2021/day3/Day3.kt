package de.cramer.adventofcode.year2021.day3

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 198)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 230)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    val size = input[0].size
    val gammaRate = (0 until size).mapNotNull { index ->
        input.asSequence()
            .map { it[index] }
            .mostCommon()
    }.toInt(2)
    val epsilonRate = (0 until size).mapNotNull { index ->
        input.asSequence()
            .map { it[index] }
            .leastCommon()
    }.toInt(2)
    return gammaRate * epsilonRate
}

private fun problem02(input: List<List<Int>>): Int {
    fun bitCriteria(selector: Sequence<Int>.() -> Int): Int {
        var values = input
        for (i in values[0].indices) {
            val neededValue = values.asSequence()
                .map { it[i] }
                .selector()
            values = values.filter { it[i] == neededValue }
            if (values.size == 1) {
                return values.first().toInt(2)
            }
        }

        error("could not calculate bit criteria")
    }

    val oxygenRating = bitCriteria { mostCommon() ?: 1 }
    val co2ScrubberRating = bitCriteria { leastCommon() ?: 0 }
    return oxygenRating * co2ScrubberRating
}

private fun String.parse(): List<List<Int>> {
    return lineSequence()
        .map { line -> line.map { it.digitToInt() } }
        .toList()
}

private fun <T> Sequence<T>.mostCommon(): T? = common { maxBy { it.value } }

private fun <T> Sequence<T>.leastCommon(): T? = common { minBy { it.value } }

private fun <T> Sequence<T>.common(selector: Map<T, Int>.() -> Map.Entry<T, Int>): T? {
    val counted = groupingBy { it }
        .eachCount()
    val value = counted
        .selector()
        .value
    val filtered = counted.filterValues { it == value }
    return filtered.keys.singleOrNull()
}

private fun List<Int>.toInt(radix: Int): Int {
    return joinToString(separator = "").toInt(radix)
}
