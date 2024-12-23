package de.cramer.adventofcode.year2023.day18

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.direction.Direction
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 62)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 952408144115L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Instruction>): Long {
    return input.getArea()
}

private fun problem02(input: List<Instruction>): Long {
    val directionMapper = listOf(Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP)
    val instructions = input.map {
        val steps = it.color.substring(0, 5).toInt(16)
        val direction = directionMapper[it.color[5].digitToInt()]
        Instruction(direction, steps, it.color)
    }
    return instructions.getArea()
}

private fun List<Instruction>.getArea(): Long {
    var previousY = 0L
    var previousX = 0L

    return sumOf { instruction ->
        var y = previousY
        var x = previousX

        val steps = instruction.steps
        when (instruction.direction) {
            Direction.UP -> y -= steps
            Direction.DOWN -> y += steps
            Direction.LEFT -> x -= steps
            Direction.RIGHT -> x += steps
        }

        val area = previousX * y - previousY * x

        previousY = y
        previousX = x

        area + steps
    } / 2 + 1
}

private fun String.parse(): List<Instruction> {
    fun Char.parseDirection() = when (this) {
        'U' -> Direction.UP
        'D' -> Direction.DOWN
        'L' -> Direction.LEFT
        'R' -> Direction.RIGHT
        else -> error("cannot parse $this to direction")
    }

    val pattern = """(.) (\d+) \(#(.*)\)""".toRegex()
    return pattern.findAll(this)
        .map {
            val (_, direction, steps, color) = it.groupValues
            Instruction(direction[0].parseDirection(), steps.toInt(), color)
        }
        .toList()
}

private data class Instruction(
    val direction: Direction,
    val steps: Int,
    val color: String,
)
