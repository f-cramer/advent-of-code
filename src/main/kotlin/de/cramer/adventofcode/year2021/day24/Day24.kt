package de.cramer.adventofcode.year2021.day24

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 8)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Command>): Long {
    val numbers = 9L downTo 1L
    val alu = Alu()
    return alu.solve(input, numbers)!!
}

private fun problem02(input: List<Command>): Long {
    val numbers = 1L..9L
    val alu = Alu()
    return alu.solve(input, numbers)!!
}

private fun String.parse(): List<Command> {
    fun Char.registerIndex() = this - 'w'

    fun String.splitToCommand(registerCreator: (Int, Int) -> Command, immediateCreator: (Int, Long) -> Command): Command {
        val (a, b) = split(" ")
        val aRegisterIndex = a.single().registerIndex()
        if (b.length == 1) {
            val possibleRegister = b[0]
            if (possibleRegister.isLetter()) {
                return registerCreator(aRegisterIndex, possibleRegister.registerIndex())
            }
        }
        return immediateCreator(aRegisterIndex, b.toLong())
    }

    fun String.toCommand() = when {
        startsWith("inp") -> InputCommand(substringAfter("inp ").single().registerIndex())
        startsWith("add") -> substringAfter("add ").splitToCommand(::AddCommand, ::AddImmediateCommand)
        startsWith("mul") -> substringAfter("mul ").splitToCommand(::MultiplyCommand, ::MultiplyImmediateCommand)
        startsWith("div") -> substringAfter("div ").splitToCommand(::DivideCommand, ::DivideImmediateCommand)
        startsWith("mod") -> substringAfter("mod ").splitToCommand(::ModuloCommand, ::ModuloImmediateCommand)
        startsWith("eql") -> substringAfter("eql ").splitToCommand(::EqualsCommand, ::EqualsImmediateCommand)
        else -> error("cannot parse $this to command")
    }

    return lineSequence()
        .map { it.toCommand() }
        .toList()
}

private data class Alu(
    val registers: MutableList<Long> = MutableList(4) { 0 },
) {
    operator fun get(register: Int): Long = registers[register]

    operator fun set(register: Int, value: Long) {
        registers[register] = value
    }
}

private data class AluRange(
    val registers: MutableList<Pair<Long, Long>> = MutableList(4) { 0L to 0L },
) {
    constructor(alu: Alu) : this(alu.registers.map { it to it }.toMutableList())

    operator fun get(register: Int): Pair<Long, Long> = registers[register]

    operator fun set(register: Int, value: Pair<Long, Long>) {
        registers[register] = value
    }
}

private fun Alu.solve(commands: List<Command>, numbers: LongProgression, prefix: String = ""): Long? {
    for ((index, command) in commands.withIndex()) {
        when (command) {
            is InputCommand -> {
                val nextCommands = commands.subList(index + 1, commands.size)
                return numbers.firstNotNullOfOrNull {
                    this[command.register] = it
                    if (checkRange(nextCommands) != RangeResult.IMPOSSIBLE) {
                        copy(registers = registers.toMutableList())
                            .solve(nextCommands, numbers, prefix + it.toString())
                    } else {
                        null
                    }
                }
            }

            else -> command(this)
        }
    }
    return if (this[3] == 0L) prefix.toLong() else null
}

private fun Alu.checkRange(commands: List<Command>): RangeResult {
    val alu = AluRange(this)
    commands.forEach { command ->
        val result = command(alu)
        if (result != null) {
            return result
        }
    }
    val (min, max) = alu[3]
    return if (min <= 0 && 0 <= max) RangeResult.POSSIBLE else RangeResult.IMPOSSIBLE
}

private sealed interface Command {
    operator fun invoke(alu: Alu)
    operator fun invoke(alu: AluRange): RangeResult?
}

private data class InputCommand(
    val register: Int,
) : Command {
    override fun invoke(alu: Alu) {
        throw UnsupportedOperationException()
    }

    override fun invoke(alu: AluRange): RangeResult? {
        alu[register] = 0L to 9L
        return null
    }
}

