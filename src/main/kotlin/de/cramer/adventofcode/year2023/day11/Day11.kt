package de.cramer.adventofcode.year2023.day11

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.cartesianProduct
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.size
import de.cramer.adventofcode.utils.vectors
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 374)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 82000210L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(universe: Universe): Int {
    val (expandedColumns, expandedRows) = universe.expand()

    return universe.galaxies.cartesianProduct(universe.galaxies)
        .sumOf { (a, b) ->
            val xDiff = min(a.x, b.x)..max(a.x, b.x)
            val yDiff = min(a.y, b.y)..max(a.y, b.y)
            val expandedXDiff = expandedColumns.count { it in xDiff }
            val expandedYDiff = expandedRows.count { it in yDiff }
            xDiff.size + expandedXDiff + yDiff.size + expandedYDiff
        } / 2
}

private fun problem02(universe: Universe): Long {
    val (expandedColumns, expandedRows) = universe.expand()

    return universe.galaxies.cartesianProduct(universe.galaxies)
        .sumOf { (a, b) ->
            val xDiff = min(a.x, b.x)..max(a.x, b.x)
            val yDiff = min(a.y, b.y)..max(a.y, b.y)
            val expandedXDiff = expandedColumns.count { it in xDiff } * 999_999L
            val expandedYDiff = expandedRows.count { it in yDiff } * 999_999L
            xDiff.size + expandedXDiff + yDiff.size + expandedYDiff
        } / 2
}

private fun String.parse(): Universe {
    val lines = lines().map { it.toCharArray().toList() }
    val size = Vector(lines.last().size, lines.size)
    val galaxies = lines.vectors()
        .filter { lines[it] == '#' }
        .toSet()
    return Universe(galaxies, size)
}

private data class Universe(
    val galaxies: Set<Vector>,
    val size: Vector,
)

private data class Expansion(
    val expandedColumns: Set<Int>,
    val expandedRows: Set<Int>,
)

private fun Universe.expand(): Expansion {
    val expandedColumns = buildSet {
        for (x in 0..this@expand.size.x) {
            if (galaxies.none { it.x == x }) {
                this += x
            }
        }
    }
    val expandedRows = buildSet {
        for (y in 0..this@expand.size.y) {
            if (galaxies.none { it.y == y }) {
                this += y
            }
        }
    }

    return Expansion(expandedColumns, expandedRows)
}
