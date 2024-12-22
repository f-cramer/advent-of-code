package de.cramer.adventofcode.year2023.day16

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.isValidIndex

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 46)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 51)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Tile>>): Int {
    return input.getEnergizedTiles(Vector(0, 0), Direction.RIGHT)
}

private fun problem02(input: List<List<Tile>>): Int {
    val starts = input.indices.map { Vector(0, it) to Direction.RIGHT } +
        input.indices.map { Vector(input[it].size - 1, it) to Direction.LEFT } +
        input.first().indices.map { Vector(it, 0) to Direction.DOWN } +
        input.first().indices.map { Vector(it, input.size - 1) to Direction.UP }
    return starts.maxOf { input.getEnergizedTiles(it.first, it.second) }
}

private fun List<List<Tile>>.getEnergizedTiles(startPosition: Vector, startDirection: Direction): Int {
    val visitedPositions = mutableSetOf<Pair<Vector, Direction>>()

    val nextPositions = ArrayDeque<Pair<Vector, Direction>>()
    nextPositions += startPosition to startDirection

    while (nextPositions.isNotEmpty()) {
        val nextPosition = nextPositions.removeFirst()
        if (nextPosition in visitedPositions) {
            continue
        }

        visitedPositions += nextPosition
        val (position, direction) = nextPosition
        this[position].encounter(direction).asSequence()
            .map { position + it.vector to it }
            .filter { (p) -> isValidIndex(p) }
            .filter { it !in visitedPositions }
            .forEach { nextPositions += it }
    }

    return visitedPositions.map { it.first }.distinct().size
}

private fun String.parse(): List<List<Tile>> {
    fun Char.parseTile() = when (this) {
        '.' -> Tile.EMPTY
        '/' -> Tile.MIRROR_UP
        '\\' -> Tile.MIRROR_DOWN
        '|' -> Tile.SPLITTER_VERTICAL
        '-' -> Tile.SPLITTER_HORIZONTAL
        else -> error("cannot parse $this to tile")
    }

    return lines().map { it.map { c -> c.parseTile() } }
}

private enum class Tile {
    EMPTY,
    MIRROR_UP,
    MIRROR_DOWN,
    SPLITTER_VERTICAL,
    SPLITTER_HORIZONTAL,
}

private fun Tile.encounter(direction: Direction) = when (this) {
    Tile.EMPTY -> setOf(direction)
    Tile.MIRROR_UP -> when (direction) {
        Direction.UP -> setOf(Direction.RIGHT)
        Direction.DOWN -> setOf(Direction.LEFT)
        Direction.LEFT -> setOf(Direction.DOWN)
        Direction.RIGHT -> setOf(Direction.UP)
    }

    Tile.MIRROR_DOWN -> when (direction) {
        Direction.UP -> setOf(Direction.LEFT)
        Direction.DOWN -> setOf(Direction.RIGHT)
        Direction.LEFT -> setOf(Direction.UP)
        Direction.RIGHT -> setOf(Direction.DOWN)
    }

    Tile.SPLITTER_VERTICAL -> when (direction) {
        Direction.UP, Direction.DOWN -> setOf(direction)
        Direction.LEFT, Direction.RIGHT -> setOf(Direction.UP, Direction.DOWN)
    }

    Tile.SPLITTER_HORIZONTAL -> when (direction) {
        Direction.UP, Direction.DOWN -> setOf(Direction.LEFT, Direction.RIGHT)
        Direction.LEFT, Direction.RIGHT -> setOf(direction)
    }
}

private enum class Direction(
    val vector: Vector,
) {
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
    UP(Vector(0, -1)),
    DOWN(Vector(0, 1)),
}
