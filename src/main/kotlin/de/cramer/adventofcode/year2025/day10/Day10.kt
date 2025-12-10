package de.cramer.adventofcode.year2025.day10

import com.microsoft.z3.Context
import com.microsoft.z3.IntExpr
import com.microsoft.z3.IntNum
import com.microsoft.z3.Status
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import java.util.BitSet

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 7)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 33)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Machine>): Int {
    data class State(val presses: Int, val indicatorLights: BitSet, val executedPresses: Set<IntArray>)

    fun BitSet.pressButton(wiring: IntArray): BitSet = (clone() as BitSet).apply {
        wiring.forEach { this[it] = !this[it] }
    }

    fun Machine.fewestButtonPressesRequired(): Int {
        val queue = ArrayDeque<State>()
        queue += State(0, BitSet(indicatorLights.size()), executedPresses = emptySet())
        while (true) {
            val state = queue.removeFirst()
            val presses = state.presses + 1
            for (buttonWiring in buttonWirings) {
                if (buttonWiring in state.executedPresses) {
                    continue
                }

                val indicatorLights = state.indicatorLights.pressButton(buttonWiring)
                if (indicatorLights == this.indicatorLights) {
                    return presses
                }
                val executedPresses = state.executedPresses.plusElement(buttonWiring)
                queue += state.copy(presses = presses, indicatorLights = indicatorLights, executedPresses = executedPresses)
            }
        }
    }

    return input.sumOf { it.fewestButtonPressesRequired() }
}

private fun problem02(input: List<Machine>): Int {
    fun Machine.fewestButtonPressesRequired(): Int {
        val context = Context()
        val optimize = context.mkOptimize()
        val presses = context.mkIntConst("presses")

        val buttonVariables = buttonWirings.indices.map { context.mkIntConst("button$it") }.toTypedArray()
        val buttonsByCounter = buildMap {
            for ((index, buttonVariable) in buttonVariables.withIndex()) {
                for (flip in buttonWirings[index]) {
                    computeIfAbsent(flip) { mutableListOf() } += buttonVariable
                }
            }
        }

        for ((counterIndex, counterButtons) in buttonsByCounter.entries) {
            val targetValue = context.mkInt(joltageRequirements[counterIndex])
            val sumOfButtonPresses = context.mkAdd(*counterButtons.toTypedArray())
            val equality = context.mkEq(targetValue, sumOfButtonPresses)
            optimize.Add(equality)
        }

        val zero = context.mkInt(0)
        for (variable in buttonVariables) {
            val nonNegative = context.mkGe(variable, zero)
            optimize.Add(nonNegative)
        }

        val sumOfAllButtonVariables = context.mkAdd(*buttonVariables) as IntExpr
        val totalPressedEquality = context.mkEq(presses, sumOfAllButtonVariables)
        optimize.Add(totalPressedEquality)

        optimize.MkMinimize(presses)
        return when (val status = optimize.Check()) {
            Status.SATISFIABLE -> (optimize.getModel().evaluate(presses, false) as IntNum).getInt()
            Status.UNSATISFIABLE -> error("Problem is UNSATISFIABLE (no solution exists)")
            else -> error("Optimization could not be determined (status $status)")
        }
    }

    return input.sumOf { it.fewestButtonPressesRequired() }
}

private fun String.parse(): List<Machine> {
    val regex = """\[([.#]+)] ((?:\([\d,]+\) )+)\{([\d,]+)}""".toRegex()
    fun String.parseMachine(): Machine {
        val (g1, g2, g3) = regex.matchEntire(this)!!.destructured
        val indicatorLights = g1.map { it == '#' }.let {
            BitSet().apply { it.forEachIndexed { i, b -> set(i, b) } }
        }
        val buttonWirings = g2.trim().split(" ").map {
            it.removeSurrounding("(", ")").split(",").map { c -> c.toInt() }.toIntArray()
        }.toTypedArray()
        val joltageRequirement = g3.split(",").map { it.toInt() }.toIntArray()
        return Machine(indicatorLights, buttonWirings, joltageRequirement)
    }

    return lines()
        .map { it.parseMachine() }
}

@Suppress("ArrayInDataClass")
private data class Machine(
    val indicatorLights: BitSet,
    val buttonWirings: Array<IntArray>,
    val joltageRequirements: IntArray,
)
