package de.cramer.adventofcode.year2025.day2

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 1227775554)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 4174379265)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<LongRange>): Long {
    return input.sumInvalidIds { id ->
        val idString = id.toString()
        val numberOfDigits = idString.length
        if (numberOfDigits % 2 > 0) {
            return@sumInvalidIds false
        }
        val firstHalf = idString.substring(0, numberOfDigits / 2).toInt()
        val secondHalf = idString.substring(numberOfDigits / 2).toInt()
        firstHalf == secondHalf
    }
}

private fun problem02(input: List<LongRange>): Long {
    return input.sumInvalidIds { id ->
        val idString = id.toString()
        val numberOfDigits = idString.length
        (1..numberOfDigits / 2).any {
            if (numberOfDigits % it > 0) {
                return@any false
            }
            idString.windowed(it, it).allEqual()
        }
    }
}

private fun String.parse(): List<LongRange> {
    return split(",").map {
        val (start, end) = it.split("-")
        start.toLong()..end.toLong()
    }
}

private fun List<LongRange>.sumInvalidIds(isInvalid: (Long) -> Boolean) = sumOf { it.sumOf { id -> if (isInvalid(id)) id else 0 } }

private fun <T> Iterable<T>.allEqual() = firstOrNull()?.let { first -> all { it == first } } == true
