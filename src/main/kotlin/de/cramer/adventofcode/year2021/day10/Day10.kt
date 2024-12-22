package de.cramer.adventofcode.year2021.day10

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 26397)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 288957)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Char>>): Long {
    return input.asSequence()
        .map { it.getFirstWrongCharacter() }
        .filterIsInstance<Left<Char>>()
        .map { it.value }
        .sumOf { errorValues.getValue(it) }
}

private fun problem02(input: List<List<Char>>): Long {
    fun ArrayDeque<Char>.getScore(): Long {
        var score = 0L
        for (character in this.asReversed()) {
            score *= 5
            score += autocompleteValues.getValue(character)
        }
        return score
    }

    val scores = input.asSequence()
        .map { it.getFirstWrongCharacter() }
        .filterIsInstance<Right<ArrayDeque<Char>>>()
        .map { it.value }
        .map { it.getScore() }
        .sorted()
        .toList()
    return scores[scores.size / 2]
}

private val characters = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

private val errorValues = mapOf(
    ')' to 3L,
    ']' to 57L,
    '}' to 1197L,
    '>' to 25137L,
)

private val autocompleteValues = mapOf(
    ')' to 1L,
    ']' to 2L,
    '}' to 3L,
    '>' to 4L,
)

private fun List<Char>.getFirstWrongCharacter(): Either<Char, ArrayDeque<Char>> {
    val stack = ArrayDeque<Char>()
    for (character in this) {
        if (character in characters.keys) {
            stack += characters.getValue(character)
        } else if (character in characters.values) {
            val expectedClosingCharacter = stack.removeLast()
            if (expectedClosingCharacter != character) {
                return Left(character)
            }
        }
    }
    return Right(stack)
}

private fun String.parse(): List<List<Char>> {
    return lineSequence()
        .map { l -> l.toCharArray().toList() }
        .toList()
}

private sealed interface Either<out A, out B>

private data class Left<out A>(val value: A) : Either<A, Nothing>

private data class Right<out B>(val value: B) : Either<Nothing, B>
