package de.cramer.adventofcode.year2024.day24

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 2024)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Long {
    val values = input.initialValues.toMutableMap()
    val queue = ArrayDeque<String>().apply {
        this += input.instructions.keys.filter { it.startsWith('z') }
        this += input.initialValues.keys.filter { it.startsWith('z') }
    }

    while (queue.isNotEmpty()) {
        val wire = queue.removeFirst()
        if (wire in values) {
            continue
        }

        val instruction = input.instructions[wire] ?: continue
        val v1 = values[instruction.leftInput]
        if (v1 == null && instruction.leftInput !in queue) {
            queue += instruction.leftInput
        }

        val v2 = values[instruction.rightInput]
        if (v2 == null && instruction.rightInput !in queue) {
            queue += instruction.rightInput
        }

        if (v1 != null && v2 != null) {
            values[wire] = instruction.gate(v1, v2)
        } else {
            if (v1 != null) {
                instruction.gate(v1)?.let { values[wire] = it }
            } else if (v2 != null) {
                instruction.gate(v2)?.let { values[wire] = it }
            }

            if (wire !in values) {
                if (v1 == null && instruction.leftInput !in queue) {
                    queue += instruction.leftInput
                }
                if (v2 == null && instruction.rightInput !in queue) {
                    queue += instruction.rightInput
                }
                queue += wire
            }
        }
    }

    return values.asSequence()
        .filter { (k, _) -> k.startsWith('z') }
        .sortedByDescending { (k, _) -> k }
        .map { (_, v) -> if (v) 1 else 0 }
        .joinToString("") { it.toString() }
        .toLong(2)
}

private fun problem02(input: Input, print: Boolean = false): String {
    val swaps = sortedSetOf<String>()
    val instructions = input.instructions.toMutableMap().apply {
        fun swap(i1: String, i2: String) {
            val h = getValue(i1)
            this[i1] = getValue(i2)
            this[i2] = h

            swaps += i1
            swaps += i2
        }

        swap("qjj", "gjc")
        swap("wmp", "z17")
        swap("z26", "gvm")
        swap("z39", "qsb")
    }

    val values = input.initialValues.toMutableMap()
    val realResult = 49521014360658
    val realResultBits = realResult.toString(2).reversed()

    fun out(output: String, indent: String = ""): Pair<String?, Boolean> {
        var result = values[output]
        if (output in values) {
            return null to result!!
        }

        val realValue = if (output.startsWith('z')) {
            realResultBits[output.substring(1).toInt()] == '1'
        } else {
            null
        }

        val instruction = instructions[output]

        var leftValue: Boolean? = instruction?.let { values[it.leftInput] }
        var leftOutput: String? = null
        var rightValue: Boolean? = instruction?.let { values[it.rightInput] }
        var rightOutput: String? = null

        if (instruction != null) {
            val newIndent = "$indent  "
            if (!instruction.leftInput.startsWith('z')) {
                val (out, value) = out(instruction.leftInput, newIndent)
                leftValue = value
                leftOutput = out
            }
            if (!instruction.rightInput.startsWith('z')) {
                val (out, value) = out(instruction.rightInput, newIndent)
                rightValue = value
                rightOutput = out
            }
        }

        val calculation = if (!print) {
            null
        } else if (instruction != null) {
            val gateString = when (instruction.gate) {
                Gate.AND -> "&&"
                Gate.OR -> "||"
                Gate.XOR -> "^"
            }

            val suffix = (
                if (leftValue != null && rightValue != null) {
                    result = instruction.gate(leftValue, rightValue)
                    "$leftValue $gateString $rightValue = $result"
                } else if (leftValue != null || rightValue != null) {
                    val value = leftValue ?: rightValue!!
                    instruction.gate(value)?.let {
                        result = it
                        val leftValueString = leftValue?.toString() ?: "unknown"
                        val rightValueString = rightValue?.toString() ?: "unknown"
                        "$leftValueString $gateString $rightValueString = $it"
                    }
                } else {
                    null
                }
                )?.let {
                val wrong = if (realValue == null || result == realValue) "" else " <---- wrong"
                " ($it)$wrong"
            }.orEmpty()

            "${instruction.leftInput} $gateString ${instruction.rightInput}$suffix"
        } else {
            ""
        }

        if (instruction != null && result == null) {
            if (leftValue != null && rightValue != null) {
                result = instruction.gate(leftValue, rightValue)
            } else if (leftValue != null || rightValue != null) {
                val value = leftValue ?: rightValue!!
                result = instruction.gate(value)
            }
        }

        if (result != null) {
            values[output] = result!!
        }

        val string = listOfNotNull(
            if (print) "$indent$output = $calculation" else null,
            leftOutput,
            rightOutput,
        ).joinToString("\n")

        return string to result!!
    }

    val result = instructions.asSequence()
        .filter { (k) -> k.startsWith("z") }
        .sortedBy { (k) -> k }
        .map { (k, _) -> k to out(k) }
        .onEach { (_, p) -> if (print) println(p.first) }
        .sortedByDescending { (k) -> k }
        .map { (_, p) -> p.second }
        .map { if (it) 1 else 0 }
        .joinToString("") { it.toString() }
        .toLong(2)

    require(result == realResult) { "expected $realResult, got $result" }
    return swaps.joinToString(",")
}

private fun String.parse(): Input {
    val (initialValuesString, gatesString) = splitByEmptyLines()

    val initialValues = initialValuesString.lines().associate {
        val (n, v) = it.split(": ")
        n to (v == "1")
    }

    fun String.parseInstruction(): Pair<String, Instruction> {
        val (in1, gateString, in2, _, out) = split(" ")
        val gate = Gate.valueOf(gateString)
        return out to Instruction(gate, in1, in2)
    }

    val gates = gatesString.lines()
        .associate { it.parseInstruction() }

    return Input(initialValues, gates)
}

private data class Input(
    val initialValues: Map<String, Boolean>,
    val instructions: Map<String, Instruction>,
)

private data class Instruction(
    val gate: Gate,
    val leftInput: String,
    val rightInput: String,
)

private enum class Gate {
    AND,
    OR,
    XOR,
    ;

    operator fun invoke(v1: Boolean): Boolean? = when (this) {
        AND -> if (v1) null else false
        OR -> if (v1) true else null
        XOR -> null
    }

    operator fun invoke(v1: Boolean, v2: Boolean): Boolean = when (this) {
        AND -> v1 && v2
        OR -> v1 || v2
        XOR -> v1 xor v2
    }
}
