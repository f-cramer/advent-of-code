package de.cramer.adventofcode.year2024.day21

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.absoluteValue

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 126384L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 154115708116294L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<NumericButton>>): Long {
    return input.sumOf {
        it.resultingPathLength(2) * it.getNumericPart()
    }
}

private fun problem02(input: List<List<NumericButton>>): Long {
    return input.sumOf {
        it.resultingPathLength(25) * it.getNumericPart()
    }
}

private fun List<NumericButton>.resultingPathLength(depth: Int): Long {
    val sequence = sequenceOf(NumericButton.A) + asSequence()
    return sequence.zipWithNext { a, b -> a.resultingPathLength(b, depth) }.sum()
}

private val pathLengthCache = mutableMapOf<Triple<Any, Any, Int>, Long>()

private fun <T : Any> T.resultingPathLength(other: T, path: (T, T) -> List<DirectionalButton>, depth: Int): Long {
    return pathLengthCache.getOrPut(Triple<Any, Any, Int>(this, other, depth)) {
        val p = path(this@resultingPathLength, other) + DirectionalButton.ACTIVATE
        when {
            depth == 0 -> p.size.toLong()
            p.size == 1 -> 1
            else -> (sequenceOf(DirectionalButton.ACTIVATE) + p.asSequence()).zipWithNext { a, b -> a.resultingPathLength(b, depth - 1) }.sum()
        }
    }
}

private fun NumericButton.resultingPathLength(other: NumericButton, depth: Int) = resultingPathLength(other, NumericButton::pathTo, depth)

private fun DirectionalButton.resultingPathLength(other: DirectionalButton, depth: Int) = resultingPathLength(other, DirectionalButton::pathTo, depth)

private val pathCache = mutableMapOf<Pair<Any, Any>, List<DirectionalButton>>()

private fun <T : Any> T.pathTo(other: T, gap: Vector, location: (T) -> Vector): List<DirectionalButton> =
    pathCache.computeIfAbsent(Pair<Any, Any>(this, other)) { pathToImpl(other, gap, location) }

private fun <T> T.pathToImpl(other: T, gap: Vector, location: (T) -> Vector) = buildList {
    val start = location(this@pathToImpl)
    val end = location(other)
    if (start == end) {
        return@buildList
    }

    val xDiff = end.x - start.x
    val yDiff = end.y - start.y

    fun addXButtons() {
        if (xDiff != 0) {
            val button = if (xDiff > 0) DirectionalButton.RIGHT else DirectionalButton.LEFT
            repeat(xDiff.absoluteValue) { add(button) }
        }
    }

    fun addYButtons() {
        if (yDiff != 0) {
            val button = if (yDiff > 0) DirectionalButton.DOWN else DirectionalButton.UP
            repeat(yDiff.absoluteValue) { add(button) }
        }
    }

    if (start.y == gap.y && end.y != gap.y && end.x == gap.x) {
        // we would touch the gap, when going left before going up
        addYButtons()
        addXButtons()
    } else if (start.x == gap.x && end.x != gap.x && end.y == gap.y) {
        // we would touch the gap, when going up before going left
        addXButtons()
        addYButtons()
    } else {
        if (xDiff < 0) {
            repeat(xDiff.absoluteValue) { add(DirectionalButton.LEFT) }
        }
        addYButtons()
        if (xDiff > 0) {
            repeat(xDiff.absoluteValue) { add(DirectionalButton.RIGHT) }
        }
    }
}

private fun NumericButton.pathTo(other: NumericButton) = pathTo(other, Vector(0, 3), NumericButton::location)

private fun DirectionalButton.pathTo(other: DirectionalButton) = pathTo(other, Vector(0, 0), DirectionalButton::location)

private fun <T> List<T>.getShortestSequence(start: T, path: (T, T) -> List<DirectionalButton>) = buildList {
    var position = start
    for (button in this@getShortestSequence) {
        addAll(path(position, button))
        add(DirectionalButton.ACTIVATE)
        position = button
    }
}

@JvmName("getShortestSequenceNumeric")
private fun List<NumericButton>.getShortestSequence() = getShortestSequence(NumericButton.A, NumericButton::pathTo)

@JvmName("getShortestSequenceDirectional")
private fun List<DirectionalButton>.getShortestSequence() = getShortestSequence(DirectionalButton.ACTIVATE, DirectionalButton::pathTo)

private fun List<NumericButton>.getNumericPart() = mapNotNull { it.char?.toString() }
    .joinToString(separator = "").toInt()

private fun String.parse(): List<List<NumericButton>> {
    fun Char.toNumericButton() = when {
        isDigit() -> NumericButton.entries[digitToInt()]
        this == 'A' -> NumericButton.A
        else -> error("cannot parse $this to numeric button")
    }

    fun String.parseLine() = map { it.toNumericButton() }
    return lines()
        .map { it.parseLine() }
}

private enum class NumericButton(
    val location: Vector,
    val char: Char? = null,
) {
    N0(Vector(1, 3), '0'),
    N1(Vector(0, 2), '1'),
    N2(Vector(1, 2), '2'),
    N3(Vector(2, 2), '3'),
    N4(Vector(0, 1), '4'),
    N5(Vector(1, 1), '5'),
    N6(Vector(2, 1), '6'),
    N7(Vector(0, 0), '7'),
    N8(Vector(1, 0), '8'),
    N9(Vector(2, 0), '9'),
    A(Vector(2, 3)),
}

private enum class DirectionalButton(
    val location: Vector,
    val direction: Vector?,
) {
    UP(Vector(1, 0), Vector(0, -1)),
    ACTIVATE(Vector(2, 0), null),
    LEFT(Vector(0, 1), Vector(-1, 0)),
    DOWN(Vector(1, 1), Vector(0, 1)),
    RIGHT(Vector(2, 1), Vector(1, 0)),
}
