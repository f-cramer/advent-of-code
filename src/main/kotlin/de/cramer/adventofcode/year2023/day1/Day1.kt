package de.cramer.adventofcode.year2023.day1

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 225)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 281)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<String>): Int {
    return input.sumOf { l -> 10 * l.first { it.isDigit() }.digitToInt() + l.last { it.isDigit() }.digitToInt() }
}

private val stringToDigitTranslator = buildMap {
    putAll(
        mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        ),
    )
    (0..9).forEach { this[it.digitToChar().toString()] = it }
}

private fun problem02(input: List<String>): Int {
    fun String.toLetter(): Int? = stringToDigitTranslator[this]

    return input.map { l ->
        val tens = l.indices.asSequence()
            .flatMap { start -> (start..<l.length).map { start..it } }
            .map { l.substring(it) }
            .mapNotNull { it.toLetter() }
            .first()

        val ones = l.indices.reversed().asSequence()
            .flatMap { endInclusive -> (endInclusive downTo 0).map { it..endInclusive } }
            .map { l.substring(it) }
            .mapNotNull { it.toLetter() }
            .first()

        10 * tens + ones
    }
        .onEach { println(it) }
        .sum()
}

private fun String.parse() = lines()
