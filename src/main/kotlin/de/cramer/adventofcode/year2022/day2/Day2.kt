package de.cramer.adventofcode.year2022.day2

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 15)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 12)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Pair<Char, Char>>): Long =
    input.asSequence()
        .map { (opponent, me) -> Game(me.toShape(), opponent.toShape()) }
        .sumOf { it.score() }

private fun problem02(input: List<Pair<Char, Char>>): Long =
    input.asSequence()
        .map { (opponent, outcome) ->
            val opponentShape = opponent.toShape()
            Game(outcome.toOutcome().getMyShape(opponentShape), opponentShape)
        }
        .sumOf { it.score() }

private fun String.parse() = lineSequence()
    .map { it.split(" ") }
    .map { (first, second) -> first.toCharArray()[0] to second.toCharArray()[0] }
    .toList()

private fun Game.score() = play().score + me.score

private fun Game.play(): Outcome = when (me) {
    Shape.ROCK -> when (opponent) {
        Shape.ROCK -> Outcome.DRAW
        Shape.PAPER -> Outcome.LOSS
        Shape.SCISSORS -> Outcome.WIN
    }

    Shape.PAPER -> when (opponent) {
        Shape.ROCK -> Outcome.WIN
        Shape.PAPER -> Outcome.DRAW
        Shape.SCISSORS -> Outcome.LOSS
    }

    Shape.SCISSORS -> when (opponent) {
        Shape.ROCK -> Outcome.LOSS
        Shape.PAPER -> Outcome.WIN
        Shape.SCISSORS -> Outcome.DRAW
    }
}

private fun Outcome.getMyShape(opponentShape: Shape): Shape = when (this) {
    Outcome.WIN -> when (opponentShape) {
        Shape.ROCK -> Shape.PAPER
        Shape.PAPER -> Shape.SCISSORS
        Shape.SCISSORS -> Shape.ROCK
    }

    Outcome.DRAW -> opponentShape
    Outcome.LOSS -> when (opponentShape) {
        Shape.ROCK -> Shape.SCISSORS
        Shape.PAPER -> Shape.ROCK
        Shape.SCISSORS -> Shape.PAPER
    }
}

private fun Char.toShape(): Shape = when (this) {
    'A', 'X' -> Shape.ROCK
    'B', 'Y' -> Shape.PAPER
    'C', 'Z' -> Shape.SCISSORS
    else -> error("cannot convert $this to shape")
}

private fun Char.toOutcome(): Outcome = when (this) {
    'X' -> Outcome.LOSS
    'Y' -> Outcome.DRAW
    'Z' -> Outcome.WIN
    else -> error("cannot convert $this to outcome")
}

private data class Game(
    val me: Shape,
    val opponent: Shape,
)

private enum class Outcome(
    val score: Long,
) {
    WIN(6), DRAW(3), LOSS(0)
}

private enum class Shape(
    val score: Long,
) {
    ROCK(1), PAPER(2), SCISSORS(3)
}
