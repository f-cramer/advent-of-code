package de.cramer.adventofcode.year2024.day19

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines
import java.util.PriorityQueue

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 6)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 16)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    fun String.isPossible(towels: List<String>): Boolean {
        val length = length
        val queue = PriorityQueue<Int>(reverseOrder()) // start index
        queue += 0
        while (queue.isNotEmpty()) {
            val index = queue.poll()!!
            if (index == length) {
                return true
            }

            val substring = substring(index)
            for (towel in towels) {
                if (substring.startsWith(towel)) {
                    queue += index + towel.length
                }
            }
        }

        return false
    }

    return input.patterns.count {
        it.isPossible(input.towels)
    }
}

private fun problem02(input: Input): Long {
    val cache = mutableMapOf<String, Long>()
    fun String.getPossibleArrangementsCount(): Long {
        val cachedValue = cache[this]
        if (cachedValue != null) {
            return cachedValue
        }

        var count = if (this in input.towels) 1L else 0L
        for (towel in input.towels) {
            if (startsWith(towel)) {
                count += substring(towel.length).getPossibleArrangementsCount()
            }
        }
        cache[this] = count
        return count
    }

    return input.patterns.sumOf {
        it.getPossibleArrangementsCount()
    }
}

private fun String.parse(): Input {
    val (towelsString, patternsString) = splitByEmptyLines()
    val towels = towelsString.split(", ")
    val patterns = patternsString.lines()
    return Input(towels, patterns)
}

private data class Input(
    val towels: List<String>,
    val patterns: List<String>,
)
