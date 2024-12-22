package de.cramer.adventofcode.year2021.day21

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import kotlin.math.max

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 739785)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 444356092776315L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    class Dice {
        private var nextRoll: Int = 1
        var totalRolls: Int = 0

        fun roll(): Int {
            totalRolls++
            val result = nextRoll++
            if (nextRoll > 100) {
                nextRoll = 1
            }
            return result
        }
    }

    val dice = Dice()
    var game = GameState(listOf(PlayerState(0, input.start1 - 1), PlayerState(0, input.start2 - 1)))

    while (true) {
        val roll = dice.roll() + dice.roll() + dice.roll()
        game = game.next(roll)

        if (game.players.any { it.score >= 1000 }) {
            return game.players.minOf { it.score } * dice.totalRolls
        }
    }
}

private fun problem02(input: Input): Long {
    val diceResults = (1..3).flatMap { a -> (1..3).flatMap { b -> (1..3).map { c -> a + b + c } } }
        .groupingBy { it }.eachCount()

    val cache = mutableMapOf<GameState, WinCount>()

    fun GameState.play(): WinCount = when {
        players[0].score >= 21 -> WinCount(1, 0)
        players[1].score >= 21 -> WinCount(0, 1)
        else -> cache.getOrPut(this) {
            diceResults.map { (roll, frequency) -> next(roll).play() * frequency }
                .reduce { acc, count -> acc + count }
        }
    }

    val game = GameState(listOf(PlayerState(0, input.start1 - 1), PlayerState(0, input.start2 - 1)))
    return game.play().maxScore
}

private fun String.parse(): Input {
    fun String.toStartPosition() = substringAfter("position: ").toInt()
    val (s1, s2) = lines()
    return Input(s1.toStartPosition(), s2.toStartPosition())
}

private data class Input(
    val start1: Int,
    val start2: Int,
)

data class PlayerState(
    val score: Int,
    val space: Int,
) {
    fun next(roll: Int): PlayerState {
        val nextSpace = (space + roll) % 10
        return PlayerState(
            score + nextSpace + 1,
            nextSpace,
        )
    }
}

private data class GameState(
    val players: List<PlayerState>,
    val playerIndex: Int = 0,
) {

    fun next(roll: Int): GameState {
        val nextPlayers = players.toMutableList()
        nextPlayers[playerIndex] = players[playerIndex].next(roll)
        val nextPlayerIndex = (playerIndex + 1) % players.size
        return GameState(nextPlayers, nextPlayerIndex)
    }
}

private data class WinCount(
    val player1: Long,
    val player2: Long,
) {
    val maxScore: Long
        get() = max(player1, player2)

    operator fun plus(other: WinCount) = WinCount(player1 + other.player1, player2 + other.player2)
    operator fun times(factor: Int) = WinCount(player1 * factor, player2 * factor)
}
