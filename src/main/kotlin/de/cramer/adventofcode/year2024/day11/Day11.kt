package de.cramer.adventofcode.year2024.day11

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 55312L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 65601038650482L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Int>): Long {
    return input.getStonesAfterBlinks(25)
}

private fun problem02(input: List<Int>): Long {
    return input.getStonesAfterBlinks(75)
}

private fun List<Int>.getStonesAfterBlinks(blinks: Int): Long {
    var countByNumber = groupingBy { it.toLong() }.fold(0L) { acc, _ -> acc + 1 }
    for (i in 0..<blinks) {
        countByNumber = countByNumber.asSequence()
            .flatMap { (number, count) ->
                if (number == 0L) {
                    listOf(1L to count)
                } else {
                    val numberString = number.toString()
                    if (numberString.length % 2 == 0) {
                        val left = numberString.substring(0, numberString.length / 2).toLong()
                        val right = numberString.substring(numberString.length / 2).toLong()
                        listOf(left to count, right to count)
                    } else {
                        val newNumber = number * 2024
                        listOf(newNumber to count)
                    }
                }
            }
            .groupingBy { (number) -> number }
            .fold(0L) { acc, (_, count) -> acc + count }
    }

    return countByNumber.values.sum()
}

private fun String.parse(): List<Int> {
    return split("""\s+""".toRegex()).map { it.toInt() }
}
