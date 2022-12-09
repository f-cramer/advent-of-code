package de.cramer.adventofcode.day9

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 13)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Command>): Int {
    return input.follow(2)
}

private fun problem02(input: List<Command>): Int {
    return input.follow(10)
}

private fun List<Command>.follow(rowLength: Int): Int {
    require(rowLength >= 2) { "rowLength has to be at least 2" }
    var head = Vector.ZERO
    var tail = List(rowLength - 1) { Vector.ZERO }
    val lastTailPositions = mutableSetOf(tail.last())

    for (command in this) {
        repeat(command.steps) {
            head += command.direction.vector
            var previous = head
            tail = tail.map {
                if (!it.isTouching(previous)) {
                    val movement = (previous - it).toSingleStep()
                    it + movement
                } else {
                    it
                }.apply {
                    previous = this
                }
            }
            lastTailPositions += tail.last()
        }
    }

    return lastTailPositions.size
}

private fun String.parse(): List<Command> {
    return lineSequence()
        .map { it.toCommand() }
        .toList()
}

private fun String.toCommand(): Command = split(" ").let { (d, s) ->
    Command(d.toDirection(), s.toInt())
}

private fun String.toDirection(): Direction = when (this) {
    "L" -> Direction.LEFT
    "R" -> Direction.RIGHT
    "U" -> Direction.UP
    "D" -> Direction.DOWN
    else -> error("cannot parse $this to Direction")
}

private enum class Direction(
    val vector: Vector,
) {
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
    UP(Vector(0, 1)),
    DOWN(Vector(0, -1)),
}

private data class Command(
    val direction: Direction,
    val steps: Int,
)

private fun Vector.isTouching(other: Vector): Boolean {
    val dx = abs(this.x - other.x)
    val dy = abs(this.y - other.y)
    return dx <= 1 && dy <= 1
}

private fun Vector.toSingleStep(): Vector = Vector(x.sign, y.sign)
