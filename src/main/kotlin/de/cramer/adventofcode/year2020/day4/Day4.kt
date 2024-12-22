package de.cramer.adventofcode.year2020.day4

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 2)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Map<String, String>>): Int {
    return input.count {
        validationData.keys.all { f -> f in it.keys }
    }
}

private fun problem02(input: List<Map<String, String>>): Int {
    return input.count {
        validationData.all { (field, validator) ->
            val value = it[field]
            value != null && validator(value)
        }
    }
}

private fun String.parse(): List<Map<String, String>> {
    fun String.toPassword() = lineSequence()
        .flatMap { it.split(" ") }
        .map { it.split(":") }
        .map { (a, b) -> a to b }
        .toMap()

    return splitByEmptyLines()
        .map { it.toPassword() }
}

val eclData = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
val validationData = mapOf<String, (String) -> Boolean>(
    "byr" to {
        it.toIntOrNull() in 1920..2002
    },
    "iyr" to {
        it.toIntOrNull() in 2010..2020
    },
    "eyr" to {
        it.toIntOrNull() in 2020..2030
    },
    "hgt" to {
        if (it.endsWith("cm")) {
            val number = it.substringBefore("cm").toIntOrNull()
            number in 150..193
        } else if (it.endsWith("in")) {
            val number = it.substringBefore("in").toIntOrNull()
            number in 59..76
        } else {
            false
        }
    },
    "hcl" to {
        it.startsWith("#") && it.substring(1).matches("""[\da-f]{6}""".toRegex())
    },
    "ecl" to {
        it in eclData
    },
    "pid" to {
        it.length == 9 && it.matches("""\d+""".toRegex())
    },
)
