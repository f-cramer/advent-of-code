package de.cramer.adventofcode.year2024.day16

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 7036)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 45)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    val queue = PriorityQueue(compareBy<State> { it.score + it.distanceToEnd }).apply {
        val path = Path(mapOf(input.startPosition to input.startDirection))
        this += State(path, input.startPosition, input.startDirection, path.getScore(input.startDirection), input.endPosition)
    }

    val visitedPositions = mutableSetOf<Vector>()
    while (queue.isNotEmpty()) {
        val state = queue.poll()!!
        if (!visitedPositions.add(state.position)) {
            continue
        }

        if (state.position == input.endPosition) {
            return state.score
        }

        Direction.entries.asSequence()
            .map { it to state.position + it.vector }
            .filter { (_, p) -> p !in state.path && input.map[p] == Tile.EMPTY }
            .map { (d, p) ->
                val newPath = state.path.copy()
                newPath[state.position] = d
                newPath[p] = d

                var newPosition = p
                var nextNewPosition = newPosition + d.vector
                var scoreIncrement = 1
                while (
                    input.map[nextNewPosition] == Tile.EMPTY &&
                    Direction.entries.asSequence()
                        .filterNot { it == d }
                        .none { input.map[newPosition + it.vector] == Tile.EMPTY }
                ) {
                    newPosition = nextNewPosition
                    newPath[newPosition] = d
                    scoreIncrement += 1

                    if (newPosition == input.endPosition) {
                        break
                    }

                    nextNewPosition += d.vector
                }
                State(newPath, newPosition, d, state.score + scoreIncrement + d.turnCount(state.direction) * 1000, input.endPosition)
            }
            .forEach(queue::add)
    }

    error("no path found")
}

private fun problem02(input: Input): Int {
    val queue = PriorityQueue(compareBy<State> { it.score + it.distanceToEnd }).apply {
        val path = Path(mapOf(input.startPosition to input.startDirection))
        this += State(path, input.startPosition, input.startDirection, path.getScore(input.startDirection), input.endPosition)
    }

    var minimumPathScore: Int? = null
    val visitedPositions = mutableMapOf<Pair<Vector, Direction>, State>()
    val paths = mutableListOf<Path>()
    while (queue.isNotEmpty()) {
        val state = queue.poll()!!
        if (minimumPathScore != null && state.score > minimumPathScore) {
            continue
        }

        val minimumStateToPosition = visitedPositions[state.position to state.direction]
        if (minimumStateToPosition != null && (minimumStateToPosition.score + 1000 * minimumStateToPosition.direction.turnCount(state.direction)) < state.score) {
            continue
        }

        if (state.position == input.endPosition) {
            paths += state.path
            minimumPathScore = state.score
            continue
        }

        visitedPositions[state.position to state.direction] = state

        Direction.entries.asSequence()
            .map { it to state.position + it.vector }
            .filter { (_, p) -> p !in state.path && input.map[p] == Tile.EMPTY }
            .map { (d, p) ->
                val newPath = state.path.copy()
                newPath[state.position] = d
                newPath[p] = d

                var newPosition = p
                var nextNewPosition = newPosition + d.vector
                var scoreIncrement = 1
                while (
                    input.map[nextNewPosition] == Tile.EMPTY &&
                    Direction.entries.asSequence()
                        .filterNot { it == d }
                        .none { input.map[newPosition + it.vector] == Tile.EMPTY }
                ) {
                    newPosition = nextNewPosition
                    newPath[newPosition] = d
                    scoreIncrement += 1

                    if (newPosition == input.endPosition) {
                        break
                    }

                    nextNewPosition += d.vector
                }
                State(newPath, newPosition, d, state.score + scoreIncrement + d.turnCount(state.direction) * 1000, input.endPosition)
            }
            .forEach(queue::add)
    }

    return paths.flatMap { it.keys }.toSet().size
}

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

    return Input(map, startPosition!!, Direction.RIGHT, endPosition!!)
}

private data class Input(
    val map: List<List<Tile>>,
    val startPosition: Vector,
    val startDirection: Direction,
    val endPosition: Vector,
)

private enum class Tile {
    WALL,
    EMPTY,
}

private enum class Direction(
    val vector: Vector,
) {
    UP(Vector(0, -1)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
}

private fun Direction.opposite(): Direction = when (this) {
    Direction.UP -> Direction.DOWN
    Direction.DOWN -> Direction.UP
    Direction.LEFT -> Direction.RIGHT
    Direction.RIGHT -> Direction.LEFT
}

private fun Direction.turnCount(target: Direction): Int = when (target) {
    this -> 0
    opposite() -> 2
    else -> 1
}

private typealias Path = LinkedHashMap<Vector, Direction>

private fun Path.copy() = LinkedHashMap(this)

private fun Path.getScore(startDirection: Direction) = size - 1 + (listOf(startDirection) + values).zipWithNext().fold(0) { acc, (current, next) -> acc + current.turnCount(next) * 1000 }

private data class State(val path: Path, val position: Vector, val direction: Direction, val score: Int, val endPosition: Vector) {
    val distanceToEnd = run {
        val xDistance = abs(endPosition.x - position.x)
        val yDistance = abs(endPosition.y - position.y)
        val turn = if (xDistance != 0 && yDistance != 0) 1000 else 0
        xDistance + yDistance + turn
    }
}
