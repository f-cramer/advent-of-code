package de.cramer.adventofcode.year2021.day9

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
    checkTestResult(testResult01, 15)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1134)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    return input.getLowPoints()
        .sumOf { input[it] + 1 }
}

private fun problem02(input: List<List<Int>>): Int {
    fun Vector.getBasinSize(): Int {
        val basin = mutableSetOf(this)
        val queue = ArrayDeque<Vector>()
        queue += this
        while (queue.isNotEmpty()) {
            val element = queue.removeFirst()
            if (input[element] == 9) {
                continue
            }

            basin += element
            input.neighbors(element)
                .filterNot { it in basin }
                .forEach { queue += it }
        }

        return basin.size
    }

    return input.getLowPoints()
        .map { it.getBasinSize() }
        .sortedDescending()
        .take(3)
        .reduce { acc, i -> acc * i }
}

private fun String.parse(): List<List<Int>> {
    return lineSequence()
        .map { l -> l.map { it.digitToInt() } }
        .toList()
}

private fun List<List<Int>>.getLowPoints() = vectors()
    .filter { v ->
        val height = this[v]
        neighbors(v).all { this[it] > height }
    }

@Suppress("ktlint:standard:argument-list-wrapping")
private val neighborOffsets = listOf(
    Vector(-1, 0), Vector(1, 0), Vector(0, -1), Vector(0, 1),
)

private fun List<List<Int>>.neighbors(position: Vector): Sequence<Vector> {
    return neighborOffsets.asSequence()
        .map { it + position }
        .filter { isValidIndex(it) }
}
