package de.cramer.adventofcode.year2020.day7

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 4)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 32)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Bag>): Int {
    fun String.addAllContainers(containers: MutableSet<String>) {
        containers += this
        input.asSequence()
            .filter { b -> b.contained.any { it.color == this } }
            .map { it.color }
            .filterNot { it in containers }
            .forEach { it.addAllContainers(containers) }
    }

    return buildSet { startingBag.addAllContainers(this) }.size - 1
}

private fun problem02(input: List<Bag>): Long {
    val containedByColor = input.associate { it.color to it.contained }

    fun String.getContainedBags(): Long = containedByColor.getValue(this)
        .sumOf { it.count * (1 + it.color.getContainedBags()) }

    return startingBag.getContainedBags()
}

const val startingBag = "shiny gold"
private val bagRegex = """(.+) bags contain (.+)\.""".toRegex()
private val containedRegex = """(\d+) ([^,]+) bags?""".toRegex()

private fun String.parse(): List<Bag> {
    fun MatchResult.toContained(): BagAndCount {
        val (count, color) = destructured
        return BagAndCount(color, count.toInt())
    }

    fun String.toBag(): Bag {
        val match = bagRegex.matchEntire(this) ?: error("cannot parse $this to bag")
        val (color, contained) = match.destructured
        val c = containedRegex.findAll(contained)
            .map { it.toContained() }
            .toList()
        return Bag(color, c)
    }

    return lineSequence()
        .map { it.toBag() }
        .toList()
}

private data class Bag(
    val color: String,
    val contained: List<BagAndCount>,
)

private data class BagAndCount(
    val color: String,
    val count: Int,
)
