package de.cramer.adventofcode.year2023.day7

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 6440u)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 5905u)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(inputs: List<Input>): ULong {
    fun List<Card>.getType(): Type {
        val cardsWithCount = groupingBy { it }.eachCount()
        val differentCards = cardsWithCount.size
        val values = cardsWithCount.values
        return getType(differentCards, values)
    }

    val typesByInput = inputs.associateWith { it.cards.getType() }
    val comparator = compareByDescending<Input> { typesByInput.getValue(it) }
        .thenByDescending { it.cards[0] }
        .thenByDescending { it.cards[1] }
        .thenByDescending { it.cards[2] }
        .thenByDescending { it.cards[3] }
        .thenByDescending { it.cards[4] }

    return inputs.getTotalWinnings(comparator)
}

private fun problem02(inputs: List<Input>): ULong {
    fun List<Card>.getType(): Type {
        val cardsWithCount = groupingBy { it }.eachCount().toMutableMap()
        val numberOfJokers = cardsWithCount.remove(Card.J)
        if (numberOfJokers != null) {
            if (numberOfJokers == 5) {
                cardsWithCount[Card.A] = numberOfJokers
            } else {
                val key = cardsWithCount.maxBy { it.value }.key
                cardsWithCount.merge(key, numberOfJokers) { a, b -> a + b }
            }
        }
        val differentCards = cardsWithCount.size
        val values = cardsWithCount.values
        return getType(differentCards, values)
    }

    val typesByInput = inputs.associateWith { it.cards.getType() }
    val cardComparator = compareByDescending<Card> { if (it == Card.J) Int.MAX_VALUE else it.ordinal }
    val comparator = compareByDescending<Input> { typesByInput.getValue(it) }
        .thenBy(cardComparator) { it.cards[0] }
        .thenBy(cardComparator) { it.cards[1] }
        .thenBy(cardComparator) { it.cards[2] }
        .thenBy(cardComparator) { it.cards[3] }
        .thenBy(cardComparator) { it.cards[4] }

    return inputs.getTotalWinnings(comparator)
}

private fun List<Input>.getTotalWinnings(comparator: Comparator<Input>): ULong = sortedWith(comparator)
    .mapIndexed { index, input ->
        input.bid * ((index + 1).toUInt())
    }.sum()

private fun String.parse(): List<Input> {
    fun String.parseLine(): Input {
        val (cardsString, bidString) = split(" ")
        val cards = cardsString.toCharArray().map {
            Card.valueOf(if (it.isDigit()) "N$it" else "$it")
        }
        return Input(cards, bidString.toULong())
    }
    return lineSequence().map { it.parseLine() }.toList()
}

private fun getType(differentCards: Int, cardCounts: Collection<Int>): Type {
    return when (differentCards) {
        1 -> Type.FIVE_OF_A_KIND
        2 -> if (4 in cardCounts) {
            Type.FOUR_OF_A_KIND
        } else if (3 in cardCounts) {
            Type.FULL_HOUSE
        } else {
            error("cannot happen")
        }

        3 -> if (3 in cardCounts) {
            Type.THREE_OF_A_KIND
        } else {
            Type.TWO_PAIR
        }

        4 -> Type.ONE_PAIR
        5 -> Type.HIGH_CARD
        else -> error("cannot happen")
    }
}

private data class Input(
    val cards: List<Card>,
    val bid: ULong,
)

private enum class Card {
    A,
    K,
    Q,
    J,
    T,
    N9,
    N8,
    N7,
    N6,
    N5,
    N4,
    N3,
    N2,
}

private enum class Type {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD,
}
