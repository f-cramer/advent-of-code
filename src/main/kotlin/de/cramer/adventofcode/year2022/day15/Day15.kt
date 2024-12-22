package de.cramer.adventofcode.year2022.day15

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runTest
import de.cramer.adventofcode.utils.vector.Vector
import kotlin.math.abs
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput, 10)
    checkTestResult(testResult01, 26)
    val testResult02 = problem02(testInput, 20)
    checkTestResult(testResult02, 56000011)

    val input = readInput().parse()
    runTest("result01") { problem01(input, 2000000) }
    runTest("result02") { problem02(input, 4000000) }
}

private fun problem01(input: List<Sensor>, lineNumber: Int): Int {
    val coverage = input.flatMap { it.getCoverageInLine(lineNumber) }.toSet()
    val knownSensors = input.asSequence()
        .map { it.closestBeacon }
        .filter { it.y == lineNumber }
        .map { it.x }
        .toSet()
    return (coverage - knownSensors).size
}

private fun problem02(input: List<Sensor>, maxCoordinate: Int): Long {
    val coordinates = 0..maxCoordinate
    return coordinates
        .firstNotNullOf { y ->
            var notCovered = listOf(coordinates)
            for (sensor in input) {
                val coverage = sensor.getCoverageInLine(y)
                notCovered = notCovered.flatMap { it - coverage }
                if (notCovered.isEmpty()) {
                    break
                }
            }
            if (notCovered.isNotEmpty()) Vector(notCovered.first().first, y) else null
        }.let {
            it.x.toLong() * 4000000 + it.y
        }
}

private fun String.parse(): List<Sensor> {
    fun String.toVector() = split(", ").let { (first, second) ->
        Vector(first.substringAfter("x=").toInt(), second.substringAfter("y=").toInt())
    }

    fun String.toSensor() = split(": ").let { (first, second) ->
        val position = first.substringAfter("Sensor at ").toVector()
        val closestBeacon = second.substringAfter("closest beacon is at ").toVector()
        Sensor(position, closestBeacon)
    }

    return lines().map { it.toSensor() }
}

private data class Sensor(
    val position: Vector,
    val closestBeacon: Vector,
) {
    fun distanceToClosestBeacon(): Int {
        val difference = position - closestBeacon
        return abs(difference.x) + abs(difference.y)
    }
}

private fun Sensor.getCoverageInLine(lineNumber: Int): IntRange {
    val distanceToClosestBeacon = distanceToClosestBeacon()
    val remainingXDistance = distanceToClosestBeacon - abs(lineNumber - position.y)
    val result = (position.x - remainingXDistance)..(position.x + remainingXDistance)
    return if (result.isEmpty()) IntRange.EMPTY else result
}

private operator fun IntRange.minus(other: IntRange): Set<IntRange> = when {
    isEmpty() && other.isEmpty() -> emptySet()
    isEmpty() -> setOf(other)
    other.isEmpty() -> setOf(this)
    other.last < first -> setOf(this)
    other.first > last -> setOf(this)
    else -> sequenceOf(
        first..<min(last, other.first),
        (other.last + 1)..last,
    )
        .filter { !it.isEmpty() }
        .toSet()
}
