package de.cramer.adventofcode.year2023.day5

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines
import java.util.stream.IntStream

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 35u)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 46u)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): UInt {
    return input.seeds.minOf {
        input.conversions.fold(it) { acc, conversion ->
            conversion.convert(acc)
        }
    }
}

private fun problem02(input: Input): UInt {
    val seeds = input.seeds.windowed(2, 2) { (start, length) -> start..<start + length }
    val conversions = input.conversions.reversed().toList()
    return IntStream.iterate(0) { it + 1 }.parallel()
        .filter { output ->
            val it = output.toUInt()
            val seed = conversions.fold(it) { acc, conversion -> conversion.reverseConvert(acc) }
            seeds.any { s -> seed in s }
        }
        .findFirst().asInt.toUInt()
}

private fun String.parse(): Input {
    val split = splitByEmptyLines()
    val seeds = split.first().removePrefix("seeds: ").split(" ")
        .map { it.toUInt() }
    val conversions = split.asSequence()
        .drop(1)
        .map { it.parseConversionMap() }
        .toList()
    return Input(seeds, conversions)
}

private fun String.parseConversionMap(): ConversionMap {
    val conversions = lineSequence()
        .drop(1)
        .map { it.parseConversion() }
        .toList()
    return ConversionMap(conversions)
}

private fun String.parseConversion(): Conversion {
    val (outputStart, inputStart, length) = split(" ").map { it.toUInt() }
    return Conversion(inputStart, outputStart, length)
}

private data class Input(
    val seeds: List<UInt>,
    val conversions: List<ConversionMap>,
)

private data class ConversionMap(
    val conversions: List<Conversion>,
) {
    fun convert(input: UInt): UInt = conversions.asSequence()
        .mapNotNull { it.convert(input) }
        .firstOrNull() ?: input

    fun reverseConvert(output: UInt): UInt = conversions.asSequence()
        .mapNotNull { it.reverseConvert(output) }
        .firstOrNull() ?: output
}

private data class Conversion(
    val inputStart: UInt,
    val outputStart: UInt,
    val length: UInt,
) {
    fun convert(input: UInt): UInt? {
        return if (input >= inputStart && input < inputStart + length) {
            outputStart + (input - inputStart)
        } else {
            null
        }
    }

    fun reverseConvert(output: UInt): UInt? {
        return if (output >= outputStart && output < outputStart + length) {
            inputStart + (output - outputStart)
        } else {
            null
        }
    }
}
