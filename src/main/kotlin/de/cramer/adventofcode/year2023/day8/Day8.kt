package de.cramer.adventofcode.year2023.day8

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.math.lcm
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.readTestInput2
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testInput2 = readTestInput2().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 6)
    val testResult02 = problem02(testInput2)
    checkTestResult(testResult02, 6L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    return input.instructionsSequence().stepsUntilEnd(input.nodes, input.nodes.getValue("AAA")) { label == "ZZZ" }
}

private fun problem02(input: Input): Long {
    val startingNodes = input.nodes.values.filter { it.label.endsWith("A") }
    val numbers = startingNodes.map {
        input.instructionsSequence().stepsUntilEnd(input.nodes, it) { label.endsWith("Z") }.toLong()
    }
    return lcm(numbers)
}

private fun String.parse(): Input {
    val (instructionsString, nodesString) = splitByEmptyLines()
    val instructions = instructionsString.map { Instruction.valueOf(it.toString()) }
    val nodes = nodesString.lineSequence()
        .map {
            val (label, left, right) = it.split(" = (", ", ", ")")
            Node(label, left, right)
        }
        .associateBy { it.label }
    return Input(instructions, nodes)
}

private data class Input(
    val instructions: List<Instruction>,
    val nodes: Map<String, Node>,
)

private data class Node(
    val label: String,
    val left: String,
    val right: String,
)

private enum class Instruction {
    L,
    R,
}

private fun Input.instructionsSequence(seed: Int = 0) = generateSequence(seed % instructions.size) { (it + 1) % instructions.size }
    .map { instructions[it] }

private fun Sequence<Instruction>.stepsUntilEnd(nodes: Map<String, Node>, start: Node, isEnd: Node.() -> Boolean) =
    runningFold(start) { node, instruction ->
        val nextNode = node.getNext(instruction)
        nodes.getValue(nextNode)
    }
        .takeWhile { !it.isEnd() }
        .count()

private fun Node.getNext(instruction: Instruction): String {
    val nextNode = when (instruction) {
        Instruction.L -> left
        Instruction.R -> right
    }
    return nextNode
}
