package de.cramer.adventofcode.year2020.day13

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 295)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1068781)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    return input.busses.asSequence()
        .filterNotNull()
        .map { it to (it - input.timestamp % it) }
        .minBy { it.second }
        .let { (bus, timeToWait) -> bus * timeToWait }
}

private fun problem02(input: Input): Long {
    val bussesWithIndex = input.busses.asSequence()
        .mapIndexed { index, bus -> index to bus }
        .filterNot { (_, bus) -> bus == null }
        .map { (index, bus) -> index.toLong() to bus!!.toLong() }
        .map { (index, bus) -> bus - index to bus }
        .toList()
    return chineseRemaindersTheorem(bussesWithIndex)
}

private fun String.parse(): Input {
    fun String.toBusses(): List<Int?> = splitToSequence(",")
        .map { if (it == "x") null else it.toInt() }
        .toList()

    val (first, second) = lines()
    return Input(first.toInt(), second.toBusses())
}

private data class Input(
    val timestamp: Int,
    val busses: List<Int?>,
)

private fun chineseRemaindersTheorem(modulos: List<Pair<Long, Long>>): Long {
    val n = modulos.asSequence()
        .map { it.second }
        .reduce { acc, i -> acc * i }
    return modulos.sumOf { (b, mod) ->
        val ni = n / mod
        b * ni * ni.getInverse(mod)
    } % n
}

private fun Long.getInverse(modulus: Long): Long = (1 until modulus)
    .first { (this * it) % modulus == 1L }
