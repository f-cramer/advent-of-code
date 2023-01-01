package de.cramer.adventofcode.year2021.day7

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 37)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 168)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Int>): Int {
    return (input.min()..input.max()).minOf { position ->
        input.sumOf { abs(it - position) }
    }
}

private fun problem02(input: List<Int>): Int {
    fun Int.fuelConsumption(): Int = this * (this + 1) / 2

    return (input.min()..input.max()).minOf { position ->
        input.sumOf { abs(it - position).fuelConsumption() }
    }
}

private fun String.parse(): List<Int> {
    return split(",").map { it.toInt() }
}
