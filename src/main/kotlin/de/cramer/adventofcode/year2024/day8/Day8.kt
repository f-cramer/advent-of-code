package de.cramer.adventofcode.year2024.day8

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 14)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 34)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    return buildSet {
        input.antennas.values.forEach { antennas ->
            antennas.allPairs().forEach { (antenna1, antenna2) ->
                val distance = antenna1 - antenna2

                val antinode1 = antenna1 + distance
                val antinode2 = antenna2 - distance
                if (antinode1 in input.mapSize) {
                    add(antinode1)
                }
                if (antinode2 in input.mapSize) {
                    add(antinode2)
                }
            }
        }
    }.size
}

private fun problem02(input: Input): Int {
    return buildSet {
        input.antennas.values.forEach { antennas ->
            antennas.allPairs().forEach { (antenna1, antenna2) ->
                val distance = antenna1 - antenna2

                add(antenna1)
                add(antenna2)

                var antinode = antenna1 + distance
                while (antinode in input.mapSize) {
                    add(antinode)
                    antinode += distance
                }

                antinode = antenna2 - distance
                while (antinode in input.mapSize) {
                    add(antinode)
                    antinode -= distance
                }
            }
        }
    }.size
}

private fun String.parse(): Input {
    val lines = lines()
    val antennas = buildMap<Char, MutableList<Vector>> {
        lines.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c != '.') {
                    computeIfAbsent(c) { mutableListOf() } += Vector(x, y)
                }
            }
        }
    }
    val mapSize = Vector(lines.first().length, lines.size)
    return Input(antennas, mapSize)
}

private data class Input(
    val antennas: Map<Char, List<Vector>>,
    val mapSize: Vector,
)

private fun <T> List<T>.allPairs(): Sequence<Pair<T, T>> = sequence {
    for (i in 0..<this@allPairs.size - 1) {
        for (j in i + 1..<this@allPairs.size) {
            yield(this@allPairs[i] to this@allPairs[j])
        }
    }
}

private operator fun Vector.contains(inner: Vector): Boolean = inner.x in 0..<x && inner.y in 0..<y
