package de.cramer.adventofcode.year2021.day4

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.set
import de.cramer.adventofcode.utils.vector.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 4512)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1924)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Bingo): Int {
    val crosses = input.fields.map { f -> f.map { l -> l.map { false }.toMutableList() } }
    for (number in input.numbers) {
        for ((fieldIndex, field) in input.fields.withIndex()) {
            val vector = field.find(number) ?: continue
            val fieldCrosses = crosses[fieldIndex]
            fieldCrosses[vector] = true
            if (fieldCrosses.isDone()) {
                return number * field.unmarkedSum(fieldCrosses)
            }
        }
    }

    error("could not find board score")
}

private fun problem02(input: Bingo): Int {
    val crosses = input.fields.map { f -> f.map { l -> l.map { false }.toMutableList() } }
    val doneFields = crosses.map { false }.toMutableList()
    for (number in input.numbers) {
        for ((fieldIndex, field) in input.fields.withIndex()) {
            val vector = field.find(number) ?: continue
            val fieldCrosses = crosses[fieldIndex]
            fieldCrosses[vector] = true
            if (!doneFields[fieldIndex] && fieldCrosses.isDone()) {
                doneFields[fieldIndex] = true
                if (doneFields.all { it }) {
                    return number * field.unmarkedSum(fieldCrosses)
                }
            }
        }
    }

    error("could not find board score")
}

private fun String.parse(): Bingo {
    val splitResult = splitByEmptyLines()
    val numbers = splitResult[0].split(",").map { it.toInt() }
    val fields = splitResult.subList(1, splitResult.size).map { field ->
        field.lineSequence()
            .map { it.trim() }
            .map { l -> l.split(" +".toRegex()).map { it.toInt() } }
            .toList()
    }
    return Bingo(numbers, fields)
}

private data class Bingo(
    val numbers: List<Int>,
    val fields: List<List<List<Int>>>,
)

private fun List<List<Boolean>>.isDone(): Boolean {
    return any { it.all { b -> b } } || this[0].indices.any { index -> all { it[index] } }
}

private fun <T> List<List<T>>.find(value: T) = vectors()
    .firstOrNull { this[it] == value }

private fun List<List<Int>>.unmarkedSum(crosses: List<List<Boolean>>) = vectors()
    .filterNot { crosses[it] }
    .sumOf { this[it] }
