package de.cramer.adventofcode.year2025.day7

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.vectors
import java.util.stream.Collectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 21)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 40)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    var totalSplits = 0
    var xs = setOf(input.start.x)
    for (y in 1 until input.depth) {
        val currentSplitters = input.splitterXsByYs[y] ?: continue
        totalSplits += xs.count { x -> x in currentSplitters }
        xs = xs.flatMap { x ->
            if (x in currentSplitters) {
                setOf(x - 1, x + 1)
            } else {
                setOf(x)
            }
        }.toSet()
    }
    return totalSplits
}

private fun problem02(input: Input): Long {
    var numberOfPathsTo = mutableMapOf(input.start.x to 1L)
    // x to origin x
    var xs = setOf(input.start.x to input.start.x)
    for (y in 1..<input.depth) {
        val currentSplitters = input.splitterXsByYs[y] ?: continue
        val newPaths = xs.map { (x, origin) -> x to (numberOfPathsTo[origin] ?: 0L) }
        xs = xs.flatMap { (x, origin) ->
            if (x in currentSplitters) {
                setOf(x - 1 to x, x + 1 to x)
            } else {
                setOf(x to x)
            }
        }.toSet()

        numberOfPathsTo = newPaths.groupingBy { (x) -> x }
            .foldTo(mutableMapOf(), 0L) { acc, (_, count) -> acc + count }
    }

    return xs.sumOf { (_, origin) -> numberOfPathsTo[origin] ?: 0 }
}

private fun String.parse(): Input {
    var start = Vector.ZERO // some dummy
    val lines = lines()
    val chars = lines.windowed(2, 2) { it.first().toCharArray().toList() } // drop each second row and get chars of remaining
    val splitters = chars.vectors().filter {
        when (chars[it]) {
            '^' -> true
            'S' -> {
                start = it
                false
            }

            else -> false
        }
    }.toSet()
    val splitterXsByYs = splitters.stream()
        .collect(Collectors.groupingBy({ it.y }, Collectors.mapping({ it.x }, Collectors.toSet())))
    return Input(start, splitterXsByYs, chars.size)
}

private data class Input(
    val start: Vector,
    val splitterXsByYs: Map<Int, Set<Int>>,
    val depth: Int,
)
