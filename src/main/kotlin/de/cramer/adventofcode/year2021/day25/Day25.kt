package de.cramer.adventofcode.year2021.day25

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 58)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
}

private fun problem01(input: List<List<Tile>>): Int {
    return generateSequence(input) { it.step() }
        .zipWithNext { a, b -> a != b }
        .takeWhile { it }
        .count() + 1
}

private fun List<List<Tile>>.step(): List<List<Tile>> {
    fun Vector.wrap() = Vector(x.mod(this@step[0].size), y.mod(size))

    fun List<List<Tile>>.move(direction: Tile): List<List<Tile>> {
        return mapIndexed { y, l ->
            l.mapIndexed { x, tile ->
                val position = Vector(x, y)
                when (tile) {
                    Tile.EMPTY -> {
                        val previousPosition = (position - direction.vector).wrap()
                        if (this[previousPosition] == direction) direction else Tile.EMPTY
                    }

                    direction -> {
                        val nextPosition = (position + direction.vector).wrap()
                        if (this[nextPosition] == Tile.EMPTY) Tile.EMPTY else direction
                    }

                    else -> tile
                }
            }
        }
    }

    return move(Tile.EAST).move(Tile.SOUTH)
}

private fun String.parse(): List<List<Tile>> {
    fun Char.toTile(): Tile = when (this) {
        'v' -> Tile.SOUTH
        '>' -> Tile.EAST
        '.' -> Tile.EMPTY
        else -> error("cannot parse $this to tile")
    }

    return lineSequence()
        .map { l -> l.map { it.toTile() } }
        .toList()
}

private enum class Tile {
    SOUTH,
    EAST,
    EMPTY,
    ;

    val vector: Vector
        get() = when (this) {
            SOUTH -> Vector(0, 1)
            EAST -> Vector(1, 0)
            EMPTY -> throw UnsupportedOperationException("Tile.EMPTY.vector")
        }
}
