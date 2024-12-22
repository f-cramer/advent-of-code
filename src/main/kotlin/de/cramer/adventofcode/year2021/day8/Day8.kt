package de.cramer.adventofcode.year2021.day8

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 26)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 61229)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<DisplayState>): Int {
    return input.sumOf { state ->
        state.outputs.count { it.size in listOf(2, 3, 4, 7) }
    }
}

private fun problem02(input: List<DisplayState>): Int {
    val allSegments = setOf('a', 'b', 'c', 'd', 'e', 'f', 'g')
    val segmentToNumberMapping: Map<Set<Char>, Char> = mapOf(
        setOf('a', 'b', 'c', 'e', 'f', 'g') to '0',
        setOf('c', 'f') to '1',
        setOf('a', 'c', 'd', 'e', 'g') to '2',
        setOf('a', 'c', 'd', 'f', 'g') to '3',
        setOf('b', 'c', 'd', 'f') to '4',
        setOf('a', 'b', 'd', 'f', 'g') to '5',
        setOf('a', 'b', 'd', 'e', 'f', 'g') to '6',
        setOf('a', 'c', 'f') to '7',
        allSegments to '8',
        setOf('a', 'b', 'c', 'd', 'f', 'g') to '9',
    )

    fun Collection<Set<Char>>.intersectAll(): Set<Char> = fold(allSegments, Set<Char>::intersect)

    fun DisplayState.getOutputValue(): Int {
        val wireToSegmentMappings = buildMap {
            val one = inputs.single { it.size == 2 }

            val a = inputs.filter { it.size !in setOf(2, 4) }.intersectAll().single()
            this[a] = 'a'

            val g = (inputs.filter { it.size !in 2..4 }.intersectAll() - a).single()
            this[g] = 'g'

            val d = (inputs.filter { it.size !in setOf(2, 3, 6) }.intersectAll() - a - g).single()
            this[d] = 'd'

            val bAndF = (inputs.filter { it.size !in setOf(2, 3, 5) }.intersectAll() - a - d - g)
            val f = (one intersect bAndF).single()
            this[f] = 'f'

            val b = (bAndF - f).single()
            this[b] = 'b'

            val c = (one - f).single()
            this[c] = 'c'

            val e = (allSegments - a - b - c - d - f - g).single()
            this[e] = 'e'
        }

        return outputs
            .map { it.map(wireToSegmentMappings::getValue).toSet() }
            .map(segmentToNumberMapping::getValue)
            .joinToString(separator = "").toInt()
    }

    return input.sumOf { it.getOutputValue() }
}

private fun String.parse(): List<DisplayState> {
    fun String.parsePart(): List<Set<Char>> {
        return split(" ").map { it.toCharArray().toSet() }
    }

    fun String.parseLine(): DisplayState {
        val (inputs, outputs) = split(" | ")
        return DisplayState(inputs.parsePart(), outputs.parsePart())
    }

    return lineSequence()
        .map { it.parseLine() }
        .toList()
}

private data class DisplayState(
    val inputs: List<Set<Char>>,
    val outputs: List<Set<Char>>,
)
