package de.cramer.adventofcode.year2020.day15

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 436)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 175594)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Int>): Int {
    return simulate(input, 2020)
}

private fun problem02(input: List<Int>): Int {
    return simulate(input, 30000000)
}

private fun simulate(input: List<Int>, turns: Int): Int {
    val numbersWithLatestTurn = mutableMapOf<Int, Int>()

    data class State(
        val number: Int,
        val latestTurn: Int?,
    )

    fun sayNumber(number: Int, turn: Int): State {
        val old = numbersWithLatestTurn[number]
        numbersWithLatestTurn[number] = turn
        return State(number, old)
    }

    var state = State(Int.MIN_VALUE, null)

    input.forEachIndexed { turn, number -> state = sayNumber(number, turn) }
    for (turn in input.size..<turns) {
        val latestTurn = state.latestTurn
        state = if (latestTurn == null) {
            sayNumber(0, turn)
        } else {
            sayNumber(turn - latestTurn - 1, turn)
        }
    }

    return state.number
}

private fun String.parse(): List<Int> {
    return split(",").map { it.toInt() }
}
