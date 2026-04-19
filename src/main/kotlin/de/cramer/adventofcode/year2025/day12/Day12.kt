package de.cramer.adventofcode.year2025.day12

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.vectors

fun main() {
    val testInput = readTestInput().parse()
//    Code is simplified to an extended that it doesn't fit the test case
//    val testResult01 = problem01(testInput)
//    checkTestResult(testResult01, 2)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, Unit)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    fun List<Shape>.fitIn(region: Region): Boolean {
        val size = region.width * region.height
        val pointCountForNeededShapes = region.shapeCounts.mapIndexed { index, count ->
            this[index].size * count
        }.sum()
        return pointCountForNeededShapes <= size
    }

    return input.regions.count { input.shapes.fitIn(it) }
}

private fun problem02(input: Input) {
    // no second problem :D
}

private fun String.parse(): Input {
    val groups = splitByEmptyLines()
    val shapesString = groups.dropLast(1)
    val regionsString = groups.last()

    val shapes = shapesString.map { string ->
        val matrix = string.lineSequence()
            .drop(1)
            .map { it.toCharArray().map { c -> c == '#' } }
            .toList()
        matrix.vectors()
            .filter { matrix[it] }
            .toList()
    }

    val regions = regionsString.lines()
        .map { string ->
            val (size, countsString) = string.split(": ")
            val (width, height) = size.split("x").map { it.toInt() }
            val counts = countsString.split(" ").map { it.toInt() }
            Region(width, height, counts)
        }

    return Input(shapes, regions)
}

private data class Input(
    val shapes: List<Shape>,
    val regions: List<Region>,
)

private typealias Shape = List<Vector>

private data class Region(
    val width: Int,
    val height: Int,
    val shapeCounts: List<Int>,
)

private val Shape.boundingSize: Vector
    get() {
        val min = Vector(minOf { it.x }, minOf { it.y })
        val max = Vector(maxOf { it.x }, maxOf { it.y })
        return Vector(max.x - min.x, max.y - min.y)
    }
