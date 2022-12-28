package de.cramer.adventofcode.year2022.day21

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runTest

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 152L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 301L)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input) }
}

private fun problem01(input: Map<String, Monkey>): Long {
    return input.getValue("root").calculate(input)
}

private fun problem02(input: Map<String, Monkey>): Long {
    data class CalculationData(val resultWithoutMe: Long, val operandWithMe: String, val switched: Boolean)

    val humn = "humn"
    val inputWithoutMe = input - humn

    fun CalculationMonkey.getCalculationData(): CalculationData = try {
        val value1 = inputWithoutMe.getValue(operand1).calculate(inputWithoutMe)
        CalculationData(value1, operand2, false)
    } catch (e: Exception) {
        val value2 = inputWithoutMe.getValue(operand2).calculate(inputWithoutMe)
        CalculationData(value2, operand1, true)
    }

    fun getHumnValue(monkeyName: String, result: Long): Long {
        if (monkeyName == humn) return result

        val monkey = input.getValue(monkeyName)
        if (monkey is NumberMonkey) {
            return if (monkey.number == result) result else error("monkey number ${monkey.number} does not match expected result $result")
        }

        if (monkey !is CalculationMonkey) error("monkey has to be a CalculationMonkey")

        val (resultWithoutMe, operandWithMe, switched) = monkey.getCalculationData()
        val reverse = if (switched) monkey.operation.reverseForFirstOperand else monkey.operation.reverseForSecondOperand
        return getHumnValue(operandWithMe, reverse(result, resultWithoutMe))
    }

    val root = input.getValue("root") as CalculationMonkey
    val (resultWithoutMe, operandWithMe) = root.getCalculationData()

    return getHumnValue(operandWithMe, resultWithoutMe)
}

private val calculationPattern = """([a-zA-Z]+) ([+\-*/]) ([a-zA-Z]+)""".toRegex()

private fun String.parse(): Map<String, Monkey> {
    fun String.toOperation(): Operation = when (this) {
        "+" -> Operation.ADDITION
        "-" -> Operation.SUBTRACTION
        "*" -> Operation.MULTIPLICATION
        "/" -> Operation.DIVISION
        else -> error("cannot parse \"$this\" to operation")
    }

    fun String.toMonkey(): Monkey {
        val (name, op) = split(": ")
        return try {
            val number = op.toLong()
            NumberMonkey(name, number)
        } catch (e: NumberFormatException) {
            val (_, op1, operation, op2) = calculationPattern.matchEntire(op)?.groupValues ?: error("operation \"$op\"does not match pattern")
            CalculationMonkey(name, operation.toOperation(), op1, op2)
        }
    }

    return lineSequence()
        .map { it.toMonkey() }
        .associateBy { it.name }
}

private sealed interface Monkey {
    val name: String
    fun calculate(monkeys: Map<String, Monkey>): Long
}

private data class NumberMonkey(
    override val name: String,
    val number: Long,
) : Monkey {
    override fun calculate(monkeys: Map<String, Monkey>): Long = number
}

private data class CalculationMonkey(
    override val name: String,
    val operation: Operation,
    val operand1: String,
    val operand2: String,
) : Monkey {
    override fun calculate(monkeys: Map<String, Monkey>): Long {
        val op1 = monkeys.getValue(operand1).calculate(monkeys)
        val op2 = monkeys.getValue(operand2).calculate(monkeys)
        return operation(op1, op2)
    }
}

private enum class Operation(
    private val operation: (Long, Long) -> Long,
) : (Long, Long) -> Long {
    ADDITION({ a, b -> Math.addExact(a, b) }),
    SUBTRACTION({ a, b -> Math.subtractExact(a, b) }),
    MULTIPLICATION({ a, b -> Math.multiplyExact(a, b) }),
    DIVISION({ a, b -> (a / b).also { require(it * b == a) { "$a / $b == ${a / b.toDouble()}" } } }),
    ;

    override operator fun invoke(op1: Long, op2: Long) = operation(op1, op2)
}

private val Operation.reverseForFirstOperand: (Long, Long) -> Long
    get() = when (this) {
        Operation.ADDITION -> Operation.SUBTRACTION
        Operation.SUBTRACTION -> Operation.ADDITION
        Operation.MULTIPLICATION -> Operation.DIVISION
        Operation.DIVISION -> Operation.MULTIPLICATION
    }

private val Operation.reverseForSecondOperand: (Long, Long) -> Long
    get() = when (this) {
        Operation.ADDITION -> Operation.SUBTRACTION
        Operation.SUBTRACTION -> {
            { a, b -> b - a }
        }

        Operation.MULTIPLICATION -> Operation.DIVISION
        Operation.DIVISION -> {
            { a, b -> b / a }
        }
    }
