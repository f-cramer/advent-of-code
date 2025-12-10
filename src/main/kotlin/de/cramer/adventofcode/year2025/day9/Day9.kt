package de.cramer.adventofcode.year2025.day9

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import java.awt.Point
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.geom.Area
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 50)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 24)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Vector>): Long {
    return input.asSequence()
        .flatMapIndexed { index, v -> input.subList(index + 1, input.size).map { v to it } }
        .maxOf { it.area }
}

private fun problem02(input: List<Vector>): Long {
    fun Vector.toPoint() = Point(x, y)
    val polygon = Polygon()
    input.forEach { polygon.addPoint(it.x, it.y) }
    val area = Area(polygon)

    return input.asSequence()
        .flatMapIndexed { index, v -> input.subList(index + 1, input.size).map { v to it } }
        .filter { (v1, v2) -> v1 != v2 }
        .filter { (v1, v2) ->
            val rectangle = Rectangle(v1.toPoint())
            rectangle.add(v2.toPoint())
            area.contains(rectangle)
        }
        .maxOf { it.area }
}

private fun String.parse(): List<Vector> {
    return lines()
        .map { it.split(",") }
        .map { (x, y) -> Vector(x.toInt(), y.toInt()) }
}

private val Pair<Vector, Vector>.area: Long
    get() = (abs(first.x - second.x).toLong() + 1) * (abs(first.y - second.y).toLong() + 1)
