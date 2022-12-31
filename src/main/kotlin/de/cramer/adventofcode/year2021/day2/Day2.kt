package de.cramer.adventofcode.year2021.day2

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.times

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 150)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 900)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Command>): Int {
    val position = input.fold(Vector.ZERO) { acc, command ->
        acc + command.vector
    }
    return position.x * position.y
}

private fun problem02(input: List<Command>): Int {
    var position = Vector.ZERO
    var aim = Vector.ZERO
    for (command in input) {
        when (command) {
            is Forward -> position += command.vector + command.units * aim
            is Down, is Up -> aim += command.vector
        }
    }

    return position.x * position.y
}

private fun String.parse(): List<Command> {
    fun String.toCommand(): Command = when {
        startsWith("forward ") -> Forward(substringAfter("forward ").toInt())
        startsWith("down ") -> Down(substringAfter("down ").toInt())
        startsWith("up ") -> Up(substringAfter("up ").toInt())
        else -> error("cannot parse $this to command")
    }

    return lineSequence()
        .map { it.toCommand() }
        .toList()
}

sealed interface Command {
    val vector: Vector
}

data class Forward(
    val units: Int,
) : Command {
    override val vector = Vector(units, 0)
}

data class Down(
    val units: Int,
) : Command {
    override val vector = Vector(0, units)
}

data class Up(
    val units: Int,
) : Command {
    override val vector = Vector(0, -units)
}
