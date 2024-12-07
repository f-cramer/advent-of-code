package de.cramer.adventofcode.year2024.day7

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.pow

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3749)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 11387)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(inputs: List<Input>): Long {
    val possibleOperators = listOf(Operator.ADD, Operator.MULTIPLY)
    return getTotalOfValidInputs(inputs, possibleOperators)
}

private fun problem02(inputs: List<Input>): Long {
    val possibleOperators = listOf(Operator.ADD, Operator.MULTIPLY, Operator.CONCAT)
    return getTotalOfValidInputs(inputs, possibleOperators)
}

private fun getTotalOfValidInputs(inputs: List<Input>, possibleOperators: List<Operator>): Long {
    return inputs.asSequence()
        .filter {
            val numberOfOperators = it.numbers.size - 1
            val operatorSets = (0..<possibleOperators.size.toDouble().pow(numberOfOperators).toInt()).asSequence()
                .map { i ->
                    i.toString(possibleOperators.size).padStart(numberOfOperators, '0').toCharArray().map { c ->
                        possibleOperators[c.digitToInt(possibleOperators.size)]
                    }
                }
            operatorSets.any { operators ->
                it.numbers.reduceIndexed { index, acc, i ->
                    val result = operators[index - 1].function(acc, i)
                    if (result > it.total) {
                        return@any false
                    }
                    result
                } == it.total
            }
        }
        .sumOf { it.total }
}

private fun String.parse(): List<Input> {
    return lines()
        .map {
            val (totalString, numbersString) = it.split(": ")
            val total = totalString.toLong()
            val numbers = numbersString.split(" ").map { n -> n.toLong() }
            Input(total, numbers)
        }
}

private data class Input(
    val total: Long,
    val numbers: List<Long>,
)

private enum class Operator(
    val function: (Long, Long) -> Long,
) {
    ADD({ a, b -> a + b }),
    MULTIPLY({ a, b -> a * b }),
    CONCAT({ a, b -> (a.toString() + b.toString()).toLong() }),
}
