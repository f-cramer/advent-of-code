package de.cramer.adventofcode.year2023.day4

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 13u)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 30u)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(cards: List<Card>): UInt {
    return cards
        .filter { it.numberOfWinningNumbersInCardNumbers > 0 }
        .sumOf { card ->
            1u.shl(card.numberOfWinningNumbersInCardNumbers - 1)
        }
}

private fun problem02(cards: List<Card>): UInt {
    val numberOfInstancesByCardIndex = cards.indices.associateWith { 1u }.toMutableMap()
    for ((index, card) in cards.withIndex()) {
        val instances = numberOfInstancesByCardIndex.getValue(index)
        (index + 1..index + card.numberOfWinningNumbersInCardNumbers)
            .filter { it < cards.size }
            .forEach {
                val oldValue = numberOfInstancesByCardIndex.getValue(it)
                numberOfInstancesByCardIndex[it] = oldValue + instances
            }
    }

    return numberOfInstancesByCardIndex.values.sum()
}

private fun String.parse(): List<Card> {
    fun String.parseLine(): Card {
        val (_, winningNumbersString, actualNumbersString) = split(": ", " | ")
        fun String.parseNumbers() = splitToSequence(" ")
            .filterNot { it.isBlank() }
            .map { it.trim().toUInt() }
            .toSet()

        val winningNumbers = winningNumbersString.parseNumbers()
        val actualNumbers = actualNumbersString.parseNumbers()
        return Card(winningNumbers, actualNumbers)
    }
    return lineSequence()
        .map { it.parseLine() }
        .toList()
}

private data class Card(
    val winningNumbers: Set<UInt>,
    val cardNumbers: Set<UInt>,
) {
    val numberOfWinningNumbersInCardNumbers =
        cardNumbers.toMutableSet().apply { retainAll(winningNumbers) }.size
}
