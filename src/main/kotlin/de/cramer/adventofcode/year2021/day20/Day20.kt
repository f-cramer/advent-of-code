package de.cramer.adventofcode.year2021.day20

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.get
import de.cramer.adventofcode.isValidIndex
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 35)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 3351)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    val enhanced = input.image.enhance(2, input.algorithm)
    return enhanced.sumOf { l -> l.sumOf { it.value } }
}

private fun problem02(input: Input): Int {
    val enhanced = input.image.enhance(50, input.algorithm)
    return enhanced.sumOf { l -> l.sumOf { it.value } }
}

private fun List<List<Pixel>>.print() {
    fun Pixel.toChar() = when (this) {
        Pixel.LIGHT -> '#'
        Pixel.DARK -> ' '
    }

    for (y in indices) {
        for (x in this[y].indices) {
            print(this[y][x].toChar())
        }
        println()
    }
    println()
}

private fun List<List<Pixel>>.enhance(rounds: Int, algorithm: List<Pixel>): List<List<Pixel>> {
    fun List<List<Pixel>>.ensurePadding(padding: Pixel): List<List<Pixel>> {
        if (isEmpty()) {
            return listOf(listOf(Pixel.DARK))
        }

        val addFirstRow = first().any { it == Pixel.LIGHT }
        val addLastRow = last().any { it == Pixel.LIGHT }
        val addFirstColumn = any { it.isNotEmpty() && it.first() == Pixel.LIGHT }
        val addLastColumn = any { it.isNotEmpty() && it.last() == Pixel.LIGHT }

        val columns = first().size

        return buildList<MutableList<Pixel>> {
            if (addFirstRow) add(0, MutableList(columns) { padding })
            addAll(this@ensurePadding.map { it.toMutableList() }.toMutableList())
            if (addLastRow) add(MutableList(columns) { padding })
            if (addFirstColumn) forEach { it.add(0, padding) }
            if (addLastColumn) forEach { it.add(padding) }
        }
    }

    var image = this

    fun Vector.getAlgorithmIndex(fallback: Pixel): Int = sequenceOf(
        Vector(x - 1, y - 1), Vector(x, y - 1), Vector(x + 1, y - 1),
        Vector(x - 1, y), this, Vector(x + 1, y),
        Vector(x - 1, y + 1), Vector(x, y + 1), Vector(x + 1, y + 1),
    )
        .map { (if (image.isValidIndex(it)) image[it] else fallback).value }
        .joinToString(separator = "").toInt(2)

    repeat(rounds) {
        val fallback = if (algorithm[0] == Pixel.LIGHT) if (it % 2 == 0) Pixel.DARK else Pixel.LIGHT else Pixel.DARK
        image = image.ensurePadding(fallback)
        image = buildList {
            for (y in image.indices) {
                add(
                    buildList {
                        for (x in image[y].indices) {
                            val index = Vector(x, y).getAlgorithmIndex(fallback)
                            add(algorithm[index])
                        }
                    }
                )
            }
        }
    }

    return image
}

private fun String.parse(): Input {
    fun Char.toPixel() = when (this) {
        '#' -> Pixel.LIGHT
        '.' -> Pixel.DARK
        else -> error("cannot parse $this to pixel")
    }

    val (alg, img) = splitByEmptyLines()
    val algorithm = alg.lines().joinToString(separator = "")
        .map { it.toPixel() }
    val image = img.lineSequence()
        .map { it.map { c -> c.toPixel() } }
        .toList()
    return Input(image, algorithm)
}

private data class Input(
    val image: List<List<Pixel>>,
    val algorithm: List<Pixel>,
)

private enum class Pixel(
    val value: Int,
) {
    LIGHT(1), DARK(0),
}
