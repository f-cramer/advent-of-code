package de.cramer.adventofcode.year2020.day19

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 3)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 12)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    val matchesForRule0 = input.rules.toMatches().getValue(0)
    return input.messages.count { it in matchesForRule0 }
}

private fun problem02(input: Input): Int {
    val modifiedRules = input.rules.toMutableMap()
    modifiedRules -= listOf(0, 8, 11)
    val modifiedMatches = modifiedRules.toMatches().toMutableMap()

    val matchesForRule31 = modifiedMatches.getValue(31)
    val matchesForRule42 = modifiedMatches.getValue(42)

    fun String.findMatch(matches: Set<String>): String? = matches.firstOrNull { endsWith(it) }

    return input.messages.count {
        val match = it.findMatch(matchesForRule31) ?: return@count false
        var remaining = it.substring(0, it.length - match.length)

        var countMatches31 = 1
        var match31 = remaining.findMatch(matchesForRule31)
        while (match31 != null) {
            remaining = remaining.substring(0, remaining.length - match31.length)
            match31 = remaining.findMatch(matchesForRule31)
            countMatches31++
        }

        repeat(countMatches31) {
            val match42 = remaining.findMatch(matchesForRule42) ?: return@count false
            remaining = remaining.substring(0, remaining.length - match42.length)
        }

        var match42: String? = remaining.findMatch(matchesForRule42) ?: return@count false
        while (match42 != null) {
            remaining = remaining.substring(0, remaining.length - match42.length)
            match42 = remaining.findMatch(matchesForRule42)
        }

        remaining.isEmpty()
    }
}

private fun String.parse(): Input {
    fun String.toRule(): Pair<Int, Matcher> {
        val rawId = takeWhile { it.isDigit() }
        val id = rawId.toInt()

        val rest = substring(rawId.length + 1).trim()
        return id to if (rest.startsWith("\"")) {
            SimpleMatcher(rest.substring(1, rest.length - 1))
        } else {
            ComplexMatcher(
                rest.splitToSequence("|")
                    .map { it.trim() }
                    .map { m -> m.split(" ").map { it.toInt() } }
                    .toSet(),
            )
        }
    }

    val (rules, messages) = splitByEmptyLines()
    return Input(rules.lines().associate { it.toRule() }, messages.lines())
}

private fun Map<Int, Matcher>.toMatches(): Map<Int, Set<String>> {
    val rules = toMutableMap()

    return buildMap {
        rules.filter { (_, matcher) -> matcher is SimpleMatcher }.forEach { (id, matcher) ->
            rules -= id
            this[id] = setOf((matcher as SimpleMatcher).match)
        }

        while (rules.isNotEmpty()) {
            val reducibleRule = rules.entries.first { (_, m) ->
                val matcher = m as ComplexMatcher
                matcher.subRules.flatten().all { it in this }
            }
            rules -= reducibleRule.key

            val matcher = reducibleRule.value as ComplexMatcher
            val matches = matcher.subRules
                .flatMap { r ->
                    var strings = listOf("")
                    r.forEach { id ->
                        val newSuffixes = getValue(id)
                        strings = strings.flatMap { old -> newSuffixes.map { old + it } }
                    }
                    strings.toSet()
                }.toSet()
            this[reducibleRule.key] = matches
        }
    }
}

private data class Input(
    val rules: Map<Int, Matcher>,
    val messages: List<String>,
)

private sealed interface Matcher

private data class SimpleMatcher(
    val match: String,
) : Matcher

private data class ComplexMatcher(
    val subRules: Set<List<Int>>,
) : Matcher
