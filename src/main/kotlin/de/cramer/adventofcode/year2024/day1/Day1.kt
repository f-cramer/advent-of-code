package de.cramer.adventofcode.year2024.day1

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 11)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 31)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Pair<List<Int>, List<Int>>): Int {
    var (l1, l2) = input
    l1 = l1.sorted()
    l2 = l2.sorted()
    return l1.zip(l2) { a, b -> abs(a - b) }.sum()
}

private fun problem02(input: Pair<List<Int>, List<Int>>): Int {
    var (l1, l2) = input
    l2 = l2.sorted()

    return l1.asSequence()
        .map { it * l2.count { n -> n == it } }
        .sum()
}

private fun String.parse(): Pair<List<Int>, List<Int>> {
    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()
    this.trim().lines().forEach {
        val (s1, s2) = it.split(Regex(" +"))
        list1.add(s1.toInt())
        list2.add(s2.toInt())
    }

    return list1 to list2
}
