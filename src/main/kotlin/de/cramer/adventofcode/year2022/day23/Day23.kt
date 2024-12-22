package de.cramer.adventofcode.year2022.day23

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runTest
import de.cramer.adventofcode.utils.set

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 110)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 20)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input) }
}

private fun problem01(input: List<List<Tile>>): Int {
    val directions = mutableListOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
    val tiles = input.map { it.toMutableList() }.toMutableList()

    repeat(10) {
        tiles.simulate(directions)
    }

    tiles.cleanup()

    return tiles.sumOf { line -> line.count { it == Tile.EMPTY } }
}

private fun problem02(input: List<List<Tile>>): Int {
    val directions = mutableListOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
    val tiles = input.map { it.toMutableList() }.toMutableList()

    (1..Int.MAX_VALUE).forEach { iteration ->
        val before = tiles.map { it.toMutableList() }.toMutableList()
        before.cleanup()
        tiles.simulate(directions)

        tiles.cleanup()
        if (before == tiles) {
            return iteration
        }
    }

    error("could not find an iteration where no elf was moved")
}

private fun MutableList<MutableList<Tile>>.simulate(directions: MutableList<Direction>) {
    fun MutableList<MutableList<Tile>>.ensurePadding() {
        if (isEmpty()) {
            add(mutableListOf(Tile.EMPTY))
            return
        }

        val addFirstRow = first().any { it == Tile.ELF }
        val addLastRow = last().any { it == Tile.ELF }
        val addFirstColumn = any { it.isNotEmpty() && it.first() == Tile.ELF }
        val addLastColumn = any { it.isNotEmpty() && it.last() == Tile.ELF }

        val columns = first().size

        if (addFirstRow) add(0, MutableList(columns) { Tile.EMPTY })
        if (addLastRow) add(MutableList(columns) { Tile.EMPTY })

        if (addFirstColumn) forEach { it.add(0, Tile.EMPTY) }
        if (addLastColumn) forEach { it.add(Tile.EMPTY) }
    }

    ensurePadding()

    val moveProposals = mutableMapOf<Vector, Vector>()
    for ((y, row) in withIndex()) {
        for ((x, tile) in row.withIndex()) {
            if (tile != Tile.ELF) continue

            val position = Vector(x, y)
            val noElfAllAround = allAround.all { this[position + it] == Tile.EMPTY }
            if (noElfAllAround) continue

            for (direction in directions) {
                if (direction.check.all { this[position + it] == Tile.EMPTY }) {
                    moveProposals[position] = position + direction.move
                    break
                }
            }
        }
    }

    val targetsBySourcePosition = moveProposals.asSequence()
        .groupBy({ it.value }) { it.key }

    for ((target, sourcePositions) in targetsBySourcePosition) {
        if (sourcePositions.size > 1) continue

        val sourcePosition = sourcePositions[0]
        this[sourcePosition] = Tile.EMPTY
        this[target] = Tile.ELF
    }

    // shift directions
    val first = directions.removeFirst()
    directions.add(first)
}

private fun MutableList<MutableList<Tile>>.cleanup() {
    while (isNotEmpty() && first().all { it == Tile.EMPTY }) {
        removeFirst()
    }
    while (isNotEmpty() && last().all { it == Tile.EMPTY }) {
        removeLast()
    }
    while (isNotEmpty() && all { it.first() == Tile.EMPTY }) {
        forEach { it.removeFirst() }
    }
    while (isNotEmpty() && all { it.last() == Tile.EMPTY }) {
        forEach { it.removeLast() }
    }
}

private fun String.parse(): List<List<Tile>> {
    fun Char.toTile() = when (this) {
        '#' -> Tile.ELF
        '.' -> Tile.EMPTY
        else -> error("cannot parse $this to tile")
    }

    return lineSequence()
        .map { line -> line.map { it.toTile() } }
        .toList()
}

private enum class Tile {
    ELF,
    EMPTY,
}

val northWest = Vector(-1, -1)
val north = Vector(0, -1)
val northEast = Vector(1, -1)
val east = Vector(1, 0)
val southEast = Vector(1, 1)
val south = Vector(0, 1)
val southWest = Vector(-1, 1)
val west = Vector(-1, 0)

val allAround = setOf(northWest, north, northEast, east, southEast, south, southWest, west)

private enum class Direction(
    val move: Vector,
    val check: List<Vector>,
) {
    NORTH(north, northWest, northEast),
    EAST(east, northEast, southEast),
    SOUTH(south, southWest, southEast),
    WEST(west, northWest, southWest),
    ;

    constructor(move: Vector, vararg check: Vector) : this(move, listOf(move, *check))
}
