package de.cramer.adventofcode.year2022.day13

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 13)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 140)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Pair<Packet, Packet>>): Int {
    return input.mapIndexedNotNull { index, (left, right) ->
        if (isInCorrectOrder(left, right)) index + 1 else null
    }.sum()
}

private fun problem02(input: List<Pair<Packet, Packet>>): Int {
    val divider1 = ListPacket(ListPacket(NumberPacket(2)))
    val divider2 = ListPacket(ListPacket(NumberPacket(6)))

    val sortedPackets = sequence {
        yield(divider1)
        yield(divider2)
        input.forEach { (first, second) ->
            yield(first)
            yield(second)
        }
    }.sortedWith { o1, o2 ->
        if (isInCorrectOrder(o1, o2)) -1 else 1
    }.toList()

    return (sortedPackets.indexOf(divider1) + 1) * (sortedPackets.indexOf(divider2) + 1)
}

private fun isInCorrectOrder(left: Packet, right: Packet): Boolean {
    fun impl(left: Packet, right: Packet): Boolean? {
        return when (left) {
            is NumberPacket -> when (right) {
                is NumberPacket -> {
                    if (left.number < right.number) {
                        true
                    } else if (left.number > right.number) {
                        false
                    } else {
                        null
                    }
                }

                is ListPacket -> impl(ListPacket(left), right)
            }

            is ListPacket -> when (right) {
                is NumberPacket -> impl(left, ListPacket(right))
                is ListPacket -> {
                    val rightIndices = right.list.indices
                    for ((index, leftSubPacket) in left.list.withIndex()) {
                        if (index in rightIndices) {
                            val rightSubPacket = right.list[index]
                            val subResult = impl(leftSubPacket, rightSubPacket)
                            if (subResult != null) {
                                return subResult
                            }
                        } else {
                            return false
                        }
                    }
                    impl(NumberPacket(left.list.size), NumberPacket(right.list.size))
                }
            }
        }
    }

    return impl(left, right)!!
}

private fun String.parse(): List<Pair<Packet, Packet>> {
    return splitByEmptyLines()
        .map {
            val (first, second) = it.lines()
            first.toPacket() to second.toPacket()
        }
}

private val allDigits = '0'..'9'

private fun String.toPacket(): Packet {
    val iterator = peekableIterator()
    return iterator.parseNext()
}

private fun PeekableCharIterator.parseList(): ListPacket {
    expectNext('[')
    val subPackets = buildList {
        while (peek() != ']') {
            add(parseNext())
            if (peek() == ',') {
                expectNext(',')
            }
        }
        expectNext(']')
    }
    return ListPacket(subPackets)
}

private fun PeekableCharIterator.parseNumber(): NumberPacket {
    val number = buildString {
        append(expectNext(allDigits))
        while (peek() in allDigits) {
            append(expectNext(allDigits))
        }
    }.toInt()
    return NumberPacket(number)
}

private fun PeekableCharIterator.parseNext(): Packet {
    check(hasNext())
    return when (val next = peek()) {
        '[' -> parseList()
        in allDigits -> parseNumber()
        else -> throw IllegalStateException("cannot parse Packet starting with '$next'")
    }
}

private sealed interface Packet

private data class ListPacket(
    val list: List<Packet>,
) : Packet {
    constructor(packet: Packet) : this(listOf(packet))
}

private data class NumberPacket(
    val number: Int,
) : Packet

private fun String.peekableIterator() = PeekableCharIterator(toCharArray())

private class PeekableCharIterator(
    private val chars: CharArray,
) : CharIterator() {
    var index: Int = 0

    override fun hasNext(): Boolean = index < chars.size

    override fun nextChar(): Char = if (hasNext()) {
        chars[index++]
    } else {
        throw NoSuchElementException()
    }

    fun peek(): Char = chars[index]

    fun expectNext(): Char = if (!hasNext()) {
        throw IllegalStateException()
    } else {
        nextChar()
    }

    fun expectNext(char: Char) {
        val next = expectNext()
        if (next != char) {
            throw IllegalStateException("next char '$next' does not match expected '$char'")
        }
    }

    fun expectNext(anyOf: Iterable<Char>): Char {
        val next = expectNext()
        return if (next in anyOf) {
            next
        } else {
            throw IllegalStateException("next char '$next' does not match expectation $anyOf")
        }
    }
}
