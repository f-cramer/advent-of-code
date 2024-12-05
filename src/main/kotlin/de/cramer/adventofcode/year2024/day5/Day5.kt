package de.cramer.adventofcode.year2024.day5

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 143)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 123)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    return input.updates.asSequence()
        .filter { it.isCorrectlyOrdered(input.pageOrders) }
        .sumOf { it[it.size / 2] }
}

private fun problem02(input: Input): Int {
    fun MutableList<Int>.swap(before: Int, after: Int) {
        val temp = this[before]
        this[before] = this[after]
        this[after] = temp
    }

    return input.updates.asSequence()
        .filterNot { it.isCorrectlyOrdered(input.pageOrders) }
        .map {
            val list = it.toMutableList()
            while (!list.isCorrectlyOrdered(input.pageOrders)) {
                for (pageOrder in input.pageOrders) {
                    while (!list.isCorrectlyOrdered(pageOrder)) {
                        val beforeIndex = list.indexOf(pageOrder.before)
                        val afterIndex = list.indexOf(pageOrder.after)
                        list.swap(beforeIndex, beforeIndex - 1)
                    }
                }
            }
            list
        }
        .sumOf { it[it.size / 2] }
}

private fun Update.isCorrectlyOrdered(pageOrder: PageOrder): Boolean {
    val beforeIndex = indexOf(pageOrder.before)
    val afterIndex = indexOf(pageOrder.after)
    return beforeIndex < 0 || afterIndex < 0 || beforeIndex < afterIndex
}

private fun Update.isCorrectlyOrdered(pageOrders: List<PageOrder>): Boolean =
    pageOrders.all { isCorrectlyOrdered(it) }

private fun String.parse(): Input {
    val (rawPageOrders, rawUpdates) = splitByEmptyLines()

    fun String.parsePageOrder(): PageOrder {
        val (before, after) = split("|")
        return PageOrder(before.toInt(), after.toInt())
    }

    val pageOrders = rawPageOrders.trim().lines()
        .map { it.parsePageOrder() }

    fun String.parseUpdate(): Update {
        return split(",").map { it.toInt() }
    }

    val updates = rawUpdates.trim().lines()
        .map { it.parseUpdate() }

    return Input(pageOrders, updates)
}

private data class Input(
    val pageOrders: List<PageOrder>,
    val updates: List<Update>,
)

private data class PageOrder(
    val before: Int,
    val after: Int,
)

private typealias Update = List<Int>
