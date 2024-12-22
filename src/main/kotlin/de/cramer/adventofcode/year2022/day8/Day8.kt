package de.cramer.adventofcode.year2022.day8

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.isValidIndex
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 21)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 8)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    fun getVisibleTrees(origin: Vector, direction: Vector): Set<Vector> {
        var maxHeight = -1
        var point = origin

        return buildSet {
            while (input.isValidIndex(point)) {
                val height = input[point]
                if (maxHeight < height) {
                    maxHeight = height
                    this += point
                }
                point += direction
            }
        }
    }

    val originsWithDirections = input.indices.flatMap {
        listOf(
            Vector(0, it) to Vector(1, 0),
            Vector(input[it].size - 1, it) to Vector(-1, 0),
        )
    } + input[0].indices.flatMap {
        listOf(
            Vector(it, 0) to Vector(0, 1),
            Vector(it, input.size - 1) to Vector(0, -1),
        )
    }
    return originsWithDirections.flatMap { (origin, direction) ->
        getVisibleTrees(origin, direction)
    }.toSet().size
}

private fun problem02(input: List<List<Int>>): Int {
    @Suppress("ktlint:standard:argument-list-wrapping")
    val directions = listOf(
        Vector(1, 0), Vector(0, 1), Vector(-1, 0), Vector(0, -1),
    )

    fun Vector.getViewingDistance(direction: Vector): Int {
        var distance = 0
        val height = input[this]
        var point = this + direction
        while (input.isValidIndex(point)) {
            distance++
            if (input[point] >= height) {
                break
            }
            point += direction
        }
        return distance
    }

    fun Vector.getScenicScore(): Int = directions.map { getViewingDistance(it) }
        .reduce { acc, v -> acc * v }

    return input.indices.flatMap {
        input[it].indices.map { i -> Vector(i, it) }
    }.maxOf { it.getScenicScore() }
}

private fun String.parse(): List<List<Int>> {
    return lines()
        .map { it.toCharArray().map { c -> c.digitToInt() } }
}
