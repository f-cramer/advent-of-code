package de.cramer.adventofcode.year2022.day16

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runTest
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testDistances = testInput.values.getDistances(testInput::getValue)
    val testDistanceCalculator: (Valve, Valve) -> Int = { from, to -> testDistances.getValue(from).getValue(to) }

    val testResult01 = problem01(testInput, testDistanceCalculator)
    checkTestResult(testResult01, 1651)
    val testResult02 = problem02(testInput, testDistanceCalculator)
    checkTestResult(testResult02, 1707)

    val input = readInput().parse()
    val distances = input.values.getDistances(input::getValue)
    val distanceCalculator: (Valve, Valve) -> Int = { from, to -> distances.getValue(from).getValue(to) }

    runTest("result01") { problem01(input, distanceCalculator) }
    runTest("result02") { problem02(input, distanceCalculator) }
}

private fun problem01(input: Map<String, Valve>, distanceCalculator: (Valve, Valve) -> Int): Int {
    val workingValves = input.values.filter { it.flowRate > 0 }.toSet()

    val minutes = 30
    val start = input["AA"] ?: error("could not find starting valve with name AA")
    val openOptions = start.findAllOpenOrders(minutes, emptySet(), workingValves, distanceCalculator)
    return openOptions.maxOf {
        start.getReleasedPressure(it, minutes, distanceCalculator)
    }
}

private fun problem02(input: Map<String, Valve>, distanceCalculator: (Valve, Valve) -> Int): Int {
    val workingValves = input.values.filter { it.flowRate > 0 }.toSet()

    val minutes = 26
    val start = input["AA"] ?: error("could not find starting valve with name AA")
    val possibleOpenOrders = start.findAllOpenOrders(minutes, emptySet(), workingValves, distanceCalculator)

    val bestScores = buildMap<List<Valve>, Int> {
        possibleOpenOrders.forEach {
            val key = it.sortedBy { v -> v.name }
            val value = start.getReleasedPressure(it, minutes, distanceCalculator)
            merge(key, value, ::max)
        }
    }.entries.toList()

    var best = 0
    for (human in bestScores.indices) {
        for (elephant in human + 1..<bestScores.size) {
            val (humanValves, humanReleasedPressure) = bestScores[human]
            val (elephantValves, elephantReleasedPressure) = bestScores[elephant]
            val totalReleasedPressure = humanReleasedPressure + elephantReleasedPressure

            if (totalReleasedPressure > best) {
                if (humanValves.none { it in elephantValves }) {
                    best = totalReleasedPressure
                }
            }
        }
    }
    return best
}

private fun String.parse(): Map<String, Valve> {
    fun String.toValve(): Valve {
        val r1 = substringAfter("Valve ")
        val (name, r2) = r1.split(" has flow rate=")
        val (flowRate, tunnels) = r2.split("; tunnels lead to valves ", "; tunnel leads to valve ")
        return Valve(name, flowRate.toInt(), tunnels.split(", "))
    }
    return lineSequence()
        .map { it.toValve() }
        .associateBy { it.name }
}

private fun Collection<Valve>.getDistances(resolver: (String) -> Valve): Map<Valve, Map<Valve, Int>> {
    val distances = associateWith { associateWith { 10000 }.toMutableMap() }
    for (valve in this) {
        val valveDistances = distances.getValue(valve)
        valveDistances[valve] = 0
        valve.tunnels
            .map { resolver(it) }
            .forEach { valveDistances[it] = 1 }
    }
    for (using in this) {
        val usingDistances = distances.getValue(using)
        for (from in this) {
            val fromDistances = distances.getValue(from)
            for (to in this) {
                fromDistances.merge(to, fromDistances.getValue(using) + usingDistances.getValue(to), ::min)
            }
        }
    }
    return distances
}

private fun Valve.findAllOpenOrders(remainingMinutes: Int, openValves: Set<Valve>, availableValves: Collection<Valve>, distanceCalculator: (Valve, Valve) -> Int): Set<List<Valve>> {
    return buildSet {
        this += openValves.toList()

        if (remainingMinutes > 0) {
            for (nextValve in availableValves) {
                if (nextValve !in openValves) {
                    val distance = distanceCalculator(this@findAllOpenOrders, nextValve)
                    if (distance <= remainingMinutes) {
                        this += nextValve.findAllOpenOrders(remainingMinutes - distance - 1, openValves + nextValve, availableValves, distanceCalculator)
                    }
                }
            }
        }
    }
}

private fun Valve.getReleasedPressure(nextValves: List<Valve>, remainingMinutes: Int, distanceCalculator: (Valve, Valve) -> Int): Int {
    var releasedPressure = 0
    var remainingTime = remainingMinutes
    var previous = this
    for (valve in nextValves) {
        remainingTime -= distanceCalculator(previous, valve) + 1
        releasedPressure += remainingTime * valve.flowRate
        previous = valve
    }
    return releasedPressure
}

private data class Valve(
    val name: String,
    val flowRate: Int,
    val tunnels: List<String>,
)
