package de.cramer.adventofcode.year2020.day18

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 26457)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 694173)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Token>>): Long {
    fun List<Token>.toExpression(): Expression {
        val tokens = toMutableList()
        val expressionSetter = ExpressionSetter()

        while (tokens.isNotEmpty()) {
            when (val first = tokens.removeFirst()) {
                is Number -> expressionSetter.expression = first
                is Operator -> expressionSetter.operator = first
                Parenthesis.OPEN -> {
                    val closingParenthesisIndex = tokens.findClosingParenthesis()
                    expressionSetter.expression = tokens.subList(0, closingParenthesisIndex).toExpression()
                    repeat(closingParenthesisIndex + 1) { tokens.removeFirst() }
                }

                else -> error("should not happen")
            }
        }

        return expressionSetter.expression
    }

    return input
        .sumOf { it.toExpression()() }
}

private fun problem02(input: List<List<Token>>): Long {
    fun List<Token>.toExpression(): Expression {
        val tokens = toMutableList()

        while (Parenthesis.OPEN in tokens) {
            val openingParenthesis = tokens.indexOf(Parenthesis.OPEN)
            val closingParenthesis = tokens.subList(openingParenthesis + 1, tokens.size).findClosingParenthesis()
            val expression = tokens.subList(openingParenthesis + 1, openingParenthesis + 1 + closingParenthesis).toExpression()
            repeat(closingParenthesis + 2) { tokens.removeAt(openingParenthesis) }
            tokens.add(openingParenthesis, expression)
        }

        require(Parenthesis.CLOSE !in tokens) { error("tokens should not contain closing parenthesis") }

        while (Operator.PLUS in tokens) {
            val plusOperator = tokens.indexOf(Operator.PLUS)
            val plusOperationStart = plusOperator - 1
            val left = tokens[plusOperationStart]
            val right = tokens[plusOperator + 1]
            val expression = Operation(Operator.PLUS, left as Expression, right as Expression)
            repeat(3) { tokens.removeAt(plusOperationStart) }
            tokens.add(plusOperationStart, expression)
        }

        require(Operator.PLUS !in tokens) { error("tokens should not contain plus operator") }

        val expressionSetter = ExpressionSetter()
        while (tokens.isNotEmpty()) {
            when (val first = tokens.removeFirst()) {
                is Expression -> expressionSetter.expression = first
                is Operator -> expressionSetter.operator = first
                else -> error("should not happen")
            }
        }

        return expressionSetter.expression
    }

    return input
        .sumOf { it.toExpression()() }
}

private fun String.parse(): List<List<Token>> {
    fun Char.toOperator() = when (this) {
        '+' -> Operator.PLUS
        '*' -> Operator.TIMES
        else -> error("cannot parse $this to operator")
    }

    fun String.tokenize(): List<Token> {
        var string = this
        return buildList {
            while (string.isNotEmpty()) {
                val first = string.first()
                string = string.substring(
                    if (first.isDigit()) {
                        val raw = string.takeWhile { it.isDigit() }
                        this += Number(raw.toLong())
                        raw.length
                    } else {
                        this += when (first) {
                            '+', '*' -> first.toOperator()
                            '(' -> Parenthesis.OPEN
                            ')' -> Parenthesis.CLOSE
                            else -> error("cannot happen")
                        }
                        1
                    }
                )
            }
        }
    }

    return lineSequence()
        .map { it.replace(" ", "") }
        .map { it.tokenize() }
        .toList()
}

private fun List<Token>.findClosingParenthesis(): Int {
    var openParentheses = 1
    forEachIndexed { index, c ->
        if (c == Parenthesis.OPEN) {
            openParentheses++
        } else if (c == Parenthesis.CLOSE) {
            openParentheses--
        }
        if (openParentheses == 0) {
            return index
        }
    }
    error("should not happen")
}

private sealed interface Token

private sealed interface Expression : Token {
    operator fun invoke(): Long
}

private data class Operation(
    val operator: Operator,
    val left: Expression,
    val right: Expression,
) : Expression {
    override fun invoke(): Long = when (operator) {
        Operator.PLUS -> left() + right()
        Operator.TIMES -> left() * right()
    }
}

private data class Number(
    val value: Long,
) : Expression, Token {
    override fun invoke(): Long = value
}

private enum class Operator : Token {
    PLUS, TIMES
}

private enum class Parenthesis : Token {
    OPEN, CLOSE
}

private class ExpressionSetter {
    private var mExpression: Expression? = null
    var operator: Operator? = null
    var expression: Expression
        get() = mExpression!!
        set(value) = if (mExpression != null && operator != null) {
            mExpression = Operation(operator!!, mExpression!!, value)
            operator = null
        } else {
            mExpression = value
        }
}
