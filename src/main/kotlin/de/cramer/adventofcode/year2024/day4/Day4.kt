package de.cramer.adventofcode.year2024.day4

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector
import de.cramer.adventofcode.utils.vector.get
import de.cramer.adventofcode.utils.vector.isValidIndex
import de.cramer.adventofcode.utils.vector.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 18)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 9)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Byte>>): Int {
    val word = "XMAS".toByteArray().toList()
    val wordSize = word.size
    val possibleSequences =
        // left to right
        input.asSequence()
            .flatMapIndexed { y, bytes ->
                bytes.indices.mapNotNull { x ->
                    if (x + wordSize <= bytes.size) (0..<wordSize).map { Vector(x + it, y) } else null
                }
            } +
            // right to left
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (x - wordSize + 1 >= 0) (0..<wordSize).map { Vector(x - it, y) } else null
                    }
                } +
            // top to bottom
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (y + wordSize <= input.size) (0..<wordSize).map { Vector(x, y + it) } else null
                    }
                } +
            // bottom to top
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (y - wordSize + 1 >= 0) (0..<wordSize).map { Vector(x, y - it) } else null
                    }
                } +
            // top left to bottom right
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (y + wordSize <= input.size && x + wordSize <= bytes.size) (0..<wordSize).map { Vector(x + it, y + it) } else null
                    }
                } +
            // bottom left to top right
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (y - wordSize + 1 >= 0 && x + wordSize <= bytes.size) (0..<wordSize).map { Vector(x + it, y - it) } else null
                    }
                } +
            // top right to bottom left
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (y + wordSize <= input.size && x - wordSize + 1 >= 0) (0..<wordSize).map { Vector(x - it, y + it) } else null
                    }
                } +
            // bottom right to top left
            input.asSequence()
                .flatMapIndexed { y, bytes ->
                    bytes.indices.mapNotNull { x ->
                        if (y - wordSize + 1 >= 0 && x - wordSize + 1 >= 0) (0..<wordSize).map { Vector(x - it, y - it) } else null
                    }
                }

    return possibleSequences
        .count { vectors -> vectors.map { input[it] } == word }
}

private fun problem02(input: List<List<Byte>>): Int {
    fun List<Vector>.isValid() = all { input.isValidIndex(it) }

    val word = "MAS".toByteArray().toList()
    val wordSize = word.size
    return input.vectors()
        .map {
            listOf(Vector(it.x - 1, it.y - 1), it, Vector(it.x + 1, it.y + 1)) to
                listOf(Vector(it.x + 1, it.y - 1), it, Vector(it.x - 1, it.y + 1))
        }
        .filter { (leftToRight, rightToLeft) -> leftToRight.isValid() && rightToLeft.isValid() }
        .count { (leftToRight, rightToLeft) ->
            (leftToRight.map { input[it] } == word && rightToLeft.map { input[it] } == word) ||
                (leftToRight.asReversed().map { input[it] } == word && rightToLeft.map { input[it] } == word) ||
                (leftToRight.map { input[it] } == word && rightToLeft.asReversed().map { input[it] } == word) ||
                (leftToRight.asReversed().map { input[it] } == word && rightToLeft.asReversed().map { input[it] } == word)
        }
}

private fun String.parse(): List<List<Byte>> = lineSequence()
    .map { it.toByteArray().toList() }
    .toList()
