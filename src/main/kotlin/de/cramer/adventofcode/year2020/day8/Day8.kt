package de.cramer.adventofcode.year2020.day8

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 5)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 8)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Instruction>): Int {
    return CPU().apply { run(input) }.acc
}

private fun problem02(input: List<Instruction>): Int {
    return input.asSequence()
        .mapIndexedNotNull { index, instruction ->
            val copy = input.toMutableList()
            copy[index] = when (instruction) {
                is AccInstruction -> return@mapIndexedNotNull null
                is JmpInstruction -> NopInstruction(instruction.offset)
                is NopInstruction -> JmpInstruction(instruction.value)
            }
            copy
        }
        .firstNotNullOf {
            val cpu = CPU()
            if (cpu.run(it) == State.DONE) cpu.acc else null
        }
}

private fun CPU.run(instructions: List<Instruction>): State {
    val cpu = this
    buildSet {
        while (true) {
            if (cpu.pc in this) {
                return State.LOOP
            } else if (cpu.pc !in instructions.indices) {
                return State.DONE
            }

            this += cpu.pc
            instructions[cpu.pc](cpu)
            cpu.pc++
        }
    }
}

private fun String.parse(): List<Instruction> {
    fun String.toInstruction(): Instruction = when {
        startsWith("acc") -> AccInstruction(substringAfter("acc ").toInt())
        startsWith("jmp") -> JmpInstruction(substringAfter("jmp ").toInt())
        startsWith("nop") -> NopInstruction(substringAfter("nop ").toInt())
        else -> error("cannot parse $this to instruction")
    }

    return lineSequence()
        .map { it.toInstruction() }
        .toList()
}

private data class CPU(
    var pc: Int = 0,
    var acc: Int = 0,
)

private sealed interface Instruction {
    operator fun invoke(cpu: CPU)
}

private data class AccInstruction(
    val value: Int,
) : Instruction {
    override fun invoke(cpu: CPU) {
        cpu.acc += value
    }
}

private data class JmpInstruction(
    val offset: Int,
) : Instruction {
    override fun invoke(cpu: CPU) {
        cpu.pc += offset - 1
    }
}

private data class NopInstruction(
    val value: Int,
) : Instruction {
    override fun invoke(cpu: CPU) {
    }
}

private enum class State {
    LOOP,
    DONE,
}
