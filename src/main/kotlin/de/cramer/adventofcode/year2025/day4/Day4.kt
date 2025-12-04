package de.cramer.adventofcode.year2025.day4

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 13)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 43)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Set<Vector>): Int {
    return input.findRemovablePositions().size
}

private fun problem02(input: Set<Vector>): Int {
    val positions = input.toMutableSet()
    do {
        val removablePositions = positions.findRemovablePositions()
        positions -= removablePositions
    } while (removablePositions.isNotEmpty())
    return input.size - positions.size
}

private fun String.parse(): Set<Vector> {
    val input = lines().map { it.toCharArray().toList() }
    return input.vectors()
        .filter { input[it] == '@' }
        .toSet()
}

private fun Set<Vector>.findRemovablePositions(): Set<Vector> {
    return filter { it.neighbors.count { n -> n in this } < 4 }.toSet()
}

@Suppress("ktlint:standard:argument-list-wrapping")
private val neighborOffsets = listOf(
    Vector(-1, -1), Vector(0, -1), Vector(1, -1),
    Vector(-1, 0), Vector(1, 0),
    Vector(-1, 1), Vector(0, 1), Vector(1, 1),
)

private val Vector.neighbors
    get() = neighborOffsets.map { this + it }
