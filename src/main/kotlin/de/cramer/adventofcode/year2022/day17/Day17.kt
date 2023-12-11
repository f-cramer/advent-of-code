package de.cramer.adventofcode.year2022.day17

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runTest
import java.util.SortedMap
import java.util.SortedSet
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3068)
    val testResult02 = problem02(testInput, 1000)
    checkTestResult(testResult02, 1514285714288L)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input, 6000) }
}

private fun problem01(input: List<Direction>): Int {
    return simulate(input, 2022).rocks.getMaxY() + 1
}

private fun problem02(input: List<Direction>, testIterations: Int): Long {
    val mainRocks = simulate(input, testIterations).rocks
    print(null, mainRocks)
    val rocksRange = 0..mainRocks.getMaxY()

    val cycle = rocksRange.asSequence().zipWith { (it + 1..rocksRange.last).asSequence() }
        .filter { (a, b) -> b + (b - a) in rocksRange }
        .map { (a, b) -> a..<b }
        .filter { it.length > 10 }
        .first {
            equals(mainRocks, it, (it.last + 1).rangeFor(it.length + 1))
        }
    val nextCycle = (cycle.last + 1).rangeFor(cycle.length + 1)

    var firstIterations = 0
    var firstSimulationState: SimulationState? = null
    while (++firstIterations <= testIterations) {
        firstSimulationState = if (firstSimulationState == null) {
            simulate(input, 1)
        } else {
            simulate(input, 1, firstSimulationState)
        }
        if (equals(mainRocks, firstSimulationState.rocks, cycle)) {
            break
        }
    }

    var nextIterations = firstIterations + 1
    var nextSimulationState = simulate(input, nextIterations)
    while (++nextIterations <= testIterations) {
        nextSimulationState = simulate(input, 1, nextSimulationState)
        if (equals(mainRocks, nextSimulationState.rocks, nextCycle)) {
            break
        }
    }

    val iterationCycleLength = nextIterations - firstIterations
    val iterationCycleOffset = firstIterations % iterationCycleLength
    val heightOffset = firstSimulationState!!.rocks.getMaxY() - cycle.last

    fun result(iterations: Long): Long {
        val offset = cycle.first
        val length = cycle.length + 1
        val cycleIterations = (iterations - iterationCycleOffset) / iterationCycleLength
        val remaining = iterations - iterationCycleOffset - cycleIterations * iterationCycleLength

        val remainingOffset = if (remaining == 0L) {
            0
        } else {
            val stateBefore = simulate(input, iterationCycleOffset + iterationCycleLength)
            val maxYBefore = stateBefore.rocks.getMaxY()
            val maxYAfter = simulate(input, Math.toIntExact(remaining), stateBefore).rocks.getMaxY()
            maxYAfter - maxYBefore
        }

        return offset + length * cycleIterations + heightOffset + remainingOffset
    }

    return result(1000000000000)
}

private fun equals(a: Rocks, b: Rocks, range: IntRange): Boolean = range.all { a[it] == b[it] }

private val IntRange.length
    get() = last - first

private fun equals(rocks: Rocks, a: IntRange, b: IntRange): Boolean {
    if (a.length != b.length) {
        return false
    }

    return (a zip b).all { (a, b) -> rocks[a] == rocks[b] }
}

private val gravity = Vector(0, -1)

private fun simulate(directions: List<Direction>, numberOfRocks: Int, state: SimulationState = SimulationState(Rocks(), WrappingIndex(wrapAt = directions.size), WrappingIndex(wrapAt = rockTypes.size))): SimulationState {
    val rocks = state.rocks
    val directionIndex = state.directionIndex
    val rockIndex = state.rockIndex

    for (ignored in 0..<numberOfRocks) {
        var rock = rockTypes[rockIndex.getAndIncrement()] + Vector(2, rocks.getMaxY() + 4)
        print(rock, rocks)
        while (rock !in rocks && rock.parts.all { p -> p.y >= 0 }) {
            val direction = directions[directionIndex.getAndIncrement()]

            // move by jet
            val moved = rock + if (direction == Direction.LEFT && rock.getMinX() > 0) {
                direction.vector
            } else if (direction == Direction.RIGHT && rock.getMaxX() < 6) {
                direction.vector
            } else {
                Vector.ZERO
            }

            if (moved !in rocks) {
                rock = moved
            }

            print(rock, rocks)

            // move by gravity
            val movedDown = rock + gravity
            if (movedDown in rocks || movedDown.parts.any { p -> p.y < 0 }) {
                break
            } else {
                rock = movedDown
                print(rock, rocks)
            }
        }

        rocks += rock
        print(null, rocks)
    }

    return SimulationState(rocks, directionIndex, rockIndex)
}

