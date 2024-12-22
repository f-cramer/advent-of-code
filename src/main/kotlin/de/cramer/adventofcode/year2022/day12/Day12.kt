package de.cramer.adventofcode.year2022.day12

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import kotlin.math.sqrt

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 31)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 29)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: HeightMap): Int {
    return input.findOptimalPathLength().pathLength
}

private fun problem02(input: HeightMap): Int {
    val startPositions = input.grid
        .flatMapIndexed { y: Int, row: List<Int> ->
            row.asSequence()
                .mapIndexedNotNull { x, value ->
                    if (value == 0) Vector(x, y) else null
                }
        }
        .toMutableSet()

    var shortestPath: List<Vector>? = null

    fun Vector.findShortestPath() = input.copy(start = this).findOptimalPathLength(shortestPath?.pathLength ?: Int.MAX_VALUE)

    while (startPositions.isNotEmpty()) {
        val startPosition = startPositions.first()
        startPositions -= startPosition
        val path = runCatching { startPosition.findShortestPath() }.getOrNull() ?: continue

        val fromIndex = path.indexOfLast { input[it] == 0 }
        val strippedPath = if (fromIndex == 0) path else path.subList(fromIndex, path.size)
        if (shortestPath == null || shortestPath.pathLength > strippedPath.pathLength) {
            shortestPath = strippedPath
        }

        startPositions -= path.toSet()
    }
    return shortestPath?.pathLength ?: error("could not find any path")
}

private fun String.parse(): HeightMap {
    fun Char.toHeight(): Int {
        return when (this) {
            'S' -> 'a'
            'E' -> 'z'
            else -> this
        } - 'a'
    }

    fun List<String>.findPosition(char: Char): Vector {
        return foldIndexed<String, Vector?>(null) { index, acc, s ->
            acc ?: s.indexOf(char).takeIf { it >= 0 }?.let {
                Vector(it, index)
            }
        } ?: error("could not find $char in $this")
    }

    val lines = lines()
    val start = lines.findPosition('S')
    val end = lines.findPosition('E')
    val grid = lines.map { it.toCharArray().map { c -> c.toHeight() } }
    return HeightMap(grid, start, end)
}

private data class HeightMap(
    val grid: List<List<Int>>,
    val start: Vector,
    val end: Vector,
) {
    operator fun get(vector: Vector) = grid[vector.y][vector.x]

    fun isValid(vector: Vector): Boolean = vector.y in grid.indices && vector.x in grid[vector.y].indices

    fun getAdjacentVectors(vector: Vector): Sequence<Vector> {
        fun Int.isValidNextHeight(nextHeight: Int): Boolean = this + 1 >= nextHeight

        val height = this[vector]
        return sequenceOf(
            Vector(vector.x - 1, vector.y),
            Vector(vector.x + 1, vector.y),
            Vector(vector.x, vector.y - 1),
            Vector(vector.x, vector.y + 1),
        )
            .filter { isValid(it) }
            .filter { height.isValidNextHeight(this[it]) }
    }
}

private val List<Vector>.pathLength
    get() = size - 1

private fun HeightMap.findOptimalPathLength(upperLengthLimit: Int = Int.MAX_VALUE): List<Vector> {
    fun HeightMap.createNode(path: List<Vector>, currentScore: Double) =
        Node(path, currentScore, path.last().getDistanceTo(end)).takeUnless {
            it.estimatedTotalScore > upperLengthLimit
        }

    fun Node.getNeighbors(): List<Node> = getAdjacentVectors(currentVector)
        .mapNotNull { createNode(path + it, currentScore + 1) }
        .sortedBy { it.estimatedTotalScore }
        .toList()

    tailrec fun findOptimalPath(ways: List<Node>, walked: Set<Vector>): List<Vector> {
        if (ways.isEmpty()) {
            error("cannot find path")
        }
        val node = ways[0]
        val tail = ways.drop(1)
        return when (node.currentVector) {
            end -> node.path
            in walked -> findOptimalPath(tail, walked)
            else -> findOptimalPath(tail + node.getNeighbors(), walked + node.currentVector)
        }
    }

    return findOptimalPath(listOf(createNode(listOf(start), 0.0)!!), emptySet())
}

private data class Node(
    val path: List<Vector>,
    val currentScore: Double,
    val estimatedRemainingScore: Double,
) {
    val currentVector: Vector = path.last()
    val estimatedTotalScore = currentScore + estimatedRemainingScore
}

private fun Vector.getDistanceTo(other: Vector): Double {
    val dx = x - other.x
    val dy = y - other.y
    return sqrt((dx * dx + dy * dy).toDouble())
}
