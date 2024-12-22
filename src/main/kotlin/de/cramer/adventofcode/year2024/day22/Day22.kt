package de.cramer.adventofcode.year2024.day22

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.readTestInput2
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 37327623L)
    val testInput2 = readTestInput2().parse()
    val testResult02 = problem02(testInput2)
    checkTestResult(testResult02, 23)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Int>): Long {
    return input.sumOf {
        it.generateSecretNumbers().last().toLong()
    }
}

private fun problem02(input: List<Int>): Int {
    val windowSize = 4

    val changeWindowCache = mutableMapOf<List<Int>, List<Int>>()
    fun List<Int>.getChangeWindows(): List<Int> {
        return changeWindowCache.getOrPut(this) {
            zipWithNext { a, b -> b.mod(10) - a.mod(10) }
                .windowed(windowSize) { (a, b, c, d) ->
                    if (a + b + c + d <= 0) -1 else (d + 9) + (c + 9) * 19 + (b + 9) * 19 * 19 + (a + 9) * 19 * 19 * 19
                }
        }
    }

    val bananas = IntArray(19 * 19 * 19 * 19)
    input.forEach {
        val secretNumbers = it.generateSecretNumbers().toList()
        val visitedWindows = BooleanArray(19 * 19 * 19 * 19)
        secretNumbers.getChangeWindows().forEachIndexed { i, cw ->
            if (cw < 0 || visitedWindows[cw]) return@forEachIndexed
            visitedWindows[cw] = true
            bananas[cw] += secretNumbers[i + windowSize].mod(10)
        }
    }
    return bananas.max()
}

fun Int.generateSecretNumbers(): Sequence<Int> {
    fun Long.mix(other: Long) = this xor other

    fun Long.prune(): Int = mod(16777216)

    fun Int.nextSecret(): Int {
        val step1 = toLong().mix(this * 64L).prune()
        val step2 = step1.toLong().mix(step1 / 32L).prune()
        val step3 = step2.toLong().mix(step2 * 2048L).prune()
        return step3
    }

    return sequence {
        var secret = this@generateSecretNumbers
        yield(secret)
        for (i in 1..2000) {
            secret = secret.nextSecret()
            yield(secret)
        }
    }
}

private fun String.parse(): List<Int> {
    return lines()
        .map { it.toInt() }
}
