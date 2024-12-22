package de.cramer.adventofcode.year2021.day12

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 10)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 36)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Map<String, List<String>>): Int {
    fun String.getPathsToEnd(alreadyVisitedSmallCaves: Set<String>): Set<List<String>>? {
        if (this == "end") {
            return setOf(listOf("end"))
        }

        val smallCaves = if (isSmallCave()) alreadyVisitedSmallCaves + this else alreadyVisitedSmallCaves
        val nextLocations = input[this] ?: return null
        return nextLocations.asSequence()
            .filterNot { it in alreadyVisitedSmallCaves }
            .flatMap { it.getPathsToEnd(smallCaves) ?: emptyList() }
            .map { listOf(this) + it }
            .toSet()
    }

    return "start".getPathsToEnd(emptySet())?.size ?: 0
}

private fun problem02(input: Map<String, List<String>>): Int {
    fun String.getPathsToEnd(smallCaveToVisitTwice: String?, alreadyVisitedSmallCaves: Set<String>): Set<List<String>>? {
        if (this == "end") {
            return setOf(listOf("end"))
        }

        val smallCaves = if (isSmallCave() && smallCaveToVisitTwice != this) alreadyVisitedSmallCaves + this else alreadyVisitedSmallCaves
        val caveToVisitTwice = smallCaveToVisitTwice?.takeUnless { it == this }
        val nextLocations = input[this] ?: return null
        return nextLocations.asSequence()
            .filterNot { it in alreadyVisitedSmallCaves }
            .flatMap { it.getPathsToEnd(caveToVisitTwice, smallCaves) ?: emptyList() }
            .map { listOf(this) + it }
            .toSet()
    }

    return input.keys.asSequence()
        .filterNot { it == "start" || it == "end" }
        .filter { it.isSmallCave() }
        .flatMap { "start".getPathsToEnd(it, emptySet()) ?: emptySet() }
        .distinct()
        .count()
}

private fun String.parse(): Map<String, List<String>> {
    fun String.toPath(): Pair<String, String> {
        val (from, to) = split("-")
        return from to to
    }
    return lineSequence()
        .map { it.toPath() }
        .flatMap { sequenceOf(it, Pair(it.second, it.first)) }
        .groupBy({ it.first }) { it.second }
}

private fun String.isSmallCave() = all { it.isLowerCase() }
