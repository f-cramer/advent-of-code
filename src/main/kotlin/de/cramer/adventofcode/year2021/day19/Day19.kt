package de.cramer.adventofcode.year2021.day19

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines
import de.cramer.adventofcode.utils.vector.Vector3
import kotlin.math.abs
import kotlin.math.sqrt

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 79)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 3621)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Scanner>): Int {
    val scanners = input.layout()
    return scanners.asSequence().flatMap { it.beacons }.distinct().count()
}

private fun problem02(input: List<Scanner>): Int {
    val scanners = input.layout()
    return scanners.asSequence()
        .flatMap { s -> scanners.asSequence().filterNot { it == s }.map { s to it } }
        .maxOf { (a, b) -> a.position.manhattanDistanceTo(b.position) }
}

private fun List<Scanner>.layout(): List<Scanner> {
    val scanners = map {
        it.copy(beacons = it.beacons.toList())
    }

    scanners[0].hasShifted = true
    val shiftedScanners = scanners.take(1).toMutableList()
    val unshiftedScanners = scanners.drop(1).toMutableList()

    while (unshiftedScanners.isNotEmpty()) {
        unshiftedScanners.forEach shifted@{ s ->
            shiftedScanners.forEach { s2 ->
                if (s != s2) {
                    s.findOverlapsWith(s2)
                    if (s.hasShifted) return@shifted
                }
            }
        }

        val newShifts = unshiftedScanners.filter { it.hasShifted }.toSet()
        shiftedScanners += newShifts
        unshiftedScanners -= newShifts
    }

    return scanners
}

private fun String.parse(): List<Scanner> {
    fun String.toVector(): Vector3 {
        val (x, y, z) = split(",")
        return Vector3(x.toInt(), y.toInt(), z.toInt())
    }

    fun String.toScanner(): Scanner {
        val lines = lines()
        val index = lines.first().substringAfter("--- scanner ").substringBefore(" ---").toInt()
        val beacons = lines.subList(1, lines.size)
            .map { it.toVector() }
        return Scanner(index, beacons)
    }

    return splitByEmptyLines()
        .map { it.toScanner() }
}

private data class Scanner(
    val index: Int,
    var beacons: List<Vector3>,
    var position: Vector3 = Vector3.ZERO,
    var orientation: Vector3 = Vector3.ZERO,
    var hasShifted: Boolean = false,
) {
    private val beaconDistances: Map<Pair<Int, Int>, Double> = buildMap {
        beacons.forEachIndexed { i, c1 ->
            beacons.forEachIndexed { j, c2 ->
                val k = listOf(i, j).sorted().let { (a, b) -> a to b }
                if (i != j && k !in this) this[k] = c1.distanceTo(c2)
            }
        }
    }

    fun findOverlapsWith(other: Scanner) {
        val allMatches = beaconDistances.filterValues { it in other.beaconDistances.values }
        val uniqueMatches = allMatches.keys.asSequence().flatMap { it.toList() }.distinct().count()

        if (uniqueMatches >= 12) {
            val otherMatches = other.beaconDistances.filterValues { it in allMatches.values }
            orientScanner(
                allMatches.entries.sortedBy { it.value },
                otherMatches.entries.sortedBy { it.value },
                other,
            )
        }
    }

    private fun orientScanner(
        matches: List<Map.Entry<Pair<Int, Int>, Double>>,
        otherMatches: List<Map.Entry<Pair<Int, Int>, Double>>,
        other: Scanner,
    ) {
        val firstMatch = matches[0]
        val secondMatch = matches.first {
            it != firstMatch && firstMatch.key.second in it.key.toList()
        }
        val firstKeys = listOf(firstMatch.key, secondMatch.key)

        val otherFirstKeys = listOf(otherMatches[0], otherMatches.first { it.value == secondMatch.value }).map { it.key }

        val (matchingIndex, matchingVector) = findMatchingVectors(firstKeys, beacons)
        val mySecondVector = beacons[firstKeys[0].toList().first { it != matchingIndex }]

        val (otherMatchingIndex, otherMatchingVector) = findMatchingVectors(otherFirstKeys, other.beacons)
        val otherSecondCoord = other.beacons[otherFirstKeys[0].toList().first { it != otherMatchingIndex }]

        var myDupe = matchingVector
        var mySecondDupe = mySecondVector
        position = otherMatchingVector - myDupe

        while (position + mySecondDupe != otherSecondCoord && orientation.x < 4) {
            val (result1, result2) = listOf(myDupe, mySecondDupe).orientToNext()
            myDupe = result1
            mySecondDupe = result2
            position = otherMatchingVector - myDupe
        }

        if (orientation.x < 4) {
            beacons = beacons.map { it.reorient() + position }
            hasShifted = true
        } else {
            position = Vector3(0, 0, 0)
            orientation = Vector3(0, 0, 0)
        }
    }

    private fun List<Vector3>.orientToNext(): List<Vector3> {
        var nextRot = orientation.copy()
        orientation = updateScannerOrientation()
        nextRot = orientation - nextRot

        if (nextRot.x == -3) nextRot = nextRot.copy(x = 1)
        if (nextRot.y == -3) nextRot = nextRot.copy(y = 1)
        if (nextRot.z == -3) nextRot = nextRot.copy(z = 1)

        return map { it.rotate(nextRot) }
    }

    private fun updateScannerOrientation(): Vector3 {
        var z = orientation.z + 1
        var y = orientation.y
        var x = orientation.x

        if (z > 3) {
            y += 1
            z = 0
        }

        if (y > 3) {
            x += 1
            y = 0
        }

        return Vector3(x, y, z)
    }

    private fun Vector3.reorient(): Vector3 {
        var vector = this
        repeat(orientation.x) {
            vector = vector.rotate(Vector3(1, 0, 0))
        }

        repeat(orientation.y) {
            vector = vector.rotate(Vector3(0, 1, 0))
        }

        repeat(orientation.z) {
            vector = vector.rotate(Vector3(0, 0, 1))
        }

        return vector
    }

    private fun Vector3.rotate(rotation: Vector3): Vector3 {
        var x = x
        var y = y
        var z = z

        if (rotation.z == 1) {
            val temp = y
            y = x
            x = temp
            y *= -1
        }

        if (rotation.y == 1) {
            val temp = x
            x = z
            z = temp
            x *= -1
        }

        if (rotation.x == 1) {
            val temp = z
            z = y
            y = temp
            z *= -1
        }

        return Vector3(x, y, z)
    }

    private fun findMatchingVectors(keys: List<Pair<Int, Int>>, beacons: List<Vector3>): Pair<Int, Vector3> {
        val indices = keys.flatMap { it.toList() }
        val vectors = indices.map { beacons[it] }

        val index = indices.groupingBy { it }.eachCount().filter { it.value > 1 }.keys.first()
        val vector = vectors.groupingBy { it }.eachCount().filter { it.value > 1 }.keys.first()
        return index to vector
    }
}

private fun Vector3.distanceTo(other: Vector3): Double =
    sqrt(((x - other.x).sqr() + (y - other.y).sqr() + (z - other.z).sqr()).toDouble())

private fun Int.sqr() = this * this

private fun Vector3.manhattanDistanceTo(other: Vector3) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
