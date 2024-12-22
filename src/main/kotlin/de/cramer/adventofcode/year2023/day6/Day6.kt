package de.cramer.adventofcode.year2023.day6

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 288u)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 71503u)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(races: List<Race>): ULong {
    return races.fold(1u) { acc, race -> acc * race.numberOfWaysToBeatRecord() }
}

private fun problem02(races: List<Race>): ULong {
    val time = races.joinToString(separator = "") { it.time.toString() }.toULong()
    val maxDistance = races.joinToString(separator = "") { it.maxDistance.toString() }.toULong()
    val race = Race(time, maxDistance)
    return race.numberOfWaysToBeatRecord()
}

private fun String.parse(): List<Race> {
    val (times, distances) = lines()
    fun String.toULongs(): Sequence<ULong> = splitToSequence("""\s+""".toRegex())
        .drop(1)
        .map { it.toULong() }

    return times.toULongs()
        .zip(distances.toULongs(), ::Race)
        .toList()
}

private data class Race(
    val time: ULong,
    val maxDistance: ULong,
)

/**
 * solves -time^2 + maxDistance*time + maxDistance = 0 using the quadratic formula
 */
private fun Race.numberOfWaysToBeatRecord(): ULong {
    val dTime = time.toDouble()
    val q = sqrt(dTime * dTime - 4 * maxDistance.toDouble())
    val min = floor((dTime - q) / 2 + 1)
    val max = ceil((dTime + q) / 2 - 1)
    return max.toULong() - min.toULong() + 1u
}
