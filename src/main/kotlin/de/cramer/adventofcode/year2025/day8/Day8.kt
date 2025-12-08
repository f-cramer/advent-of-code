package de.cramer.adventofcode.year2025.day8

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vector.Vector3
import kotlin.math.sqrt

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput, 10)
    checkTestResult(testResult01, 40)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 25272)

    val input = readInput().parse()
    runProblem01 { problem01(input, 1000) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Vector3>, numberOfConnections: Int): Int {
    val connectionsToMake = getSortedConnections(input)
        .take(numberOfConnections)
        .toList()

    val circuitSizes = UnionFind(input).let { uf ->
        connectionsToMake.forEach { (a, b) -> uf.connect(a, b) }
        input.asSequence()
            .map { uf.getCircuit(it) }
            .groupingBy { it }
            .eachCount()
            .values
    }

    return circuitSizes.asSequence()
        .sortedDescending()
        .take(3)
        .reduce { acc, i -> acc * i }
}

private fun problem02(input: List<Vector3>): Long {
    val connectionsToMake = getSortedConnections(input).toList()

    val numberOfNodes = input.size
    val unionFind = UnionFind(input)
    for ((node1, node2) in connectionsToMake) {
        unionFind.connect(node1, node2)
        val circuitSizes = input
            .map { unionFind.getCircuit(it) }
            .groupingBy { it }
            .eachCount()
            .values
        if (circuitSizes.any { it == numberOfNodes }) {
            return node1.x.toLong() * node2.x.toLong()
        }
    }

    error("no solution found")
}

private fun String.parse(): List<Vector3> {
    return lines()
        .map { line ->
            val (x, y, z) = line.split(",")
            Vector3(x.toInt(), y.toInt(), z.toInt())
        }
}

private fun getSortedConnections(input: List<Vector3>) = input.asSequence()
    .flatMapIndexed { i1, n1 -> input.mapIndexedNotNull { i2, n2 -> if (i1 > i2) n1 to n2 else null } }
    .associateWith { (a, b) -> a.distanceTo(b) }
    .asSequence()
    .sortedBy { (_, distance) -> distance }
    .map { (k) -> k }

private fun Vector3.distanceTo(other: Vector3): Double =
    sqrt(((x - other.x).sqr() + (y - other.y).sqr() + (z - other.z).sqr()).toDouble())

private fun Int.sqr(): Long = toLong() * toLong()

private class UnionFind<T>(nodes: List<T>) {
    private val circuits = nodes.associateWithTo(mutableMapOf()) { it }

    fun getCircuit(node: T): T {
        val circuit = circuits[node]
        requireNotNull(circuit) { "node $node not found" }
        if (circuit != node) {
            circuits[node] = getCircuit(circuit)
        }
        return circuits[node]!!
    }

    fun connect(node1: T, node2: T) {
        val circuit1 = getCircuit(node1)
        val circuit2 = getCircuit(node2)
        if (circuit1 != circuit2) {
            circuits[circuit1] = circuit2
        }
    }
}
