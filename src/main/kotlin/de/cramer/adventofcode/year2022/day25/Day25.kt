package de.cramer.adventofcode.year2022.day25

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runTest
import kotlin.math.pow

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, "2=-1=0")

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
}

private fun problem01(input: List<Long>): String {
    return input.sum().toSnafuNumber()
}

private fun Long.toSnafuNumber(): String {
    fun Long.toSnafuDigit() = when (this) {
        0L, 1L, 2L -> toInt().digitToChar()
        3L -> '='
        4L -> '-'
        else -> error("cannot convert $this to snafu digit")
    }

    return generateSequence(this) { (it + 2) / 5 }
        .takeWhile { it != 0L }
        .map { it % 5 }
        .map { it.toSnafuDigit() }
        .joinToString(separator = "")
        .reversed()
}

private fun String.parse(): List<Long> {
    fun Char.toFactor(): Int = when (this) {
        '2', '1', '0' -> digitToInt()
        '-' -> -1
        '=' -> -2
        else -> error("cannot parse $this to factor")
    }

    fun String.snafuNumberToLong(): Long = reversed()
        .mapIndexed { index, c -> 5.0.pow(index).toLong() * c.toFactor() }
        .sum()

    return lineSequence()
        .map { it.snafuNumberToLong() }
        .toList()
}
