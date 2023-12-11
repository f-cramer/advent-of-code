package de.cramer.adventofcode.year2020.day5

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 820)
//    val testResult02 = problem02(testInput)
//    checkTestResult(testResult02, Unit)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Char>>): Int {
    return input.maxOf { it.getPosition() }
}

private fun problem02(input: List<List<Char>>): Int {
    val allSeats = 0..<(128 * 8)
    val positions = input.map { it.getPosition() }
    return allSeats.first { it !in positions && (it - 1) in positions && (it + 1) in positions }
}

private fun String.parse(): List<List<Char>> {
    return lineSequence()
        .map { l -> l.toList() }
        .toList()
}

fun List<Char>.getPosition(): Int {
    var rows = 0..127
    for (i in 0..<7) {
        val char = this[i]
        rows = when (char) {
            'F' -> rows.first..<rows.first + rows.length / 2
            'B' -> (rows.last - rows.length / 2 + 1)..rows.last
            else -> error("found $char but expected F or B")
        }
    }
    var columns = 0..7
    for (i in 7..<10) {
        val char = this[i]
        columns = when (char) {
            'L' -> columns.first..<columns.first + columns.length / 2
            'R' -> (columns.last - columns.length / 2 + 1)..columns.last
            else -> error("found $char but expected L or R")
        }
    }
    return columns.first + rows.first * 8
}

private val IntRange.length: Int
    get() = last - first + 1
