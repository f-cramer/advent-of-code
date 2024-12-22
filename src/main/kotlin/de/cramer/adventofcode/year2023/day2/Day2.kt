package de.cramer.adventofcode.year2023.day2

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import kotlin.math.max

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 8u)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2286u)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Game>): UInt {
    val availableCubes = mapOf(
        "red" to 12u,
        "green" to 13u,
        "blue" to 14u,
    )

    return input.asSequence()
        .filter { g ->
            g.draws.all { d ->
                d.all { (color, count) -> count <= availableCubes.getOrDefault(color, 0u) }
            }
        }
        .sumOf { it.id }
}

private fun problem02(input: List<Game>): UInt {
    return input.asSequence()
        .map {
            it.draws.fold(emptyMap<String, UInt>()) { acc, draw ->
                buildMap {
                    putAll(acc)
                    draw.forEach { (t, u) ->
                        merge(t, u, ::max)
                    }
                }
            }
        }
        .sumOf { it.values.reduce { acc, count -> acc * count } }
}

private fun String.parse() = lineSequence()
    .map { it.toGame() }
    .toList()

private fun String.toGame(): Game {
    val (gameWithIdString, drawsString) = split(": ")
    val id = gameWithIdString.substring(5).toUInt()
    val draws = drawsString.splitToSequence("; ")
        .map { it.toDraw() }
        .toList()
    return Game(id, draws)
}

private fun String.toDraw(): Draw {
    return splitToSequence(", ")
        .map { it.split(" ") }
        .associate { it[1] to it[0].toUInt() }
}

private data class Game(
    val id: UInt,
    val draws: List<Draw>,
)

private typealias Draw = Map<String, UInt>
