package de.cramer.adventofcode.year2023.day12

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 21L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 525152L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(rows: List<Row>): Long {
    return rows.sumOf {
        count(it.springs, it.contiguousDamagedGroups)
    }
}

private fun problem02(rows: List<Row>): Long {
    return rows.sumOf {
        val springs = (1..5)
            .flatMap { _ -> it.springs + Spring.UNKNOWN }
            .dropLast(1)
        val contiguousDamagedGroups = (1..5)
            .flatMap { _ -> it.contiguousDamagedGroups }
        count(springs, contiguousDamagedGroups)
    }
}

private val cache = mutableMapOf<Pair<List<Spring>, List<Int>>, Long>()
private fun count(springs: List<Spring>, contigiousDamagedGroups: List<Int>): Long {
    if (contigiousDamagedGroups.isEmpty()) {
        return if (Spring.DAMAGED in springs) 0 else 1
    }
    if (springs.isEmpty()) {
        return 0
    }

    return cache.getOrPut(springs to contigiousDamagedGroups) {
        var result = 0L

        val spring = springs.first()
        if (spring == Spring.OPERATIONAL || spring == Spring.UNKNOWN) {
            // Spring is operational
            val remainingSprings = springs.drop(1)
            result += count(remainingSprings, contigiousDamagedGroups)
        }

        val damagedGroupSize = contigiousDamagedGroups.first()
        if ((spring == Spring.DAMAGED || spring == Spring.UNKNOWN) &&
            damagedGroupSize <= springs.size &&
            Spring.OPERATIONAL !in springs.take(damagedGroupSize) &&
            (springs.size == damagedGroupSize || springs[damagedGroupSize] != Spring.DAMAGED)
        ) {
            // Spring is damaged
            val remainingSprings = springs.drop(damagedGroupSize + 1)
            val remainingDamagedGroupSizes = contigiousDamagedGroups.drop(1)
            result += count(remainingSprings, remainingDamagedGroupSizes)
        }

        result
    }
}

private fun String.parse(): List<Row> {
    fun Char.toSpring() = when (this) {
        '.' -> Spring.OPERATIONAL
        '#' -> Spring.DAMAGED
        '?' -> Spring.UNKNOWN
        else -> error("cannot parse $this to Spring")
    }

    fun String.toRow(): Row {
        val (springs, groups) = split(" ")
        return Row(
            springs.map { it.toSpring() },
            groups.split(",").map { it.toInt() },
        )
    }

    return lineSequence()
        .map { it.toRow() }
        .toList()
}

private data class Row(
    val springs: List<Spring>,
    val contiguousDamagedGroups: List<Int>,
)

private enum class Spring {
    OPERATIONAL,
    DAMAGED,
    UNKNOWN,
}
