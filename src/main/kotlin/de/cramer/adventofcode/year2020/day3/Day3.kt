package de.cramer.adventofcode.year2020.day3

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 7)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 336L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Tile>>): Int {
    val slope = Vector(3, 1)
    return input.countTrees(slope)
}

private fun problem02(input: List<List<Tile>>): Long {
    return sequenceOf(
        Vector(1, 1),
        Vector(3, 1),
        Vector(5, 1),
        Vector(7, 1),
        Vector(1, 2),
    )
        .map { input.countTrees(it).toLong() }
        .reduce { acc, i -> acc * i }
}

private fun List<List<Tile>>.countTrees(slope: Vector) = generateSequence(Vector(0, 0)) { it + slope }
    .takeWhile { (_, y) -> y < size }
    .map { (x, y) -> Vector(x % this[y].size, y) }
    .count { this[it] == Tile.TREE }

private fun String.parse(): List<List<Tile>> {
    fun Char.toTile() = when (this) {
        '.' -> Tile.EMPTY
        '#' -> Tile.TREE
        else -> error("cannot parse $this to tile")
    }

    return lineSequence()
        .map { l -> l.map { it.toTile() } }
        .toList()
}

private enum class Tile {
    EMPTY,
    TREE,
}
