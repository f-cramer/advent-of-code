package de.cramer.adventofcode.year2023.day3

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.isValidIndex
import de.cramer.adventofcode.utils.vector.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 4361u)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 467835u)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): UInt {
    return input.partNumbers
        .filter { it.adjacentVectors.any { v -> input.chars[v] != null } }
        .sumOf { it.number }
}

private fun problem02(input: Input): UInt {
    return input.chars.vectors()
        .filter { input.chars[it] == '*' }
        .sumOf {
            val adjacentPartNumbers = input.partNumbers.filter { n ->
                it in n.adjacentVectors
            }
            if (adjacentPartNumbers.size == 2) {
                adjacentPartNumbers[0].number * adjacentPartNumbers[1].number
            } else {
                0u
            }
        }
}

private val partNumberRegex = """\d+""".toRegex()

private fun String.parse(): Input {
    val chars = mutableListOf<List<Char?>>()
    val possiblePartNumbers = mutableListOf<PossiblePartNumber>()
    lineSequence().forEachIndexed { index, line ->
        chars += line.toCharArray().map { if (it == '.') null else it }
        partNumberRegex.findAll(line).forEach {
            possiblePartNumbers += PossiblePartNumber(
                it.value.toUInt(),
                Vector(it.range.first, index),
                Vector(it.range.last, index),
            )
        }
    }
    val partNumbers = possiblePartNumbers.asSequence()
        .filter {
            it.adjacentVectors.any { v ->
                if (chars.isValidIndex(v)) {
                    chars[v] != null
                } else {
                    false
                }
            }
        }
        .map { PartNumber(it.number, it.adjacentVectors.filter(chars::isValidIndex)) }
        .toList()

    return Input(chars, partNumbers)
}

private data class Input(
    val chars: List<List<Char?>>,
    val partNumbers: List<PartNumber>,
)

private data class PossiblePartNumber(
    val number: UInt,
    val start: Vector,
    val end: Vector,
) {
    val adjacentVectors: List<Vector> = listOf(start.copy(x = start.x - 1)) +
        (start.x - 1..end.x + 1).flatMap { listOf(Vector(it, start.y - 1), Vector(it, start.y + 1)) } +
        end.copy(x = end.x + 1)
}

private data class PartNumber(
    val number: UInt,
    val adjacentVectors: List<Vector>,
)
