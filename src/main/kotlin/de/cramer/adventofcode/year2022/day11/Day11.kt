package de.cramer.adventofcode.year2022.day11

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 10605L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2713310158L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Monkey>): Long {
    val monkeys = input.map { it.copy(items = it.items.toMutableList()) }
    val modulo = (monkeys.map { it.testDivider } + 3).distinct().reduce { acc, v -> acc * v }
    repeat(20) {
        monkeys.performOneRoundOfMonkeyBusiness { level -> (level / 3) % modulo }
    }

    return monkeys.getLevelOfMonkeyBusiness()
}

private fun problem02(input: List<Monkey>): Long {
    val monkeys = input.map { it.copy(items = it.items.toMutableList()) }
    val modulo = monkeys.asSequence().map { it.testDivider }.distinct().reduce { acc, v -> acc * v }
    repeat(10000) {
        monkeys.performOneRoundOfMonkeyBusiness { level -> level % modulo }
    }

    return monkeys.getLevelOfMonkeyBusiness()
}

private fun List<Monkey>.performOneRoundOfMonkeyBusiness(operation: (Long) -> Long) {
    for (monkey in this) {
        val items = monkey.items.toList()
        monkey.items.clear()
        for (item in items) {
            monkey.totalInspections++
            val newItemValue = operation(monkey.operation(item))
            val nextMonkeyIndex = if (newItemValue % monkey.testDivider == 0L) {
                monkey.nextMonkeyIfTestTrue
            } else {
                monkey.nextMonkeyIfTestFalse
            }
            this[nextMonkeyIndex].items += newItemValue
        }
    }
}

private fun List<Monkey>.getLevelOfMonkeyBusiness(): Long {
    return asSequence()
        .map { it.totalInspections }
        .sortedDescending()
        .take(2)
        .toList()
        .let { (a, b) ->
            a * b
        }
}

private fun String.parse(): List<Monkey> {
    return splitByEmptyLines()
        .map { it.toMonkey() }
}

private data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val testDivider: Int,
    val nextMonkeyIfTestTrue: Int,
    val nextMonkeyIfTestFalse: Int,
    var totalInspections: Long = 0,
)

private fun String.toMonkey(): Monkey {
    fun String.toOperation(): (Long) -> Long = when (this[0]) {
        '*' -> {
            val sFactor = substring(2)
            if (sFactor == "old") {
                { it * it }
            } else {
                val factor = sFactor.toLong()
                val op = { it: Long -> it * factor }
                op
            }
        }

        '+' -> {
            val factor = substring(2).toLong()
            val operation = { it: Long -> it + factor }
            operation
        }

        else -> {
            error("cannot parse operation from $this")
        }
    }

    val (sItems, sOperation, sTestDivider, sNextTrue, sNextFalse) = lines().drop(1)
    val items = sItems.substringAfter("Starting items: ").split(", ")
        .map { it.toLong() }
    val operation = sOperation.substringAfter("Operation: new = old ").toOperation()
    val testDivider = sTestDivider.substringAfter("Test: divisible by ").toInt()
    val nextTrue = sNextTrue.substringAfter("If true: throw to monkey ").toInt()
    val nextFalse = sNextFalse.substringAfter("If false: throw to monkey ").toInt()
    return Monkey(items.toMutableList(), operation, testDivider, nextTrue, nextFalse)
}
