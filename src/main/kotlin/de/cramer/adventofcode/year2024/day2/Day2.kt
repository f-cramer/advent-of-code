package de.cramer.adventofcode.year2024.day2

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

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

private fun problem01(input: List<Report>): Int {
    return input.count { it.isSafe() }
}

private fun problem02(input: List<Report>): Int {
    fun Report.isDampenedSafe(): Boolean {
        if (isSafe()) {
            return true
        }

        for (index in indices) {
            val copy = toMutableList()
            copy.removeAt(index)
            if (copy.isSafe()) {
                return true
            }
        }

        return false
    }

    return input.count { it.isDampenedSafe() }
}

private fun String.parse(): List<Report> {
    fun String.parseReport(): Report = split(" ").map { it.toInt() }

    return lineSequence()
        .map { it.parseReport() }
        .toList()
}

private typealias Report = List<Int>

private data class Aggregator(val direction: Int, val safe: Boolean, val first: Boolean = false)

private val unsafe = Aggregator(0, false)

private fun Report.isSafe(): Boolean = asSequence()
    .zipWithNext()
    .fold(Aggregator(0, safe = true, first = true)) { acc, (a, b) ->
        if (!acc.safe) {
            return@fold unsafe
        }

        val diff = b - a
        if (diff > 3 || diff < -3) {
            return@fold unsafe
        }

        val increasing = diff > 0
        val decreasing = diff < 0
        if (!increasing && !decreasing) {
            unsafe
        } else if (acc.first && (increasing && acc.direction < 0 || decreasing && acc.direction > 0)) {
            unsafe
        } else if (!acc.first && (increasing && acc.direction <= 0 || decreasing && acc.direction >= 0)) {
            unsafe
        } else {
            Aggregator(if (increasing) 1 else -1, true)
        }
    }.safe
