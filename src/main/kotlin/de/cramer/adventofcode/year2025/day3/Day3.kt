package de.cramer.adventofcode.year2025.day3

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import kotlin.math.pow

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 357)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 3121910778619)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Bank>): Long {
    return input.getJoltage(2)
}

private fun problem02(input: List<Bank>): Long {
    return input.getJoltage(12)
}

@JvmName("getJoltageList")
private fun List<Bank>.getJoltage(numberOfBatteries: Int) = sumOf { it.getJoltage(numberOfBatteries) }

private fun Bank.getJoltage(numberOfBatteries: Int): Long {
    val digits = mutableListOf<Int>()
    var minIndex = 0
    for (i in numberOfBatteries - 1 downTo 0) {
        val possibleMaxValues = subList(minIndex, size).dropLast(i)
        val digit = possibleMaxValues.max()
        minIndex += possibleMaxValues.indexOf(digit) + 1
        digits += digit
    }
    digits.reverse()
    return digits.mapIndexed { index, digit -> digit * 10.0.pow(index).toLong() }.sum()
}

private typealias Bank = List<Int>

private fun String.parse(): List<Bank> {
    return lines().map { l -> l.toCharArray().map { it.digitToInt() } }
}
