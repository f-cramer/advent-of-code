package de.cramer.adventofcode.year2024.day20

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.direction.Direction
import de.cramer.adventofcode.utils.graph.findShortestPath
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import kotlin.math.absoluteValue

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput, 50)
    checkTestResult(testResult01, 1)
    val testResult02 = problem02(testInput, 50)
    checkTestResult(testResult02, 285)

    val input = readInput().parse()
    runProblem01 { problem01(input, 100) }
    runProblem02 { problem02(input, 100) }
}

private fun problem01(input: Input, minimumSaving: Int): Int {
    return getPathCountWithSavings(input, minimumSaving, 2)
}

private fun problem02(input: Input, minimumSaving: Int): Int {
    return getPathCountWithSavings(input, minimumSaving, 20)
}

private fun getPathCountWithSavings(input: Input, minimumSaving: Int, maximumCheatLength: Int): Int {
    val path = input.getShortestPath()
    return path.mapIndexed { startIndex, start ->
        (startIndex + minimumSaving + 2..<path.size).count { endIndex ->
            val end = path[endIndex]
            val distance = (end.x - start.x).absoluteValue + (end.y - start.y).absoluteValue
            distance <= maximumCheatLength && endIndex - startIndex - distance >= minimumSaving
        }
    }.sum()
}

private fun Input.getShortestPath(): List<Vector> = findShortestPath(
    startPosition,
    { this == endPosition },
    { p ->
        Direction.entries.asSequence()
            .map { p + it.vector }
            .filter { map[it] == Tile.EMPTY }
            .toList()
    },
).path

private fun String.parse(): Input {
    var startPosition: Vector? = null
    var endPosition: Vector? = null
    val map = lines().mapIndexed { y, s ->
        s.toCharArray().mapIndexed { x, c ->
            when (c) {
                '#' -> Tile.WALL
                '.' -> Tile.EMPTY
                'E' -> {
                    endPosition = Vector(x, y)
                    Tile.EMPTY
                }

                'S' -> {
                    startPosition = Vector(x, y)
                    Tile.EMPTY
                }

                else -> error("cannot parse $c to tile")
            }
        }
    }

    return Input(map, startPosition!!, endPosition!!)
}

private data class Input(
    val map: List<List<Tile>>,
    val startPosition: Vector,
    val endPosition: Vector,
)

private enum class Tile {
    WALL,
    EMPTY,
}
