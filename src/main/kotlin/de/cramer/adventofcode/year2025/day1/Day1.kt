package de.cramer.adventofcode.year2025.day1

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 6)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Action>): Int {
    var numberOfZeros = 0
    var position = 50
    for (action in input) {
        position += action.steps * action.direction.signum
        position = position.mod(100)
        if (position == 0) {
            numberOfZeros++
        }
    }
    return numberOfZeros
}

private fun problem02(input: List<Action>): Int {
    var numberOfZeros = 0
    var position = 50
    for (action in input) {
        val direction = action.direction
        val totalSteps = action.steps
        numberOfZeros += totalSteps / 100
        val remainingSteps = totalSteps.mod(100)
        val oldPosition = position
        position += remainingSteps * direction.signum
        position = position.mod(100)
        if (position == 0 || oldPosition != 0 && (direction == Direction.LEFT && oldPosition < position) || (direction == Direction.RIGHT && oldPosition > position)) {
            numberOfZeros++
        }
    }
    return numberOfZeros
}

private fun String.parse(): List<Action> {
    return lines()
        .map {
            val direction = if (it.startsWith("L")) {
                Direction.LEFT
            } else {
                Direction.RIGHT
            }
            val steps = it.substring(1).toInt()
            Action(direction, steps)
        }
}

private enum class Direction(
    val signum: Int,
) {
    LEFT(-1),
    RIGHT(1),
}

private data class Action(
    val direction: Direction,
    val steps: Int,
)
