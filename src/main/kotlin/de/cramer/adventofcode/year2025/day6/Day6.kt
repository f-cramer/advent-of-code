package de.cramer.adventofcode.year2025.day6

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 4277556)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 3263827)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Problem>): Long {
    return input.sumOf {
        val numbers = it.numbers.map { s -> s.trim().toLong() }
        when (it.operator) {
            Operator.ADD -> numbers.sum()
            Operator.MULTIPLY -> numbers.reduce { acc, l -> acc * l }
        }
    }
}

private fun problem02(input: List<Problem>): Long {
    return input.sumOf {
        val numbers = it.numbers.first().indices.map { index ->
            it.numbers.map { n -> n[index] }.joinToString(separator = "").trim().toLong()
        }
        when (it.operator) {
            Operator.ADD -> numbers.sum()
            Operator.MULTIPLY -> numbers.reduce { acc, l -> acc * l }
        }
    }
}

private fun String.parse(): List<Problem> {
    val lines = lines()
    val numberLines = lines.subList(0, lines.size - 1)
    val operatorLine = lines.last()
    return buildList {
        val lineLength = lines.maxOf { it.length }
        var operator: Operator? = null
        var numbers: MutableList<MutableList<Char>>? = null

        fun addProblem() {
            if (operator != null && numbers != null) {
                val nonNullNumbers = numbers!!
                this += Problem(nonNullNumbers.map { it.joinToString(separator = "") { c -> c.toString() } }, operator!!)
            }
        }

        for (index in 0..<lineLength) {
            @Suppress("KotlinConstantConditions")
            if (operator == null || numbers == null) {
                operator = when (operatorLine[index]) {
                    '+' -> Operator.ADD
                    '*' -> Operator.MULTIPLY
                    else -> error("invalid operator")
                }
                numbers = mutableListOf()
            }

            val chars = numberLines.map { it[index] }
            if (chars.all { it == ' ' }) {
                addProblem()
                operator = null
                numbers = null
            } else if (numbers.isEmpty()) {
                numbers.addAll(chars.map { mutableListOf(it) })
            } else {
                chars.forEachIndexed { i, c -> numbers[i].add(c) }
            }
        }

        addProblem()
    }
}

private enum class Operator {
    ADD,
    MULTIPLY,
}

private data class Problem(
    val numbers: List<String>,
    val operator: Operator,
)
