package de.cramer.adventofcode.year2021.day5

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 5)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 12)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Line>): Int {
    return input.asSequence()
        .filter { it.from.x == it.to.x || it.from.y == it.to.y }
        .flatMap { it.from..it.to }
        .groupingBy { it }
        .eachCount()
        .filterValues { it > 1 }
        .size
}

private fun problem02(input: List<Line>): Int {
    return input.asSequence()
        .flatMap { it.from..it.to }
        .groupingBy { it }
        .eachCount()
        .filterValues { it > 1 }
        .size
}

private fun String.parse(): List<Line> {
    fun String.toLine(): Line {
        val (fromX, fromY, toX, toY) = split(",", " -> ")
        return Line(Vector(fromX.toInt(), fromY.toInt()), Vector(toX.toInt(), toY.toInt()))
    }

    return lineSequence()
        .map { it.toLine() }
        .toList()
}

private data class Line(
    val from: Vector,
    val to: Vector,
)

private operator fun Vector.rangeTo(other: Vector): List<Vector> =
    if (x == other.x) {
        (min(y, other.y)..max(y, other.y)).map { Vector(x, it) }
    } else if (y == other.y) {
        (min(x, other.x)..max(x, other.x)).map { Vector(it, y) }
    } else if (abs(x - other.x) == abs(y - other.y)) {
        val xSign = (other.x - x).sign
        val ySign = (other.y - y).sign
        (0..abs(x - other.x)).map { Vector(x + it * xSign, y + it * ySign) }
    } else {
        error("cannot create diagonal line")
    }
