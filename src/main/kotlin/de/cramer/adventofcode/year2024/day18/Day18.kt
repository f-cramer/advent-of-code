package de.cramer.adventofcode.year2024.day18

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.direction.Direction
import de.cramer.adventofcode.utils.graph.SearchResult
import de.cramer.adventofcode.utils.graph.findShortestPath
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines
import de.cramer.adventofcode.utils.vector.Vector
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput, 12)
    checkTestResult(testResult01, 22)
    val testResult02 = problem02(testInput, 12)
    checkTestResult(testResult02, "6,1")

    val input = readInput().parse()
    runProblem01 { problem01(input, 1024) }
    runProblem02 { problem02(input, 1024) }
}

private fun problem01(input: Input, byteCount: Int): Int {
    return getFastestPath(input, byteCount)!!.cost
}

private fun problem02(input: Input, byteCount: Int): String {
    var fastestPath = getFastestPath(input, byteCount)!!.path
    input.bytePositions.withIndex().asSequence()
        .drop(byteCount)
        .filter { (_, p) -> p in fastestPath }
        .forEach { (i, p) ->
            fastestPath = getFastestPath(input, i + 1)?.path ?: return "${p.x},${p.y}"
        }

    error("no skipped path found")
}

private fun getFastestPath(input: Input, byteCount: Int): SearchResult<Vector>? {
    val mapSize = input.mapSize
    val bytePositions = input.bytePositions.take(byteCount).toSet()
    val endPosition = mapSize - Vector(1, 1)
    val path = Path(setOf(Vector.ZERO))
    val start = Vector.ZERO

    return runCatching {
        findShortestPath(
            start,
            { this == endPosition },
            { p ->
                Direction.entries.asSequence()
                    .map { p + it.vector }
                    .filter { it.x in 0..<mapSize.x && it.y in 0..<mapSize.y }
                    .filter { it !in bytePositions }
                    .toList()
            },
        )
    }.getOrNull()
}

private fun String.parse(): Input {
    val (mapSizeString, positionsString) = splitByEmptyLines()
    val mapSize = mapSizeString.split(",").let { (x, y) -> Vector(x.toInt(), y.toInt()) }
    val bytePositions = positionsString.lines().map {
        val (x, y) = it.split(",")
        Vector(x.toInt(), y.toInt())
    }
    return Input(mapSize, bytePositions)
}

private data class Input(
    val mapSize: Vector,
    val bytePositions: List<Vector>,
)

private typealias Path = LinkedHashSet<Vector>

private fun Path.copy() = LinkedHashSet(this)

private data class State(val path: Path, val position: Vector, val score: Int, val endPosition: Vector) {
    val distanceToEnd = abs(endPosition.x - position.x) + abs(endPosition.y - position.y)
}
