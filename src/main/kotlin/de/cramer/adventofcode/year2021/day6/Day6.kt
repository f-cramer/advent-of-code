package de.cramer.adventofcode.year2021.day6

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 5934L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 26984457539L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Int>): Long {
    return input.simultatePopuplation(80)
}

private fun problem02(input: List<Int>): Long {
    return input.simultatePopuplation(256)
}

private fun List<Int>.simultatePopuplation(rounds: Int): Long {
    var fish = groupingBy { it }.eachCount().mapValues { (_, value) -> value.toLong() }
    repeat(rounds) {
        fish = buildMap {
            fish[0]?.let {
                this[6] = it
                this[8] = it
            }
            fish.keys.forEach {
                if (it != 0) merge(it - 1, fish[it]!!) { a, b -> a + b }
            }
        }
    }
    return fish.values.sum()
}

private fun String.parse(): List<Int> {
    return split(",").map { it.toInt() }
}
