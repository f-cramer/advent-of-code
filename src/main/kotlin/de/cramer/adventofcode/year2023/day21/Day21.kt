package de.cramer.adventofcode.year2023.day21

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
    val testResult01 = problem01(testInput, 6)
    checkTestResult(testResult01, 16)

    val input = readInput().parse()
    runProblem01 { problem01(input, 64) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input, steps: Int): Int {
    return getNumberOfReachablePositions(input, steps) {
        if (input.map.isValidIndex(it)) input.map[it] else null
    }.getValue(steps)
}

private fun problem02(input: Input): Long {
    val mapSize = Vector(input.map.first().size, input.map.size)
    val x = (1..5 step 2).map { it * mapSize.y / 2 }.map { it.toLong() }
    val y = getNumberOfReachablePositions(input, *x.map { it.toInt() }.toIntArray()) { v ->
        input.map[v.y.mod(mapSize.y)][v.x.mod(mapSize.x)]
    }.values.sorted()

    fun lagrange(steps: Int): Long {
        return x.map { xi ->
            val numerator = x.fold(1L) { acc, i -> if (i == xi) acc else acc * (steps - i) }
            val denominator = x.fold(1L) { acc, i -> if (i == xi) acc else acc * (xi - i) }
            (numerator.toDouble() / denominator).toLong()
        }.zip(y) { a, b -> a * b }.sum()
    }

    return lagrange(26501365)
}

private fun getNumberOfReachablePositions(input: Input, vararg steps: Int, getter: (Vector) -> Tile?): Map<Int, Int> {
    val result = mutableMapOf<Int, Int>()
    var positions = setOf(input.start)
    for (i in 1..steps.max()) {
        positions = buildSet {
            positions.forEach { p ->
                this += sequenceOf(p.north(), p.east(), p.south(), p.west())
                    .filter { getter(it) == Tile.GARDEN_PLOT }
            }
        }

        if (i in steps) {
            result[i] = positions.size
        }
    }
    return result
}

private fun String.parse(): Input {
    var start: Vector? = null
    val map = lines().mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            when (char) {
                '#' -> Tile.ROCK
                '.' -> Tile.GARDEN_PLOT
                'S' -> {
                    start = Vector(x, y)
                    Tile.GARDEN_PLOT
                }

                else -> error("cannot parse $char to tile")
            }
        }
    }
    return Input(map, start!!)
}

private data class Input(
    val map: List<List<Tile>>,
    val start: Vector,
)

private enum class Tile {
    GARDEN_PLOT,
    ROCK,
}
