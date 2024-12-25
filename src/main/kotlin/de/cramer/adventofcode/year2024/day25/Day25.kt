package de.cramer.adventofcode.year2024.day25

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
}

private fun problem01(input: Input): Int {
    return input.locks.sumOf { lock ->
        input.keys.count { keys ->
            lock.zip(keys) { a, b -> a + b }.all { it <= 5 }
        }
    }
}

private fun String.parse(): Input {
    fun String.parseEntry(): Pair<List<Int>?, List<Int>?> {
        val lines = lines()
        val lineCount = lines.size
        val firstLine = lines.first()
        val isLock = firstLine.first() == '#'

        val result = buildList {
            firstLine.indices.forEach { x ->
                lines.forEachIndexed { y, l ->
                    if (y == 0) { // skip first line
                        return@forEachIndexed
                    }

                    if (isLock) {
                        if (l[x] == '.') {
                            this += y - 1
                            return@forEach
                        }
                    } else {
                        if (l[x] == '#') {
                            this += lineCount - y - 1
                            return@forEach
                        }
                    }
                }
            }
        }

        return if (isLock) result to null else null to result
    }

    return splitByEmptyLines()
        .map { it.parseEntry() }
        .fold(mutableListOf<List<Int>>() to mutableListOf<List<Int>>()) { acc, (locks, keys) ->
            if (locks != null) acc.first += locks
            if (keys != null) acc.second += keys
            acc
        }.let { (locks, keys) ->
            Input(locks, keys)
        }
}

private data class Input(
    val locks: List<List<Int>>,
    val keys: List<List<Int>>,
)
