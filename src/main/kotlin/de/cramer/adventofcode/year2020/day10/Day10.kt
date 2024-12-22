package de.cramer.adventofcode.year2020.day10

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 220)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 19208)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Int>): Int {
    val numbers = listOf(0) + input
    return numbers.zipWithNext { a, b -> b - a }
        .groupingBy { it }
        .eachCount()
        .let { it.getValue(1) * ((it[3] ?: 0) + 1) }
}

private fun problem02(input: List<Int>): Long {
    val cache: MutableMap<Int, MutableMap<List<Int>, Long>> = mutableMapOf()

    fun getPossibleArrangements(previous: Int, adapters: MutableList<Int>): Long {
        if (adapters.isEmpty()) {
            return 1L
        }

        var count = cache[previous]?.get(adapters)
        if (count != null) {
            return count
        }

        val nextAdapter = adapters.removeAt(0)
        count = getPossibleArrangements(nextAdapter, adapters)

        if (adapters.isNotEmpty() && adapters[0] - previous <= 3) {
            count += getPossibleArrangements(previous, adapters)
        }
        adapters.add(0, nextAdapter)

        cache[previous] = mutableMapOf(adapters to count)
        return count
    }

    return getPossibleArrangements(0, input.toMutableList())
}

private fun String.parse(): List<Int> {
    return lineSequence()
        .map { it.toInt() }
        .sorted()
        .toList()
}
