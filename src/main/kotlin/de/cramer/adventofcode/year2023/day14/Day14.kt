package de.cramer.adventofcode.year2023.day14

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 136)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 64)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Tile>>): Int {
    val tiles = input.mutableCopy()
    tiles.tiltNorth()
    return tiles.summarize()
}

private fun problem02(input: List<List<Tile>>): Int {
    fun MutableList<MutableList<Tile>>.cycle() {
        tiltNorth()
        tiltWest()
        tiltSouth()
        tiltEast()
    }

    val tiles = input.mutableCopy()
    val results = mutableListOf<List<List<Tile>>>()
    val iterations = 1000000000
    for (i in 0..<iterations) {
        tiles.cycle()
        val firstOccurance = results.indexOf(tiles)
        if (firstOccurance >= 0) {
            val cycleLength = i - firstOccurance
            val remainingCycles = iterations - i
            val cycleIndex = remainingCycles % cycleLength
            val index = cycleIndex + firstOccurance - 1
            return results[index].summarize()
        }
        results += tiles.mutableCopy()
    }

    return results.last().summarize()
}

private fun List<List<Tile>>.summarize(): Int {
    val rowCount = size
    return mapIndexed { y, rows ->
        (rowCount - y) * rows.count { it == Tile.ROUNDED_ROCK }
    }.sum()
}

private fun MutableList<MutableList<Tile>>.tiltNorth() {
    forEachIndexed { y, row ->
        row.indices.forEach { x ->
            if (this[y][x] != Tile.ROUNDED_ROCK) return@forEach

            this[y][x] = Tile.EMPTY

            val newY = (y - 1 downTo 0).firstOrNull { newY ->
                this[newY][x] != Tile.EMPTY
            } ?: -1

            this[y][x] = Tile.EMPTY
            this[newY + 1][x] = Tile.ROUNDED_ROCK
        }
    }
}

private fun MutableList<MutableList<Tile>>.tiltEast() {
    val first = first()
    val width = first.size
    first.indices.reversed().forEach { x ->
        indices.forEach inner@{ y ->
            if (this[y][x] != Tile.ROUNDED_ROCK) return@inner

            this[y][x] = Tile.EMPTY

            val newX = (x + 1..<width).firstOrNull { newX ->
                this[y][newX] != Tile.EMPTY
            } ?: width

            this[y][x] = Tile.EMPTY
            this[y][newX - 1] = Tile.ROUNDED_ROCK
        }
    }
}

private fun MutableList<MutableList<Tile>>.tiltSouth() {
    indices.reversed().forEach { y ->
        val row = this[y]
        row.indices.forEach inner@{ x ->
            if (this[y][x] != Tile.ROUNDED_ROCK) return@inner

            this[y][x] = Tile.EMPTY

            val newY = (y + 1..<size).firstOrNull { newY ->
                this[newY][x] != Tile.EMPTY
            } ?: size

            this[y][x] = Tile.EMPTY
            this[newY - 1][x] = Tile.ROUNDED_ROCK
        }
    }
}

private fun MutableList<MutableList<Tile>>.tiltWest() {
    val first = first()
    first.indices.forEach { x ->
        indices.forEach inner@{ y ->
            if (this[y][x] != Tile.ROUNDED_ROCK) return@inner

            this[y][x] = Tile.EMPTY

            val newX = (x - 1 downTo 0).firstOrNull { newX ->
                this[y][newX] != Tile.EMPTY
            } ?: -1

            this[y][x] = Tile.EMPTY
            this[y][newX + 1] = Tile.ROUNDED_ROCK
        }
    }
}

private fun <T> List<List<T>>.mutableCopy() = asSequence()
    .map { it.toMutableList() }
    .toMutableList()

private fun String.parse(): List<List<Tile>> {
    fun Char.toTile() = when (this) {
        'O' -> Tile.ROUNDED_ROCK
        '#' -> Tile.CUBED_ROCK
        '.' -> Tile.EMPTY
        else -> error("cannot parse $this to tile")
    }

    return lines()
        .map { l -> l.map { it.toTile() } }
}

private enum class Tile {
    ROUNDED_ROCK,
    CUBED_ROCK,
    EMPTY,
}