private fun print(fallingRock: Rock?, rocks: Rocks, force: Boolean = false) {
    if (!force) {
        return
    }

    val fallingRockParts = fallingRock?.parts ?: emptySet()
    val maxY = max(fallingRock?.parts?.maxOf { it.y } ?: 0, rocks.getMaxY())
    val yLength = floor(log10(maxY.toDouble())).toInt() + 1
    for (y in maxY downTo 0) {
        print(y.toString().padStart(yLength))
        print('|')
        for (x in 0..6) {
            val char = when (Vector(x, y)) {
                in fallingRockParts -> '@'
                in rocks -> '#'
                else -> '.'
            }
            print(char)
        }
        print('|')
        println()
    }
    print(" ".repeat(yLength))
    println("+-------+")
    println()
}

private fun String.parse(): List<Direction> {
    return toCharArray().map { it.toDirection() }
}

private data class Rock(
    val parts: Set<Vector>,
) {
    constructor(vararg parts: Vector) : this(parts.toSet())
    constructor(parts: Iterable<Vector>) : this(parts.toSet())

    fun getMinX(): Int = parts.minOf { it.x }
    fun getMaxX(): Int = parts.maxOf { it.x }

    operator fun plus(vector: Vector): Rock {
        return Rock(parts.map { it + vector })
    }

    operator fun minus(vector: Vector): Rock {
        return Rock(parts.map { it - vector })
    }
}

private data class Rocks(
    private val rocks: SortedMap<Int, SortedSet<Int>> = sortedMapOf(),
) {
    fun getMaxY(): Int = if (rocks.isEmpty()) -1 else rocks.lastKey()

    operator fun get(y: Int) = rocks[y]?.toSet() ?: emptySet()

    operator fun plusAssign(rock: Rock) {
        rock.parts.forEach { this += it }
    }

    operator fun plusAssign(vector: Vector) {
        rocks.computeIfAbsent(vector.y) { sortedSetOf() } += vector.x
    }

    operator fun contains(rock: Rock): Boolean = rock.parts.any { it in this }

    operator fun contains(vector: Vector): Boolean {
        val row = rocks[vector.y] ?: return false
        return vector.x in row
    }
}

private data class SimulationState(
    val rocks: Rocks,
    val directionIndex: WrappingIndex,
    val rockIndex: WrappingIndex,
)

private enum class Direction(
    val vector: Vector,
) {
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
}

private fun Char.toDirection() = when (this) {
    '<' -> Direction.LEFT
    '>' -> Direction.RIGHT
    else -> error("cannot parse '$this' to Direction")
}

private val rockTypes = listOf(
    Rock(Vector(0, 0), Vector(1, 0), Vector(2, 0), Vector(3, 0)),
    Rock(Vector(1, 0), Vector(0, 1), Vector(1, 1), Vector(2, 1), Vector(1, 2)),
    Rock(Vector(0, 0), Vector(1, 0), Vector(2, 0), Vector(2, 1), Vector(2, 2)),
    Rock(Vector(0, 0), Vector(0, 1), Vector(0, 2), Vector(0, 3)),
    Rock(Vector(0, 0), Vector(1, 0), Vector(0, 1), Vector(1, 1)),
)

private data class WrappingIndex(
    var index: Int = 0,
    val wrapAt: Int,
) {
    fun getAndIncrement(): Int {
        val oldIndex = index
        index++
        index %= wrapAt
        return oldIndex
    }
}

private fun <A, B> Sequence<A>.zipWith(other: (A) -> Sequence<B>): Sequence<Pair<A, B>> = sequence {
    for (a in this@zipWith) {
        for (b in other(a)) {
            yield(a to b)
        }
    }
}

private fun Int.rangeFor(length: Int): IntRange = this..<this + length
