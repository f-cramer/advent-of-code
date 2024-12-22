package de.cramer.adventofcode.year2024.day6

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 41)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 6)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    return buildSet {
        var position = input.position
        var direction = input.direction
        while (position.x in 0..<input.size.x && position.y in 0..<input.size.y) {
            this += position

            var newPosition = position + direction.vector
            while (newPosition in input.obstacles) {
                direction = direction.turn()
                newPosition = position + direction.vector
            }

            position = newPosition
        }
    }.size
}

private fun problem02(input: Input): Int {
    var numberOfSuccessfulNewObstacles = 0
    (0..<input.size.x).forEach { x ->
        (0..<input.size.y).forEach inner@{ y ->
            val newObstacle = Vector(x, y)
            if (newObstacle in input.obstacles || newObstacle == input.position) {
                return@inner
            }
            val obstacles = input.obstacles + newObstacle

            buildSet {
                var position = input.position
                var direction = input.direction
                while (position.x in 0..<input.size.x && position.y in 0..<input.size.y) {
                    val key = position to direction
                    if (key in this) {
                        // found a loop
                        numberOfSuccessfulNewObstacles++
                        return@inner
                    }
                    this += key

                    var newPosition = position + direction.vector
                    while (newPosition in obstacles) {
                        direction = direction.turn()
                        newPosition = position + direction.vector
                    }

                    position = newPosition
                }
            }
        }
    }
    return numberOfSuccessfulNewObstacles
}

private fun String.parse(): Input {
    val obstacles = mutableSetOf<Vector>()
    var position: Vector? = null
    val lines = lines()
    lines.forEachIndexed { y, s ->
        s.toCharArray().forEachIndexed { x, c ->
            if (c == '#') {
                obstacles += Vector(x, y)
            } else if (c == '^') {
                position = Vector(x, y)
            }
        }
    }

    val size = Vector(lines[0].length, lines.size)
    return Input(obstacles, size, requireNotNull(position) { "position cannot be null" }, Direction.UP)
}

private data class Input(
    val obstacles: Set<Vector>,
    val size: Vector,
    val position: Vector,
    val direction: Direction,
)

private enum class Direction(
    val vector: Vector,
) {
    UP(Vector(0, -1)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
}

private fun Direction.turn(): Direction = when (this) {
    Direction.UP -> Direction.RIGHT
    Direction.DOWN -> Direction.LEFT
    Direction.LEFT -> Direction.UP
    Direction.RIGHT -> Direction.DOWN
}
