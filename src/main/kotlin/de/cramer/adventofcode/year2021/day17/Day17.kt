package de.cramer.adventofcode.year2021.day17

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import kotlin.math.sign

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 45)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 112)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Area): Int {
    return input.getPossibleStartVelocities()
        .flatMap { simulateBound(Vector.ZERO, it, input) }
        .maxOf { it.y }
}

private fun problem02(input: Area): Int {
    return input.getPossibleStartVelocities().count()
}

private fun String.parse(): Area {
    fun String.toRange(): IntRange {
        val (end, start) = split("..")
        return end.toInt()..start.toInt()
    }
    val (x, y) = substringAfter("target area: x=").split(", y=")
    return Area(x.toRange(), y.toRange())
}

private fun Area.getPossibleStartVelocities(): Sequence<Vector> {
    return (1..xRange.last).asSequence()
        .flatMap { dx ->
            var stopped = false
            val roundsAndPositions = simulateBoundX(0, dx, xRange.last)
                .takeWhileWithPrevious { previous, current ->
                    (previous != current).also { if (!stopped) stopped = !it }
                }
                .mapIndexedNotNull { round, position -> if (position in xRange) round + 1 to position else null }
                .toList()
            if (roundsAndPositions.isNotEmpty()) {
                val start = roundsAndPositions.minOf { it.first }
                val rounds = if (stopped) generateSequence(start) { it + 1 } else (start..roundsAndPositions.maxOf { it.first }).asSequence()
                sequence {
                    for (round in rounds.takeWhile { it <= -yRange.first }) {
                        for (dy in yRange.first..round) {
                            val velocity = Vector(dx, dy)
                            if (simulateBound(Vector.ZERO, velocity, this@getPossibleStartVelocities).any { it in this@getPossibleStartVelocities }) {
                                yield(velocity)
                            }
                        }
                    }
                }
            } else {
                emptySequence()
            }
        }
        .distinct()
}

private fun <T> Sequence<T>.takeWhileWithPrevious(predicate: (previous: T, current: T) -> Boolean) = zipWithNext()
    .takeWhile { (previous, current) -> predicate(previous, current) }
    .flatMapIndexed { index, (previous, current) -> if (index == 0) sequenceOf(previous, current) else sequenceOf(current) }

private fun simulateX(start: Int, velocity: Int): Sequence<Int> = sequence {
    var position = start
    var speed = velocity
    while (true) {
        yield(position)
        position += speed
        speed -= speed.sign
    }
}

private fun simulateY(start: Int, velocity: Int): Sequence<Int> = sequence {
    var position = start
    var speed = velocity
    while (true) {
        yield(position)
        position += speed
        speed -= 1
    }
}

private fun simulateBoundX(start: Int, velocity: Int, maxX: Int): Sequence<Int> = simulateX(start, velocity)
    .takeWhile { it <= maxX }

private fun simulateBoundY(start: Int, velocity: Int, minY: Int): Sequence<Int> = simulateY(start, velocity)
    .takeWhile { it >= minY }

private fun simulateBound(start: Vector, velocity: Vector, area: Area) =
    simulateBoundX(start.x, velocity.x, area.xRange.last).zip(simulateBoundY(start.y, velocity.y, area.yRange.first)) { x, y -> Vector(x, y) }

data class Area(
    val xRange: IntRange,
    val yRange: IntRange,
) {
    operator fun contains(vector: Vector) =
        vector.x in xRange && vector.y in yRange
}