private data class AddCommand(
    val register1: Int,
    val register2: Int,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register1] += alu[register2]
    }

    override fun invoke(alu: AluRange): RangeResult? {
        val (min1, max1) = alu[register1]
        val (min2, max2) = alu[register2]
        alu[register1] = (min1 + min2) to (max1 + max2)
        return null
    }
}

private data class AddImmediateCommand(
    val register: Int,
    val argument: Long,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register] += argument
    }

    override fun invoke(alu: AluRange): RangeResult? {
        val (min1, max1) = alu[register]
        alu[register] = (min1 + argument) to (max1 + argument)
        return null
    }
}

private data class MultiplyCommand(
    val register1: Int,
    val register2: Int,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register1] *= alu[register2]
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return multiply(alu, register1, alu[register2])
    }
}

private data class MultiplyImmediateCommand(
    val register: Int,
    val argument: Long,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register] *= argument
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return multiply(alu, register, argument to argument)
    }
}

private fun multiply(alu: AluRange, register: Int, argument: Pair<Long, Long>): RangeResult? {
    val (min1, max1) = alu[register]
    val (min2, max2) = argument
    alu[register] = when {
        min1 >= 0 && min2 >= 0 -> min1 * min2 to max1 * max2
        min1 <= 0 && min2 <= 0 -> max1 * max2 to min1 * min2
        min1 >= 0 -> min1 * max2 to max1 * min2
        else -> max1 * min2 to min1 * max2
    }
    return null
}

private data class DivideCommand(
    val register1: Int,
    val register2: Int,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register1] /= alu[register2]
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return divide(alu, register1, alu[register2])
    }
}

private data class DivideImmediateCommand(
    val register: Int,
    val argument: Long,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register] /= argument
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return divide(alu, register, argument to argument)
    }
}

private fun divide(alu: AluRange, register: Int, argument: Pair<Long, Long>): RangeResult? {
    val (min1, max1) = alu[register]
    val (min2, max2) = argument
    alu[register] = when {
        min2 > 0 -> min1 / max2 to max1 / min2
        max2 < 0 -> max1 / min2 to min1 / max2
        else -> return RangeResult.UNKNOWN
    }
    return null
}

private data class ModuloCommand(
    val register1: Int,
    val register2: Int,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register1] %= alu[register2]
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return modulo(alu, register1, alu[register2])
    }
}

private data class ModuloImmediateCommand(
    val register: Int,
    val argument: Long,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register] %= argument
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return modulo(alu, register, argument to argument)
    }
}

private fun modulo(alu: AluRange, register: Int, argument: Pair<Long, Long>): RangeResult? {
    val (min1, max1) = alu[register]
    val (min2, max2) = argument
    alu[register] = if (0 < min2 && min2 == max2) {
        if (max1 - min1 + 1 < min2 && min1 % min2 <= max1 % min2) min1 % min2 to max1 % min2 else 0L to min2 - 1
    } else {
        return RangeResult.UNKNOWN
    }

    return null
}

private data class EqualsCommand(
    val register1: Int,
    val register2: Int,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register1] = if (alu[register1] == alu[register2]) 1 else 0
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return equals(alu, register1, alu[register2])
    }
}

private data class EqualsImmediateCommand(
    val register: Int,
    val argument: Long,
) : Command {
    override fun invoke(alu: Alu) {
        alu[register] = if (alu[register] == argument) 1 else 0
    }

    override fun invoke(alu: AluRange): RangeResult? {
        return equals(alu, register, argument to argument)
    }
}

private fun equals(alu: AluRange, register: Int, argument: Pair<Long, Long>): RangeResult? {
    val (min1, max1) = alu[register]
    val (min2, max2) = argument
    alu[register] = if (min1 == max1 && max1 == min2 && min2 == max2) {
        1L to 1L
    } else if (min1 <= max2 && min2 <= max1) {
        0L to 1L
    } else {
        0L to 0L
    }

    return null
}

private enum class RangeResult {
    POSSIBLE, IMPOSSIBLE, UNKNOWN
}
