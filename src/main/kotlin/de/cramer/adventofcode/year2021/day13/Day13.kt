package de.cramer.adventofcode.year2021.day13

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 17)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, Unit)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Manual): Int {
    val instruction = input.folds.first()
    return input.dots.fold(instruction).size
}

private fun problem02(input: Manual) {
    fun Set<Vector>.print() {
        (0..maxOf { it.y }).forEach { y ->
            (0..maxOf { it.x }).forEach { x ->
                print(if (Vector(x, y) in this) '#' else ' ')
            }
            println()
        }
    }

    input.folds.fold(input.dots, Set<Vector>::fold).print()
}

private fun String.parse(): Manual {
    fun String.toVector(): Vector {
        val (x, y) = split(",")
        return Vector(x.toInt(), y.toInt())
    }

    fun String.toDots(): Set<Vector> = lineSequence()
        .map { it.toVector() }
        .toSet()

    fun String.toFoldInstruction(): FoldInstruction {
        val (axis, offset) = substringAfter("fold along ").split("=")
        return when (axis) {
            "x" -> XFoldInstruction(offset.toInt())
            "y" -> YFoldInstruction(offset.toInt())
            else -> error("cannot parse $this to fold instruction")
        }
    }

    fun String.toFoldInstructions(): List<FoldInstruction> = lineSequence()
        .map { it.toFoldInstruction() }
        .toList()

    val (dots, folds) = splitByEmptyLines()
    return Manual(dots.toDots(), folds.toFoldInstructions())
}

private fun Set<Vector>.fold(instruction: FoldInstruction): Set<Vector> {
    return when (instruction) {
        is XFoldInstruction -> {
            map {
                if (it.x < instruction.x) {
                    it
                } else {
                    Vector(instruction.x - (it.x - instruction.x), it.y)
                }
            }
        }

        is YFoldInstruction -> {
            map {
                if (it.y < instruction.y) {
                    it
                } else {
                    Vector(it.x, instruction.y - (it.y - instruction.y))
                }
            }
        }
    }.toSet()
}

private data class Manual(
    val dots: Set<Vector>,
    val folds: List<FoldInstruction>,
)

private sealed interface FoldInstruction

private data class XFoldInstruction(val x: Int) : FoldInstruction
private data class YFoldInstruction(val y: Int) : FoldInstruction
