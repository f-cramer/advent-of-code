package de.cramer.adventofcode.year2021.day22

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 474140L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2758514936282235L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Cuboid>): Long {
    val initializationCuboid = Cuboid(-50..50, -50..50, -50..50, true)
    return input.filter { it intersection initializationCuboid != null }.reboot()
}

private fun problem02(input: List<Cuboid>): Long {
    return input.reboot()
}

private fun List<Cuboid>.reboot(): Long {
    return fold(emptyList<Cuboid>()) { volumes, cuboid ->
        (volumes + volumes.mapNotNull { it intersection cuboid }).let { if (cuboid.on) it + cuboid else it }
    }.sumOf { it.getVolume() }
}

private fun String.parse(): List<Cuboid> {
    val line = """(on|off) x=([-\d]+)..([-\d]+),y=([-\d]+)..([-\d]+),z=([-\d]+)..([-\d]+)""".toRegex()

    fun String.toCuboid(): Cuboid {
        val (state, xStart, xEnd, yStart, yEnd, zStart, zEnd) = line.matchEntire(this)!!.destructured
        return Cuboid(xStart.toInt()..xEnd.toInt(), yStart.toInt()..yEnd.toInt(), zStart.toInt()..zEnd.toInt(), state == "on")
    }

    return lineSequence()
        .map { it.toCuboid() }
        .toList()
}

private data class Cuboid(
    val x: IntRange,
    val y: IntRange,
    val z: IntRange,
    val on: Boolean,
) {
    fun getVolume(): Long = x.size.toLong() * y.size.toLong() * z.size.toLong() * if (on) 1 else -1

    infix fun intersection(other: Cuboid): Cuboid? =
        if (x intersects other.x && y intersects other.y && z intersects other.z) {
            Cuboid(
                maxOf(x.first, other.x.first)..minOf(x.last, other.x.last),
                maxOf(y.first, other.y.first)..minOf(y.last, other.y.last),
                maxOf(z.first, other.z.first)..minOf(z.last, other.z.last),
                !on,
            )
        } else {
            null
        }

    private val IntRange.size
        get() = last - first + 1

    private infix fun IntRange.intersects(other: IntRange) = first <= other.last && last >= other.first
}
