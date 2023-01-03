package de.cramer.adventofcode.year2021.day14

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 1588L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2188189693529L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Long {
    var polymers = input.template
    repeat(10) {
        polymers = polymers.applyRules(input.insertionRules)
    }
    val counts = polymers.getCharCounts(input.lastTemplateChar).values
    return counts.max() - counts.min()
}

private fun problem02(input: Input): Long {
    var polymers = input.template
    repeat(40) {
        polymers = polymers.applyRules(input.insertionRules)
    }
    val counts = polymers.getCharCounts(input.lastTemplateChar).values
    return counts.max() - counts.min()
}

private fun String.parse(): Input {
    fun String.toTemplate(): Map<Pair<Char, Char>, Long> = windowed(2) { it[0] to it[1] }
        .groupingBy { it }
        .eachCount()
        .mapValues { (_, value) -> value.toLong() }

    fun String.toRule(): Pair<Pair<Char, Char>, Char> {
        val (input, output) = split(" -> ")
        val (left, right) = input.toCharArray()
        return (left to right) to output.single()
    }

    fun String.toRules() = lineSequence()
        .map { it.toRule() }
        .toMap()

    val (template, rules) = splitByEmptyLines()
    return Input(template.toTemplate(), rules.toRules(), template.last())
}

private fun Map<Pair<Char, Char>, Long>.applyRules(rules: Map<Pair<Char, Char>, Char>): Map<Pair<Char, Char>, Long> = sequence {
    for ((key, value) in this@applyRules) {
        val result = rules.getValue(key)
        yield((key.first to result) to value)
        yield((result to key.second) to value)
    }
}
    .groupingBy { it.first }
    .aggregate { _, accumulator, element, _ -> (accumulator ?: 0L) + element.second }

private fun Map<Pair<Char, Char>, Long>.getCharCounts(lastChar: Char): Map<Char, Long> = asSequence()
    .flatMap { (chars, count) -> sequenceOf(chars.first to count, chars.second to count) }
    .groupingBy { it.first }
    .aggregate { _, accumulator: Long?, element, _ -> (accumulator ?: 0) + element.second }
    .toMutableMap().apply {
        keys.forEach { this[it] = getValue(it) / 2 }
        this[lastChar] = getValue(lastChar) + 1L
    }

private data class Input(
    val template: Map<Pair<Char, Char>, Long>,
    val insertionRules: Map<Pair<Char, Char>, Char>,
    val lastTemplateChar: Char,
)
