package de.cramer.adventofcode.year2021.day15

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.isValidIndex
import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 40)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 315)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Int>>): Int {
    val map = HeightMap(input, Vector(0, 0), Vector(input[0].lastIndex, input.lastIndex))
    return map.findShortestPathLength()
}

private fun problem02(input: List<List<Int>>): Int {
    val topLeft = Vector(0, 0)
    val yLength = input.size

    val grid = (0..<yLength * 5).map { y ->
        val yField = y / yLength
        val xLength = input[y % yLength].size
        (0..<xLength * 5).map { x ->
            val vectorInTopLeft = Vector(x % xLength, y % yLength)
            val topLeftValue = input[vectorInTopLeft]

            val fieldVector = Vector(x / xLength, yField)
            val distance = topLeft.getDistanceTo(fieldVector)
            val newValue = (topLeftValue + distance) % 10
            if (newValue < topLeftValue) newValue + 1 else newValue
        }
    }
    val map = HeightMap(grid, Vector(0, 0), Vector(grid[0].lastIndex, grid.lastIndex))
    return map.findShortestPathLength()
}

private fun String.parse(): List<List<Int>> {
    return lineSequence()
        .map { l -> l.map { it.digitToInt() } }
        .toList()
}

private data class HeightMap(
    val grid: List<List<Int>>,
    val start: Vector,
    val end: Vector,
) {
    operator fun get(vector: Vector) = grid[vector]

    @Suppress("ktlint:standard:argument-list-wrapping")
    fun getNeighbors(vector: Vector) = sequenceOf(
        Vector(vector.x - 1, vector.y), Vector(vector.x + 1, vector.y),
        Vector(vector.x, vector.y - 1), Vector(vector.x, vector.y + 1),
    )
        .filter { grid.isValidIndex(it) }
}

private fun HeightMap.findShortestPathLength(): Int {
    data class VectorWithDistance(val vector: Vector, val distance: Int)

    val priorityQueue = PriorityQueue<VectorWithDistance>(compareBy { it.distance })
    val visited = mutableSetOf<Vector>()
    val totalRiskLevel = mutableMapOf<Vector, Int>()

    totalRiskLevel[start] = 0
    priorityQueue += VectorWithDistance(start, 0)

    while (priorityQueue.isNotEmpty()) {
        val (location, distance) = priorityQueue.remove()
        if (location == end) break
        visited += location

        val previousRiskLevel = totalRiskLevel[location]
        if (previousRiskLevel == null || previousRiskLevel < distance) continue

        for (neighbors in getNeighbors(location).filterNot { it in visited }) {
            val newRiskLevel = previousRiskLevel + this[neighbors]
            if (newRiskLevel < totalRiskLevel.getOrDefault(neighbors, Int.MAX_VALUE)) {
                totalRiskLevel[neighbors] = newRiskLevel
                priorityQueue += VectorWithDistance(neighbors, newRiskLevel)
            }
        }
    }

    return totalRiskLevel[end]!!
}

private fun Vector.getDistanceTo(other: Vector): Int {
    val dx = abs(x - other.x)
    val dy = abs(y - other.y)
    return dx + dy
}
