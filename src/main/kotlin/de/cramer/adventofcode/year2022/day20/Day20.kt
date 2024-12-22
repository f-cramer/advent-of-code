package de.cramer.adventofcode.year2022.day20

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runTest

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1623178306L)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input) }
}

private fun problem01(input: List<Int>): Long {
    return decrypt(input, 1, 1)
}

private fun problem02(input: List<Int>): Long {
    return decrypt(input, 10, 811589153)
}

private fun decrypt(input: List<Int>, rounds: Int, key: Long): Long {
    val originalState = input.mapIndexed { index, value -> NumberWithOriginalIndex(value * key, index) }
    val data = originalState.toMutableList()

    repeat(rounds) {
        originalState.forEach {
            val index = data.indexOf(it).takeIf { index -> index >= 0 } ?: error("cannot find element $it")
            data.move(index, it.value)
        }
    }

    fun List<NumberWithOriginalIndex>.getWrapping(index: Int) = this[index % size].value

    val zeroIndex = data.indexOfFirst { it.value == 0L }
    val thousand = data.getWrapping(zeroIndex + 1000)
    val twoThousand = data.getWrapping(zeroIndex + 2000)
    val threeThousand = data.getWrapping(zeroIndex + 3000)
    return thousand + twoThousand + threeThousand
}

private fun <T> MutableList<T>.move(fromIndex: Int, byPlaces: Long) {
    require(fromIndex in indices) { "fromIndex ($fromIndex) not in $indices" }

    if (byPlaces == 0L) {
        return
    }

    val toIndex = (fromIndex + byPlaces).mod(size - 1)
    if (fromIndex == toIndex) {
        return
    }

    val element = removeAt(fromIndex)
    add(toIndex, element)
}

private fun String.parse(): List<Int> {
    return lineSequence()
        .map { it.toInt() }
        .toList()
}

private data class NumberWithOriginalIndex(
    val value: Long,
    val originalIndex: Int,
)
