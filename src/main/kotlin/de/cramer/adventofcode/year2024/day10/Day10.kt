package de.cramer.adventofcode.year2024.day10

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.get
import de.cramer.adventofcode.isValidIndex
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 36)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 81)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    return input.vectors()
        .filter { input[it] == 0 }
        .sumOf { v ->
            val stack = ArrayDeque<Vector>()
            stack += v

            buildSet {
                while (stack.isNotEmpty()) {
                    val position = stack.removeFirst()
                    if (input[position] == 9) {
                        this += position
                    } else {
                        stack += position.neighbors
                            .filter { input.isValidIndex(it) }
                            .filter { input[it] == input[position] + 1 }
                    }
                }
            }.size
        }
}

private fun problem02(input: List<List<Int>>): Int {
    return input.vectors()
        .filter { input[it] == 0 }
        .sumOf { v ->
            val stack = ArrayDeque<Vector>()
            stack += v

            var numberOfTrails = 0
            while (stack.isNotEmpty()) {
                val position = stack.removeFirst()
                if (input[position] == 9) {
                    numberOfTrails++
                } else {
                    stack += position.neighbors
                        .filter { input.isValidIndex(it) }
                        .filter { input[it] == input[position] + 1 }
                }
            }

            numberOfTrails
        }
}

private fun String.parse(): List<List<Int>> {
    return lines()
        .map { s -> s.toCharArray().map { it.digitToInt() } }
}

private val Vector.neighbors: Sequence<Vector>
    get() = sequenceOf(north(), east(), south(), west())
