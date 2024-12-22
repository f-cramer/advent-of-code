package de.cramer.adventofcode.year2024.day12

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.isValidIndex
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vectors
import java.util.SortedSet

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 1930)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1206)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Char>>): Int {
    return input.buildRegions().sumOf { region ->
        val perimeter = region.sumOf { it.neighbors.count { n -> n !in region } }
        region.size * perimeter
    }
}

private fun problem02(input: List<List<Char>>): Int {
    val horizontalComparator = compareBy<Vector> { it.y }.thenBy { it.x }
    val verticalComparator = compareBy<Vector> { it.x }.thenBy { it.y }
    fun SortedSet<Vector>.countSides(mainSide: (Vector) -> Int, secondarySide: (Vector) -> Int): Int = fold(Vector(Int.MIN_VALUE, Int.MIN_VALUE) to 0) { (last, sides), v ->
        v to if (mainSide(last) + 1 == mainSide(v) && secondarySide(last) == secondarySide(v)) sides else sides + 1
    }.second
    fun SortedSet<Vector>.countHorizontalSides() = countSides({ it.x }, { it.y })
    fun SortedSet<Vector>.countVerticalSides() = countSides({ it.y }, { it.x })

    return input.buildRegions().sumOf { region ->
        val northSides = sortedSetOf(horizontalComparator)
        val eastSides = sortedSetOf(verticalComparator)
        val southSides = sortedSetOf(horizontalComparator)
        val westSides = sortedSetOf(verticalComparator)

        region.forEach { v ->
            if (v.north() !in region) {
                northSides += v
            }
            if (v.east() !in region) {
                eastSides += v
            }
            if (v.south() !in region) {
                southSides += v
            }
            if (v.west() !in region) {
                westSides += v
            }
        }

        val northSidesCount = northSides.countHorizontalSides()
        val eastSidesCount = eastSides.countVerticalSides()
        val southSidesCount = southSides.countHorizontalSides()
        val westSidesCount = westSides.countVerticalSides()

        val sides = northSidesCount + eastSidesCount + southSidesCount + westSidesCount
        region.size * sides
    }
}

private fun List<List<Char>>.buildRegions(): Set<Set<Vector>> {
    val vectors = vectors().toMutableSet()
    return buildSet {
        while (vectors.isNotEmpty()) {
            val start = vectors.iterator().next()
            vectors -= start

            val identifier = this@buildRegions[start]
            val region = buildSet {
                val stack = ArrayDeque<Vector>()
                stack += start
                while (stack.isNotEmpty()) {
                    val current = stack.removeFirst()
                    if (current in this) {
                        continue
                    }

                    this += current
                    vectors -= current
                    stack += current.neighbors
                        .filter { isValidIndex(it) }
                        .filter { this@buildRegions[it] == identifier }
                        .filterNot { it in this }
                }
            }
            this += region
        }
    }
}

private fun String.parse(): List<List<Char>> {
    return lines()
        .map { it.toCharArray().toList() }
}

private val Vector.neighbors: Sequence<Vector>
    get() = sequenceOf(north(), east(), south(), west())
