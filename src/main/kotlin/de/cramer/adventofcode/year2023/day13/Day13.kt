package de.cramer.adventofcode.year2023.day13

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.set
import de.cramer.adventofcode.utils.splitByEmptyLines
import de.cramer.adventofcode.utils.vectors
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 405)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 400)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(patterns: List<Pattern>): Int {
    return patterns.sumOf { it.summarize() }
}

private fun problem02(patterns: List<Pattern>): Int {
    return patterns.sumOf { pattern ->
        val oldResult = pattern.summarize()
        val ignoredColumn = if (oldResult < 100) oldResult else null
        val ignoredRow = if (oldResult >= 100) oldResult / 100 else null

        pattern.vectors().firstNotNullOf { v ->
            val copy = pattern.asSequence()
                .map { it.toMutableList() }
                .toMutableList()
            copy[v] = when (copy[v]) {
                Tile.EMPTY -> Tile.MIRROR
                Tile.MIRROR -> Tile.EMPTY
            }
            runCatching { copy.summarize(ignoredColumn, ignoredRow) }.getOrNull()
        }
    }
}

private fun Pattern.summarize(ignoredColumn: Int? = null, ignoredRow: Int? = null): Int {
    val height = size
    val width = first().size

    val splitX = (1..<width).firstOrNull { splitX ->
        if (ignoredColumn == splitX) return@firstOrNull false

        val length = min(splitX, width - splitX)
        all {
            val left = it.subList(splitX - length, splitX)
            val right = it.subList(splitX, splitX + length).reversed()
            left == right
        }
    }
    if (splitX != null) {
        return splitX
    }

    val splitY = (1..<height).firstOrNull { splitY ->
        if (ignoredRow == splitY) return@firstOrNull false

        val length = min(splitY, height - splitY)
        (0..<width).all { x ->
            val top = (splitY - length..<splitY).map { y -> this[y][x] }
            val bottom = (splitY..<splitY + length).map { y -> this[y][x] }.reversed()
            top == bottom
        }
    }
    if (splitY != null) {
        return splitY * 100
    }

    error("could not summarize")
}

private fun String.parse(): List<Pattern> {
    fun Char.parseTile() = when (this) {
        '#' -> Tile.MIRROR
        '.' -> Tile.EMPTY
        else -> error("cannot parse $this to tile")
    }

    fun String.parsePattern() = lines()
        .map { l -> l.map { it.parseTile() } }

    return splitByEmptyLines()
        .map { it.parsePattern() }
}

private typealias Pattern = List<List<Tile>>

private enum class Tile {
    EMPTY,
    MIRROR,
}
