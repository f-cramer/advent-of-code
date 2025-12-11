package de.cramer.adventofcode.year2025.day11

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.readTestInput2
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput01 = readTestInput().parse()
    val testResult01 = problem01(testInput01)
    checkTestResult(testResult01, 5)
    val testInput02 = readTestInput2().parse()
    val testResult02 = problem02(testInput02)
    checkTestResult(testResult02, 2)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Node>): Long {
    return findNumberOfPaths(input, "you", "out")
}

private fun problem02(input: List<Node>): Long {
    val start = "svr"
    val output = "out"
    val dac = "dac"
    val fft = "fft"

    val inputWithoutOutput = input.map { if (output !in it.outputs) it else it.copy(outputs = it.outputs - output) }

    val startToFftPaths = findNumberOfPaths(inputWithoutOutput, start, fft)
    val fftToDacPaths = findNumberOfPaths(inputWithoutOutput, fft, dac)
    val dacToOutputPaths = findNumberOfPaths(input, dac, output)
    val startToDacPaths = findNumberOfPaths(inputWithoutOutput, start, dac)
    val dacToFftPaths = findNumberOfPaths(inputWithoutOutput, dac, fft)
    val fftToOutputPaths = findNumberOfPaths(input, fft, output)

    return (startToFftPaths * fftToDacPaths * dacToOutputPaths) + (startToDacPaths * dacToFftPaths * fftToOutputPaths)
}

private fun String.parse(): List<Node> {
    fun String.parseNode(): Node {
        val (name, outputs) = split(": ")
        return Node(name, outputs.split(" ").toSet())
    }

    return lines().map { it.parseNode() }
}

private fun findNumberOfPaths(input: List<Node>, start: String, end: String): Long {
    val nodes = input.associate { it.name to it.outputs }
    val pathLengths = mutableMapOf<String, Long>()

    fun findNumberOfPaths(node: String, visited: Set<String> = emptySet()): Long {
        if (node == end) {
            return 1
        }
        if (node in pathLengths) {
            return pathLengths.getValue(node)
        }

        val count = nodes.getValue(node).sumOf { findNumberOfPaths(it, visited + node) }
        pathLengths[node] = count
        return count
    }

    return findNumberOfPaths(start)
}

private data class Node(
    val name: String,
    val outputs: Set<String>,
)
