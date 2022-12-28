package de.cramer.adventofcode.year2022.day5

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines
import java.util.Deque
import java.util.LinkedList

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, "CMZ")
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, "MCD")

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Pair<List<Deque<Char>>, List<Command>>): String {
    val (s, commands) = input
    val stacks = s.deepCopy()
    commands.forEach { (count, from, to) ->
        val fromStack = stacks[from]
        val toStack = stacks[to]
        repeat(count) {
            val char = fromStack.pop()!!
            toStack.push(char)
        }
    }

    return stacks.toResult()
}

private fun problem02(input: Pair<List<Deque<Char>>, List<Command>>): String {
    val (s, commands) = input
    val stacks = s.deepCopy()
    commands.forEach { (count, from, to) ->
        val fromStack = stacks[from]
        val chars = buildList {
            repeat(count) {
                add(0, fromStack.pop()!!)
            }
        }

        val toStack = stacks[to]
        chars.forEach { toStack.push(it) }
    }

    return stacks.toResult()
}

private fun String.parse(): Pair<List<Deque<Char>>, List<Command>> {
    fun String.toInput(): List<Deque<Char>> {
        val lines = lines()
        val numberOfCrates = lines.last().trim().split(" +".toRegex()).last().toInt()

        val stacks = List(numberOfCrates) { LinkedList<Char>() }

        lines.subList(0, lines.size - 1).asReversed().forEach {
            it.windowedSequence(3, 4, true)
                .map { s -> s[1] }
                .forEachIndexed { index, c ->
                    if (c != ' ') {
                        stacks[index].push(c)
                    }
                }
        }

        return stacks
    }

    fun String.toCommands(): List<Command> = lines().map { it.toCommand() }

    val (input, commands) = splitByEmptyLines()
    return input.toInput() to commands.toCommands()
}

private fun List<Deque<Char>>.toResult(): String = map { it.peek()!! }
    .joinToString(separator = "") { it.toString() }

private fun List<Deque<Char>>.deepCopy(): List<Deque<Char>> = map {
    LinkedList(it)
}

private val commandRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

private fun String.toCommand(): Command {
    val (_, count, fromIndex, toIndex) = commandRegex.find(this)?.groupValues
        ?: error("\"$this\" does not match $commandRegex")
    return Command(
        count.toInt(),
        fromIndex.toInt() - 1,
        toIndex.toInt() - 1,
    )
}

private data class Command(
    val count: Int,
    val fromIndex: Int,
    val toIndex: Int,
)
