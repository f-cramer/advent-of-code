package de.cramer.adventofcode.year2023.day15

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 1320)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 145)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<String>): Int {
    return input.sumOf { it.hash() }
}

private fun problem02(input: List<String>): Int {
    data class Lens(val label: String, var focalLength: Int)

    val boxes = Array(256) { mutableListOf<Lens>() }

    input.forEach { instruction ->
        val (label, focalLengthString) = if ("=" in instruction) {
            instruction.split("=")
        } else {
            listOf(instruction.dropLast(1), "")
        }

        val boxIndex = label.hash()
        val box = boxes[boxIndex]

        if (focalLengthString.isEmpty()) {
            box.removeIf { it.label == label }
        } else {
            val focalLength = focalLengthString.toInt()
            val existingLens = box.firstOrNull { it.label == label }
            if (existingLens == null) {
                box += Lens(label, focalLength)
            } else {
                existingLens.focalLength = focalLength
            }
        }
    }

    return boxes.asSequence()
        .mapIndexed { boxIndex, box ->
            box.mapIndexed { lensIndex, lens ->
                (boxIndex + 1) * (lensIndex + 1) * lens.focalLength
            }.sum()
        }.sum()
}

private fun String.hash(): Int {
    var result = 0
    for (c in this) {
        result += c.code
        result *= 17
        result %= 256
    }
    return result
}

private fun String.parse(): List<String> {
    return split(",")
}
