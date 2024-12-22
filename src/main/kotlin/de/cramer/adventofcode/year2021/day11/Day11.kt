package de.cramer.adventofcode.year2021.day11

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.isValidIndex
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.set
import de.cramer.adventofcode.utils.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 1656)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 195)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    val octopuses = input.map { it.toMutableList() }
    var flashes = 0
    repeat(100) {
        val flashers = octopuses.simulate()
        flashes += flashers.size
    }

    return flashes
}

private fun problem02(input: List<List<Int>>): Int {
    val octopuses = input.map { it.toMutableList() }
    val totalOctopuses = octopuses.sumOf { it.size }
    var round = 0
    while (true) {
        round++

        val flashers = octopuses.simulate()
        if (flashers.size == totalOctopuses) {
            return round
        }
    }
}

private fun String.parse(): List<List<Int>> {
    return lineSequence()
        .map { l -> l.map { it.digitToInt() } }
        .toList()
}

private fun List<MutableList<Int>>.simulate(): Set<Vector> {
    vectors().forEach { this[it] += 1 }
    val flashers = mutableSetOf<Vector>()
    while (true) {
        val newFlashers = vectors()
            .filterNot { it in flashers }
            .filter { this[it] > 9 }
            .toList()
        if (newFlashers.isEmpty()) {
            break
        }

        flashers += newFlashers
        newFlashers.asSequence()
            .flatMap { getNeighbors(it) }
            .forEach { this[it] += 1 }
    }

    flashers.forEach { this[it] = 0 }
    return flashers
}

private operator fun List<List<Int>>.contains(value: Int) = any { value in it }

@Suppress("ktlint:standard:argument-list-wrapping")
private val neighborOffsets = listOf(
    Vector(-1, -1), Vector(0, -1), Vector(1, -1),
    Vector(-1, 0), Vector(1, 0),
    Vector(-1, 1), Vector(0, 1), Vector(1, 1),
)

private fun List<List<Int>>.getNeighbors(position: Vector) = neighborOffsets.asSequence()
    .map { position + it }
    .filter { isValidIndex(it) }
