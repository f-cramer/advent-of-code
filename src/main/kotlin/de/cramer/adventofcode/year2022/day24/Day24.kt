package de.cramer.adventofcode.year2022.day24

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.direction.Direction
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runTest
import de.cramer.adventofcode.utils.vector.Vector
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 18)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 54)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input) }
}

private fun problem01(input: Input): Int {
    return traverseValley(input.state, input.start, input.end).second
}

private fun problem02(input: Input): Int {
    val (stateAfterFirst, result1) = traverseValley(input.state, input.start, input.end)
    val (stateAfterSecond, result2) = traverseValley(stateAfterFirst, input.end, input.start)
    val (_, result3) = traverseValley(stateAfterSecond, input.start, input.end)
    return result1 + result2 + result3
}

private fun traverseValley(initialState: State, start: Vector, end: Vector): Pair<State, Int> {
    infix fun Vector.manhattanDistanceTo(other: Vector): Int = abs(this.x - other.x) + abs(this.y - other.y)

    var minutes = 0
    var players = setOf(start)
    var state = initialState
    while (end !in players) {
        minutes++
        state = state.moveBlizzards()

        players = players.asSequence()
            .flatMap { state.getPossibleNextPositions(it) }
            .distinct()
            .sortedBy { p -> p manhattanDistanceTo end }
            .toSet()
    }

    return state to minutes
}

private fun String.parse(): Input {
    fun Char.toDirection() = when (this) {
        '.', '#' -> null
        '^' -> Direction.UP
        '>' -> Direction.RIGHT
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        else -> error("cannot parse $this to direction vector")
    }

    val lines = lines()
    val firstLine = lines.first()
    val lastLine = lines.last()

    val blizzards = lines.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            val direction = c.toDirection() ?: return@mapIndexedNotNull null
            Vector(x, y) to listOf(Blizzard(direction))
        }
    }.toMap()

    val bottomRight = Vector(lastLine.length - 1, lines.size - 1)
    val start = Vector(firstLine.indexOfFirst { it == '.' }, 0)
    val end = Vector(lastLine.indexOfFirst { it == '.' }, bottomRight.y)
    val walls = (0..bottomRight.x).flatMap { x -> (0..bottomRight.y).map { y -> Vector(x, y) } }
        .filter { (it.x == 0 || it.y == 0 || it.x == bottomRight.x || it.y == bottomRight.y) && it != start && it != end }
        .toSet()
    return Input(State(blizzards, bottomRight, walls), start, end)
}

private data class Input(
    val state: State,
    val start: Vector,
    val end: Vector,
)

private data class State(
    val blizzards: Map<Vector, List<Blizzard>>,
    val bottomRight: Vector,
    val walls: Set<Vector>,
)

private fun State.moveBlizzards(): State {
    val newBlizzards = blizzards.asSequence()
        .flatMap { (position, b) ->
            b.map {
                var newPosition = position + it.direction.vector
                if (newPosition in walls) {
                    newPosition = when (it.direction) {
                        Direction.UP -> position.copy(y = bottomRight.y - 1)
                        Direction.RIGHT -> position.copy(x = 1)
                        Direction.DOWN -> position.copy(y = 1)
                        Direction.LEFT -> position.copy(x = bottomRight.x - 1)
                    }
                }
                newPosition to it
            }
        }
        .groupBy({ it.first }) { it.second }
    return copy(blizzards = newBlizzards)
}

private fun State.getPossibleNextPositions(position: Vector) = (Direction.entries.asSequence().map { position + it.vector } + position)
    .filterNot { it in blizzards.keys }
    .filterNot { it in walls }
    .filter { it.x in 0..bottomRight.x && it.y in 0..bottomRight.y }

private data class Blizzard(
    val direction: Direction,
)
