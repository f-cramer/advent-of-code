package de.cramer.adventofcode.year2021.day18

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 4140)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 3993)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<SnailfishNumber>): Int {
    val result = input.asSequence()
        .map { it.reduce() }
        .reduce { acc, snailfishNumber -> acc + snailfishNumber }
    return result.getMagnitude()
}

private fun problem02(input: List<SnailfishNumber>): Int {
    return input.indices.asSequence()
        .flatMap { i -> input.indices.map { j -> i to j } }
        .filter { (i, j) -> i != j }
        .map { (i, j) -> input[i] + input[j] }
        .maxOf { it.getMagnitude() }
}

private fun String.parse(): List<SnailfishNumber> {
    return lineSequence()
        .map { SnailfishNumber(it) }
        .toList()
}

private data class SnailfishNumber(
    val value: String,
)

private data class RangeAndValue(
    val range: IntRange,
    val value: Int,
)

private operator fun SnailfishNumber.plus(other: SnailfishNumber) =
    SnailfishNumber("[$value,${other.value}]").reduce()

private val number = """\d+""".toRegex()

private fun SnailfishNumber.getNumbers(): Sequence<RangeAndValue> = number.findAll(value)
    .map { RangeAndValue(it.range, it.value.toInt()) }

private fun SnailfishNumber.getLastNumberBefore(index: Int): RangeAndValue? = getNumbers()
    .filter { (range, _) -> index > range.last }
    .maxByOrNull { (range, _) -> range.last }

private fun SnailfishNumber.getNextNumberAfter(index: Int): RangeAndValue? = getNumbers()
    .filter { (range, _) -> index < range.first }
    .minByOrNull { (range, _) -> range.first }

private fun SnailfishNumber.explode(startIndex: Int): SnailfishNumber {
    val (left, right) = getNumbers()
        .dropWhile { it.range.first <= startIndex }
        .take(2)
        .toList()
    val range = startIndex..right.range.last + 1
    val replacedPair = value.replaceRange(range, "0")
    val increaseLeft = SnailfishNumber(replacedPair).getLastNumberBefore(range.first)?.let { (range, value) ->
        val newValue = (value + left.value).toString()
        replacedPair.replaceRange(range, newValue)
    } ?: replacedPair
    val increaseRight = SnailfishNumber(increaseLeft).getNextNumberAfter(range.first + 1)?.let { (range, value) ->
        increaseLeft.replaceRange(range, ((value + right.value).toString()))
    } ?: increaseLeft
    return SnailfishNumber(increaseRight)
}

private fun SnailfishNumber.split(rangeAndValue: RangeAndValue): SnailfishNumber {
    val (range, v) = rangeAndValue
    val left = v / 2
    val right = (v + 1) / 2
    val result = value.replaceRange(range, "[$left,$right]")
    return SnailfishNumber(result)
}

private fun SnailfishNumber.reduce(): SnailfishNumber {
    var number = this
    while (true) {
        val explosion = number.getNumbers()
            .mapNotNull { (range, _) ->
                val nestingLevel = number.getNestingLevel(range.first)
                if (nestingLevel > 4) range to nestingLevel else null
            }
            .firstOrNull()
        if (explosion != null) {
            val (range, nestingLevel) = explosion
            number = number.explode(range.first - nestingLevel + 4)
            continue
        }

        val split = number.getNumbers()
            .firstOrNull { (_, value) -> value >= 10 }
        if (split != null) {
            number = number.split(split)
            continue
        }
        break
    }
    return number
}

private val Char.nestingLevelValue: Int
    get() = when (this) {
        '[' -> 1
        ']' -> -1
        else -> 0
    }

private fun SnailfishNumber.getNestingLevel(index: Int): Int =
    value.toCharArray(0, index).sumOf { it.nestingLevelValue }

sealed interface Snailfish

data class SnailfishPair(
    val left: Snailfish,
    val right: Snailfish,
) : Snailfish

data class RegularNumber(
    val value: Int,
) : Snailfish

private data class CharBuffer(
    val string: String,
    var index: Int = 0,
) {
    fun peek(): Char = string[index]

    fun get(): Char = string[index++]

    fun requireNext(c: Char) {
        val next = get()
        require(next == c) { "expected $c but got $next" }
    }
}

private fun CharBuffer.parsePair(): SnailfishPair {
    requireNext('[')
    val left = parse()
    requireNext(',')
    val right = parse()
    requireNext(']')
    return SnailfishPair(left, right)
}

private fun CharBuffer.parseNumber(): RegularNumber {
    return RegularNumber(
        buildString {
            while (peek().isDigit()) {
                append(get())
            }
        }.toInt(),
    )
}

private fun CharBuffer.parse(): Snailfish {
    val peek = peek()
    return when {
        peek == '[' -> parsePair()
        peek.isDigit() -> parseNumber()
        else -> error("cannot parse from $peek")
    }
}

private fun SnailfishNumber.getMagnitude(): Int {
    fun Snailfish.getMagnitude(): Int = when (this) {
        is SnailfishPair -> 3 * left.getMagnitude() + 2 * right.getMagnitude()
        is RegularNumber -> value
    }

    return CharBuffer(value).parse().getMagnitude()
}
