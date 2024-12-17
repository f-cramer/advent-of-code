package de.cramer.adventofcode.year2024.day17

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.splitByEmptyLines
import java.util.PriorityQueue

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, "4,6,3,5,6,3,5,2,1,0")

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): String {
    val vm = input.vm.run(input.program)
    return vm.out.joinToString(separator = ",") { it.toString() }
}

private fun problem02(input: Input): Long {
    val program = input.program.mapIndexed { index, it ->
        // replace JNZ with NOP to break after one iteration
        if (it != Operation.JNZ.ordinal || index % 2 > 0) it else Operation.NOP.ordinal
    }

    data class State(val index: Int, val a: Long)

    val queue = PriorityQueue(compareByDescending(State::index))
    queue += State(-1, 0)

    while (queue.isNotEmpty()) {
        val state = queue.poll()!!
        if (state.index == program.lastIndex) {
            return state.a
        }

        val index = state.index + 1
        val start = if (index == 0) 1 else 0
        for (aMod in start..<8) {
            val potentialA = state.a * 8 + aMod
            var vm = input.vm.copy(a = potentialA)
            vm = vm.run(program)

            val programLength = input.program.size
            if (vm.out.single() == input.program[programLength - 1 - index]) {
                queue += State(index, potentialA)
            }
        }
    }

    error("not found")
}

private fun String.parse(): Input {
    val (registerString, programString) = splitByEmptyLines()
    val registerLines = registerString.lines()
    val (_, aString) = registerLines[0].split(": ")
    val (_, bString) = registerLines[1].split(": ")
    val (_, cString) = registerLines[2].split(": ")
    val program = programString.substring(programString.indexOf(": ") + 2)
        .split(",").map { it.toInt() }

    return Input(VM(aString.toLong(), bString.toLong(), cString.toLong()), program)
}

private data class Input(
    val vm: VM,
    val program: List<Int>,
)

private data class VM(
    val a: Long,
    val b: Long,
    val c: Long,
    val pc: Int = 0,
    val out: List<Int> = emptyList(),
)

private fun VM.run(program: List<Int>): VM {
    var vm = this
    val lastValidOperationIndex = program.size - 1
    while (vm.pc < lastValidOperationIndex) {
        val operation = Operation.entries[program[vm.pc]]
        val operand = Operand(program[vm.pc + 1])
        vm = operation.run(vm, operand)
    }

    return vm
}

private enum class Operation {
    ADV,
    BXL,
    BST,
    JNZ,
    BXC,
    OUT,
    BDV,
    CDV,
    NOP,
}

private fun Operation.run(vm: VM, operand: Operand): VM = when (this) {
    Operation.ADV -> vm.copy(a = div(vm.a, operand.combo[vm]), pc = vm.pc + 2)
    Operation.BXL -> vm.copy(b = vm.b.xor(operand.literal[vm].toLong()), pc = vm.pc + 2)
    Operation.BST -> vm.copy(b = operand.combo[vm].and(7).toLong(), pc = vm.pc + 2)
    Operation.JNZ -> if (vm.a == 0L) {
        vm.copy(pc = vm.pc + 2)
    } else {
        vm.copy(pc = operand.literal[vm])
    }

    Operation.BXC -> vm.copy(b = vm.b.xor(vm.c), pc = vm.pc + 2)
    Operation.OUT -> vm.copy(out = vm.out + operand.combo[vm].mod(8).toInt(), pc = vm.pc + 2)
    Operation.BDV -> vm.copy(b = div(vm.a, operand.combo[vm]), pc = vm.pc + 2)
    Operation.CDV -> vm.copy(c = div(vm.a, operand.combo[vm]), pc = vm.pc + 2)
    Operation.NOP -> vm.copy(pc = vm.pc + 2)
}

private fun div(numerator: Long, denominator: Int): Long {
    return numerator.ushr(denominator)
}

private class Operand(number: Int) {
    val literal = LiteralOperand(number)
    val combo = ComboOperand.entries[number]
}

private data class LiteralOperand(val number: Int) {
    operator fun get(vm: VM): Int = number
}

private enum class ComboOperand {
    LITERAL_0,
    LITERAL_1,
    LITERAL_2,
    LITERAL_3,
    REGISTER_A,
    REGISTER_B,
    REGISTER_C,
    RESERVED,
    ;

    operator fun get(vm: VM): Int = when (this) {
        LITERAL_0 -> 0
        LITERAL_1 -> 1
        LITERAL_2 -> 2
        LITERAL_3 -> 3
        REGISTER_A -> vm.a.mod(8)
        REGISTER_B -> vm.b.mod(8)
        REGISTER_C -> vm.c.mod(8)
        RESERVED -> error("reserved")
    }
}
