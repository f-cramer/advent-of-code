package de.cramer.adventofcode.year2020.day11

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.isValidIndex

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 37)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 26)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Seat>>): Int {
    return input.getOccupiedSeatsAtEquilibrium(4, List<List<Seat>>::getNeighbors)
}

private fun problem02(input: List<List<Seat>>): Int {
    return input.getOccupiedSeatsAtEquilibrium(5, List<List<Seat>>::getNeighborSeats)
}

private fun String.parse(): List<List<Seat>> {
    fun Char.toSeat() = when (this) {
        'L' -> Seat.EMPTY
        '#' -> Seat.OCCUPIED
        '.' -> Seat.FLOOR
        else -> error("cannot parse $this to seat")
    }

    return lineSequence()
        .map { l -> l.map { it.toSeat() } }
        .toList()
}

private fun List<List<Seat>>.getOccupiedSeatsAtEquilibrium(minimumSeatsToLeave: Int, neighbors: List<List<Seat>>.(Vector) -> List<Vector>): Int {
    fun List<List<Seat>>.step(): List<List<Seat>> = mapIndexed { y, row ->
        row.mapIndexed inner@{ x, seat ->
            if (seat == Seat.FLOOR) {
                return@inner Seat.FLOOR
            }

            val position = Vector(x, y)
            val occupiedSeats = neighbors(position)
                .count { this[it] == Seat.OCCUPIED }
            when (seat) {
                Seat.EMPTY -> if (occupiedSeats == 0) Seat.OCCUPIED else Seat.EMPTY
                Seat.OCCUPIED -> if (occupiedSeats >= minimumSeatsToLeave) Seat.EMPTY else Seat.OCCUPIED
                else -> error("cannot happen") // compiler forces me to add this
            }
        }
    }

    var previous = this
    do {
        val prev = previous
        val current = previous.step()
        previous = current
    } while (current != prev)

    return previous.sumOf { l -> l.count { it == Seat.OCCUPIED } }
}

private enum class Seat {
    EMPTY,
    OCCUPIED,
    FLOOR,
}

@Suppress("ktlint:standard:argument-list-wrapping")
private val neighborOffsets = listOf(
    Vector(-1, -1), Vector(0, -1), Vector(1, -1),
    Vector(-1, 0), Vector(1, 0),
    Vector(-1, 1), Vector(0, 1), Vector(1, 1),
)

private fun List<List<*>>.getNeighbors(position: Vector) = neighborOffsets
    .map { position + it }
    .filter { isValidIndex(it) }

private fun List<List<Seat>>.getNeighborSeats(position: Vector) = neighborOffsets
    .mapNotNull { offset ->
        generateSequence(position) { it + offset }
            .drop(1)
            .takeWhile { isValidIndex(it) }
            .firstOrNull { this[it] != Seat.FLOOR }
    }
