package de.cramer.adventofcode.year2020.day17

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector3
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 112)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 848)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Set<Vector3>): Int {
    data class IntRange3(
        val x: IntRange,
        val y: IntRange,
        val z: IntRange,
    )

    infix fun IntRange3.merge(other: IntRange3) = IntRange3(x merge other.x, y merge other.y, z merge other.z)

    fun Iterable<Vector3>.getRanges(): IntRange3 {
        fun Vector3.toIntRange3() = IntRange3(x..x, y..y, z..z)
        return asSequence()
            .map { it.toIntRange3() }
            .reduce { acc, range -> acc merge range }
    }

    fun Set<Vector3>.step() = buildSet {
        val ranges = this@step.getRanges()
        ranges.x.expand().forEach { x ->
            ranges.y.expand().forEach { y ->
                ranges.z.expand().forEach { z ->
                    val vector = Vector3(x, y, z)
                    val activeNeighbors = vector.getNeighbors()
                        .count { it in this@step }
                    if (shouldBeActive(vector in this@step, activeNeighbors)) {
                        this += vector
                    }
                }
            }
        }
    }

    var activeCubes = input
    repeat(6) {
        activeCubes = activeCubes.step()
    }
    return activeCubes.size
}

private fun problem02(input: Set<Vector3>): Int {
    fun Vector3.toVector4() = Vector4(x, y, z, 0)

    data class IntRange4(
        val x: IntRange,
        val y: IntRange,
        val z: IntRange,
        val w: IntRange,
    )

    infix fun IntRange4.merge(other: IntRange4) = IntRange4(x merge other.x, y merge other.y, z merge other.z, w merge other.w)

    fun Iterable<Vector4>.getRanges(): IntRange4 {
        fun Vector4.toIntRange4() = IntRange4(x..x, y..y, z..z, w..w)
        return asSequence()
            .map { it.toIntRange4() }
            .reduce { acc, range -> acc merge range }
    }

    fun Set<Vector4>.step() = buildSet {
        val ranges = this@step.getRanges()
        ranges.x.expand().forEach { x ->
            ranges.y.expand().forEach { y ->
                ranges.z.expand().forEach { z ->
                    ranges.w.expand().forEach { w ->
                        val vector = Vector4(x, y, z, w)
                        val activeNeighbors = vector.getNeighbors()
                            .count { it in this@step }
                        if (shouldBeActive(vector in this@step, activeNeighbors)) {
                            this += vector
                        }
                    }
                }
            }
        }
    }

    var activeCubes = input.map { it.toVector4() }.toSet()
    repeat(6) {
        activeCubes = activeCubes.step()
    }
    return activeCubes.size
}

private fun String.parse(): Set<Vector3> {
    return lineSequence()
        .flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == '#') Vector3(x, y, 0) else null
            }
        }
        .toSet()
}

private fun shouldBeActive(active: Boolean, activeNeighbors: Int): Boolean =
    if (active) activeNeighbors in 2..3 else activeNeighbors == 3

private fun IntRange.expand() = (first - 1)..(last + 1)

private infix fun IntRange.merge(other: IntRange) = min(first, other.first)..max(last, other.last)

private val offsetRange = -1..1
private val neighborOffsets3 = offsetRange.flatMap { x ->
    offsetRange.flatMap { y ->
        offsetRange.map { z -> Vector3(x, y, z) }
    }
}.filterNot { it == Vector3.ZERO }

private fun Vector3.getNeighbors() = neighborOffsets3.asSequence()
    .map { this + it }

data class Vector4(
    val x: Int,
    val y: Int,
    val z: Int,
    val w: Int,
) {
    operator fun plus(other: Vector4) = Vector4(x + other.x, y + other.y, z + other.z, w + other.w)

    companion object {
        val ZERO = Vector4(0, 0, 0, 0)
    }
}

private val neighborOffsets4 = offsetRange.flatMap { x ->
    offsetRange.flatMap { y ->
        offsetRange.flatMap { z ->
            offsetRange.map { w ->
                Vector4(x, y, z, w)
            }
        }
    }
}.filterNot { it == Vector4.ZERO }

private fun Vector4.getNeighbors() = neighborOffsets4.asSequence()
    .map { this + it }
