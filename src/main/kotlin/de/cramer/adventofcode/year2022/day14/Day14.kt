package de.cramer.adventofcode.year2022.day14

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 24)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 93)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Vector>): Int {
    val maxY = input.maxOf { it.y }
    return simulateSingleCornOfSand(input, true) {
        it.y > maxY
    }.size
}

private fun problem02(input: List<Vector>): Int {
    val maxY = input.maxOf { it.y } + 2
    return simulateSingleCornOfSand(input, false) {
        it.y >= maxY
    }.size
}

private fun simulateSingleCornOfSand(stones: List<Vector>, breakIfStepConditionFalse: Boolean, stepCondition: (Vector) -> Boolean): List<Vector> {
    @Suppress("ktlint:standard:argument-list-wrapping")
    fun Vector.possibleNextSteps() = sequenceOf(
        Vector(x, y + 1), Vector(x - 1, y + 1), Vector(x + 1, y + 1),
    )

    val cornsOfSand = mutableSetOf<Vector>()

    fun Vector.step(): Vector? = possibleNextSteps().firstOrNull { it !in stones && it !in cornsOfSand }

    val sandStart = Vector(500, 0)
    while (true) {
        var sand: Vector
        var next: Vector? = sandStart

        do {
            sand = next!!
            next = sand.step()
        } while (next != null && !stepCondition(next))

        if (next != null && breakIfStepConditionFalse) {
            break
        }

        if (!cornsOfSand.add(sand)) {
            break
        }
    }

    return cornsOfSand.toList()
}

private fun String.parse(): List<Vector> {
    fun String.toVector(): Vector = split(",").let { (first, second) ->
        Vector(first.toInt(), second.toInt())
    }

    fun getPathBetween(start: Vector, end: Vector): List<Vector> =
        if (start.x == end.x) {
            val s = min(start.y, end.y)
            val e = max(start.y, end.y)
            (s..e).map { Vector(start.x, it) }
        } else if (start.y == end.y) {
            val s = min(start.x, end.x)
            val e = max(start.x, end.x)
            (s..e).map { Vector(it, start.y) }
        } else {
            error("cannot create diagonal paths")
        }

    fun String.toPathElements(): Sequence<Vector> = splitToSequence(" -> ")
        .map { it.toVector() }
        .zipWithNext { start, end -> getPathBetween(start, end) }
        .flatten()

    return lines()
        .flatMap { it.toPathElements() }
}
