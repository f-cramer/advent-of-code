package de.cramer.adventofcode.year2022.day18

import de.cramer.adventofcode.Vector3
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runTest
import java.util.LinkedList
import java.util.Queue

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 64)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 58)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input) }
}

private fun problem01(input: Set<Vector3>): Int {
    return input.sumOf { v ->
        v.getNeighbors()
            .count { it !in input }
    }
}

private fun problem02(input: Set<Vector3>): Int {
    val minX = input.minOf { it.x } - 1
    val xRange = minX..input.maxOf { it.x } + 1
    val minY = input.minOf { it.y } - 1
    val yRange = minY..input.maxOf { it.y } + 1
    val minZ = input.minOf { it.z } - 1
    val zRange = minZ..input.maxOf { it.z } + 1

    fun Vector3.getValidNeighbors() = getNeighbors()
        .filter { it.x in xRange && it.y in yRange && it.z in zRange }
        .filter { it !in input }

    fun floodFill(start: Vector3): Set<Vector3> = buildSet {
        val queue: Queue<Vector3> = LinkedList()
        queue += start

        while (queue.isNotEmpty()) {
            val element = queue.remove()
            if (add(element)) {
                queue += element.getValidNeighbors()
            }
        }
    }

    val externalCubes = floodFill(Vector3(minX, minY, minZ))
    return input.sumOf { v -> v.getNeighbors().count { it in externalCubes } }
}

private fun String.parse(): Set<Vector3> {
    fun String.toVector3(): Vector3 {
        val (x, y, z) = split(",")
        return Vector3(x.toInt(), y.toInt(), z.toInt())
    }
    return lineSequence()
        .map { it.toVector3() }
        .toSet()
}

private val neighborDirections = listOf(
    Vector3(-1, 0, 0), Vector3(1, 0, 0),
    Vector3(0, -1, 0), Vector3(0, 1, 0),
    Vector3(0, 0, -1), Vector3(0, 0, 1),
)

private fun Vector3.getNeighbors() = neighborDirections.asSequence()
    .map { this + it }
