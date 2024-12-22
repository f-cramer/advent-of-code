package de.cramer.adventofcode.year2020.day16

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 71)
    val testResult02 = problem02(testInput, "class")
    checkTestResult(testResult02, 1)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input, "departure") }
}

private fun problem01(input: Input): Int {
    val allFieldRanges = input.fields.values.flatten()
    return input.nearbyTickets
        .flatten()
        .filter { v -> allFieldRanges.none { v in it } }
        .sum()
}

private fun problem02(input: Input, prefix: String): Long {
    val allFieldRanges = input.fields.values.flatten()
    val validTickets = input.nearbyTickets.filter { ticket ->
        ticket.all { v -> allFieldRanges.any { v in it } }
    }

    fun Map<String, MutableSet<Int>>.reduce(): Map<String, Int> = buildMap {
        while (size < this@reduce.size) {
            this@reduce.asSequence()
                .filter { (_, possibleValues) -> possibleValues.size == 1 }
                .forEach { (rule, possibleValues) ->
                    val columnNumber = possibleValues.single()
                    this[rule] = columnNumber
                    this@reduce.values.forEach { it -= columnNumber }
                }
        }
    }

    val fieldIndicesByFieldName = input.fields.map { (name, ranges) ->
        name to input.myTicket.indices.filter { index ->
            validTickets.all { ticket -> ranges.any { ticket[index] in it } }
        }.toMutableSet()
    }.toMap().reduce()

    return fieldIndicesByFieldName.entries
        .filter { (name, _) -> name.startsWith(prefix) }
        .map { (_, index) -> input.myTicket[index].toLong() }
        .reduce { acc, i -> acc * i }
}

private fun String.parse(): Input {
    fun String.toRange(): IntRange {
        val (start, end) = split("-")
        return start.toInt()..end.toInt()
    }

    fun String.toField(): Pair<String, List<IntRange>> {
        val entries = split(": ", " or ")
        val name = entries[0]
        return name to entries.drop(1).map { it.toRange() }
    }

    fun String.toTicket(): List<Int> = split(",").map { it.toInt() }

    val (fields, myTicket, nearbyTickets) = splitByEmptyLines()
    return Input(
        fields.lineSequence()
            .map { it.toField() }
            .toMap(),
        myTicket.lines()[1].toTicket(),
        nearbyTickets.lineSequence()
            .drop(1)
            .map { it.toTicket() }
            .toList(),
    )
}

private data class Input(
    val fields: Map<String, List<IntRange>>,
    val myTicket: List<Int>,
    val nearbyTickets: List<List<Int>>,
)
