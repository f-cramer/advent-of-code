package de.cramer.adventofcode.year2023.day17

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.direction.Direction
import de.cramer.adventofcode.utils.direction.turnLeft
import de.cramer.adventofcode.utils.direction.turnRight
import de.cramer.adventofcode.utils.graph.findShortestPath
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.isValidIndex
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 102)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 94)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    data class State(val position: Vector, val direction: Direction, val continuousForwardSteps: Int)

    fun State.getNeighbors(): List<State> = buildList {
        if (continuousForwardSteps < 3) {
            this += State(position + direction.vector, direction, continuousForwardSteps + 1)
        }

        val left = direction.turnLeft()
        this += State(position + left.vector, left, 1)
        val right = direction.turnRight()
        this += State(position + right.vector, right, 1)
    }.filter { input.isValidIndex(it.position) }

    val start = State(Vector(0, 0), Direction.DOWN, 0)
    val end = Vector(input.first().size - 1, input.size - 1)

    return findShortestPath(start, { position == end }, State::getNeighbors, { _, to -> input[to.position] }).cost
}

private fun problem02(input: List<List<Int>>): Int {
    data class State(val position: Vector, val direction: Direction, val continuousForwardSteps: Int)

    fun State.getNeighbors(): List<State> = buildList {
        if (continuousForwardSteps < 10) {
            this += State(position + direction.vector, direction, continuousForwardSteps + 1)
        }

        if (continuousForwardSteps >= 4) {
            val left = direction.turnLeft()
            this += State(position + left.vector, left, 1)
            val right = direction.turnRight()
            this += State(position + right.vector, right, 1)
        }
    }.filter { input.isValidIndex(it.position) }

    val end = Vector(input.first().size - 1, input.size - 1)

    var cost = Int.MAX_VALUE
    sequenceOf(Direction.RIGHT, Direction.DOWN).forEach {
        val start = State(Vector(0, 0), it, 0)
        val shortestPath = runCatching { findShortestPath(start, { position == end && continuousForwardSteps >= 4 }, State::getNeighbors, { _, to -> input[to.position] }, maximumCost = cost) }
        cost = min(cost, shortestPath.getOrNull()?.cost ?: Int.MAX_VALUE)
    }
    return cost
}

private fun String.parse(): List<List<Int>> {
    return lines()
        .map { l -> l.map { it.digitToInt() } }
}
