package de.cramer.adventofcode.year2024.day13

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines
import de.cramer.adventofcode.utils.vector.LongVector
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.toLong
import java.math.MathContext
import java.math.RoundingMode

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 480)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 875318608908)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Input>): Int {
    return input.sumOf {
        solve(it.a.toLong(), it.b.toLong(), it.target.toLong()).toInt()
    }
}

private fun problem02(input: List<Input>): Long {
    return input.sumOf { inp ->
        val realA = inp.a.toLong()
        val realB = inp.b.toLong()
        val realTarget = LongVector(inp.target.x + 10000000000000, inp.target.y + 10000000000000)

        solve(realA, realB, realTarget)
    }
}

private fun solve(a: LongVector, b: LongVector, target: LongVector): Long {
    val context = MathContext.DECIMAL128
    val scale = 30
    val ax = a.x.toBigDecimal(context).setScale(scale)
    val ay = a.y.toBigDecimal(context).setScale(scale)
    val bx = b.x.toBigDecimal(context).setScale(scale)
    val by = b.y.toBigDecimal(context).setScale(scale)
    val tx = target.x.toBigDecimal(context).setScale(scale)
    val ty = target.y.toBigDecimal(context).setScale(scale)

    val f = bx / by
    val x = (tx - f * ty) / (ax - f * ay)
    val y = (ty - ay * x) / by

    val roundX = x.setScale(0, RoundingMode.HALF_UP)
    val roundY = y.setScale(0, RoundingMode.HALF_UP)
    return if ((x - roundX).abs(context).toDouble() < 0.00001 && (y - roundY).abs(context).toDouble() < 0.00001) {
        3 * roundX.toLong() + roundY.toLong()
    } else {
        0
    }
}

private fun String.parse(): List<Input> {
    val aRegex = """Button A: X\+(\d+), Y\+(\d+)""".toRegex()
    val bRegex = """Button B: X\+(\d+), Y\+(\d+)""".toRegex()
    val targetRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()

    fun String.parseInput(): Input {
        val (aString, bString, targetString) = lines()
        val (ax, ay) = aRegex.matchEntire(aString)!!.destructured
        val (bx, by) = bRegex.matchEntire(bString)!!.destructured
        val (tx, ty) = targetRegex.matchEntire(targetString)!!.destructured
        return Input(Vector(ax.toInt(), ay.toInt()), Vector(bx.toInt(), by.toInt()), Vector(tx.toInt(), ty.toInt()))
    }

    return splitByEmptyLines()
        .map { it.parseInput() }
}

private data class Input(
    val a: Vector,
    val b: Vector,
    val target: Vector,
)
