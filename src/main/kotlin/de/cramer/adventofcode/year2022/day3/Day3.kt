package de.cramer.adventofcode.year2022.day3

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 157)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 70)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Backpack>): Int {
    return input.sumOf {
        val commonElements = it.firstCompartment intersect it.secondCompartment.toSet()
        commonElements.single().priority
    }
}

private fun problem02(input: List<Backpack>): Int {
    return input.chunked(3).sumOf { b ->
        b.asSequence()
            .map { it.items }
            .reduce { acc, chars -> acc intersect chars }
            .single().priority
    }
}

private fun String.parse(): List<Backpack> {
    return lineSequence()
        .map { it.toCharArray().splitInHalf() }
        .map { (first, second) -> Backpack(first, second) }
        .toList()
}

private fun CharArray.splitInHalf(): List<List<Char>> {
    val size = size
    require(size % 2 == 0) { "input has to be of even size" }
    val list = toList()
    return listOf(
        list.subList(0, size / 2),
        list.subList(size / 2, size),
    )
}

private val priorities: Map<Char, Int> by lazy {
    buildMap {
        ('a'..'z').forEach {
            this[it] = it - 'a' + 1
        }
        ('A'..'Z').forEach {
            this[it] = it - 'A' + 27
        }
    }
}

private val Char.priority: Int
    get() = priorities[this] ?: error("no priority found for item $this")

private data class Backpack(
    val firstCompartment: List<Char>,
    val secondCompartment: List<Char>,
)

private val Backpack.items: Set<Char>
    get() = (firstCompartment + secondCompartment).toSet()
