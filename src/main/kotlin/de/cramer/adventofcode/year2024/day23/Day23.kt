package de.cramer.adventofcode.year2024.day23

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 7)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, "co,de,ka,ta")

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Connection>): Int {
    val connectionsByPc = input.getConnectionsByPc()

    val visited = mutableSetOf<String>()
    return connectionsByPc.asSequence()
        .flatMap { (pc1, c) ->
            val innerVisited = mutableSetOf<String>()
            visited += pc1
            innerVisited += pc1

            c.asSequence().flatMap inner@{ pc2 ->
                if (pc2 in visited) return@inner emptySequence()
                innerVisited += pc2

                c.asSequence().mapNotNull { pc3 ->
                    if (pc3 in visited || pc3 in innerVisited) return@mapNotNull null
                    if (pc3 !in connectionsByPc.getValue(pc2)) return@mapNotNull null
                    if (pc2 !in connectionsByPc.getValue(pc3)) return@mapNotNull null

                    setOf(pc1, pc2, pc3)
                }
            }
        }
        .count { it.any { pc -> pc.startsWith('t') } }
}

private fun problem02(input: List<Connection>): String {
    val connectionsByPc = input.getConnectionsByPc()
    val lanParties = mutableListOf<MutableSet<String>>()

    connectionsByPc.asSequence()
        .forEach { (pc1, c1) ->
            c1.forEach { pc2 ->
                val c2 = connectionsByPc.getValue(pc2)
                val relevantLanParties = lanParties.filter { party ->
                    if (pc1 in party && pc2 in party) return@filter true
                    party.all { (it == pc1 || it in c1) && (it == pc2 || it in c2) }
                }

                if (relevantLanParties.isEmpty()) {
                    lanParties += mutableSetOf(pc1, pc2)
                } else {
                    relevantLanParties.forEach {
                        it += pc1
                        it += pc2
                    }
                }
            }
        }

    return lanParties.maxBy { it.size }
        .sorted()
        .joinToString(",")
}

private fun List<Connection>.getConnectionsByPc(): Map<String, Set<String>> = asSequence()
    .flatMap { sequenceOf(it.a to it.b, it.b to it.a) }
    .distinct()
    .groupingBy { (k) -> k }
    .aggregate { _, acc: MutableSet<String>?, (_, v), _ ->
        if (acc == null) {
            mutableSetOf(v)
        } else {
            acc += v
            acc
        }
    }

private fun String.parse(): List<Connection> {
    return lines()
        .map {
            val (a, b) = it.split("-")
            Connection(a, b)
        }
}

private data class Connection(val a: String, val b: String)
