package de.cramer.adventofcode.year2022.day10

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 13140)
    val testResult02 = problem02(testInput)
    checkTestResult(
        testResult02,
        """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent(),
    )

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Command>): Int {
    fun mod(cycle: Int): Int = if (cycle < 20) {
        cycle
    } else {
        (cycle - 20) % 40
    }

    var totalSignalStrength = 0
    CPU().execute(input) {
        if (mod(it) == 0) {
            totalSignalStrength += it * x
        }
    }

    return totalSignalStrength
}

private fun problem02(input: List<Command>): String = buildString {
    CPU().execute(input) {
        val column = (it - 1) % 40
        val char = if (abs(x - column) <= 1) "#" else "."
        append(char)

        if (it % 40 == 0 && it != 240) {
            append('\n')
        }
    }
}

private fun CPU.execute(commands: List<Command>, onCycle: CPU.(Int) -> Unit) {
    fun newCycle(cycle: Int) {
        if (cycle <= 240) onCycle(cycle)
    }

    var cycle = 1
    newCycle(cycle)

    for (command in commands) {
        repeat(command.cycles - 1) {
            cycle++
            newCycle(cycle)
        }

        cycle++
        command.execute(this)
        newCycle(cycle)
    }
}

private fun String.parse(): List<Command> = lineSequence().map { it.toCommand() }.toList()

private fun String.toCommand(): Command = when {
    this == "noop" -> Noop
    startsWith("addx ") -> AddX(substringAfter("addx ").toInt())
    else -> error("cannot parse $this to Command")
}

private sealed interface Command {
    val cycles: Int

    fun execute(cpu: CPU)
}

private object Noop : Command {
    override val cycles: Int = 1

    override fun execute(cpu: CPU) {
    }

    override fun toString(): String = "Noop"
}

private data class AddX(
    val value: Int,
) : Command {
    override val cycles: Int = 2

    override fun execute(cpu: CPU) {
        cpu.x += value
    }
}

private data class CPU(
    var x: Int = 1,
)
