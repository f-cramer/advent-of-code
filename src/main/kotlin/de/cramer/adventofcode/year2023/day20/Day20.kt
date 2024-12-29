package de.cramer.adventofcode.year2023.day20

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.math.lcm
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 11687500)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Module>): Int {
    val modules = input.map { it.clone() }
    val initialState = input.map { it.currentState }

    var cycleLength = 0
    val numberOfPulses = mutableMapOf<Pulse, Int>()

    val maximumClicks = 1000

    do {
        cycleLength++
        push(modules).forEach { (p, c) ->
            numberOfPulses.merge(p, c, Int::plus)
        }
    } while (modules.map { it.currentState } != initialState && cycleLength < maximumClicks)

    val cycles = maximumClicks / cycleLength
    val totalNumberOfPulses = numberOfPulses.values
        .map { it * cycles }
    return totalNumberOfPulses.reduce(Int::times)
}

private fun problem02(input: List<Module>): Long {
    val sj = cylesUntilHighPulse(input, "sj")
    val qq = cylesUntilHighPulse(input, "qq")
    val ls = cylesUntilHighPulse(input, "ls")
    val bg = cylesUntilHighPulse(input, "bg")
    return lcm(sj.toLong(), lcm(qq.toLong(), lcm(ls.toLong(), bg.toLong())))
}

private fun cylesUntilHighPulse(input: List<Module>, observed: String): Int {
    data class Observer(
        override val name: String,
        var receivedHighPulse: Boolean = false,
    ) : Module {
        override val receivers: List<String> get() = emptyList()
        override val currentState: Any get() = Unit

        override fun receive(source: Module, pulse: Pulse): List<Action> {
            if (pulse == Pulse.LOW) {
                receivedHighPulse = true
            }
            return emptyList()
        }

        override fun clone(): Module = copy()
    }

    val observer = Observer(observed)
    val modules = input.mapTo(mutableListOf()) { it.clone() }
    modules.removeIf { it.name == observed }
    modules += observer

    var clicks = 0
    while (!observer.receivedHighPulse) {
        clicks++
        push(modules)
    }

    return clicks
}

private fun push(modules: Collection<Module>): Map<Pulse, Int> {
    val broadcaster = modules.single { it is Broadcast }
    val numberOfPulses: MutableMap<Pulse, Int> = mutableMapOf()

    val queue: ArrayDeque<Action> = ArrayDeque()
    queue += Action(broadcaster, Pulse.LOW, broadcaster.name)

    while (queue.isNotEmpty()) {
        val action = queue.removeFirst()

        numberOfPulses.merge(action.pulse, 1, Int::plus)
        val receiver = modules.singleOrNull { it.name == action.receiver } ?: continue
        queue += receiver.receive(action.source, action.pulse)
    }

    return numberOfPulses
}

private fun String.parse(): List<Module> {
    fun String.parseModule(): Module {
        val (fullName, receiversString) = split(" -> ")
        val receivers = receiversString.split(", ")
        if (fullName == "broadcaster") {
            return Broadcast(fullName, receivers)
        }

        val prefix = fullName.first()
        val name = fullName.substring(1)
        return when (prefix) {
            '%' -> FlipFlop(name, receivers)
            '&' -> Conjunction(name, receivers)
            else -> error("cannot parse $fullName")
        }
    }

    val allModules = lines().map { it.parseModule() }
    allModules.forEach { it.initialize(allModules) }
    return allModules
}

private enum class Pulse {
    LOW,
    HIGH,
}

private interface Module {
    val name: String
    val receivers: List<String>
    val currentState: Any

    fun initialize(allModules: List<Module>) {}

    fun receive(source: Module, pulse: Pulse): List<Action>

    fun clone(): Module
}

private fun Module.send(pulse: Pulse): List<Action> = receivers.map {
    Action(this, pulse, it)
}

private data class FlipFlop(
    override val name: String,
    override val receivers: List<String>,
    var state: Boolean = false,
) : Module {
    override val currentState: Any get() = state

    override fun receive(source: Module, pulse: Pulse): List<Action> = when (pulse) {
        Pulse.LOW -> {
            state = !state
            send(if (state) Pulse.HIGH else Pulse.LOW)
        }

        Pulse.HIGH -> emptyList()
    }

    override fun clone(): FlipFlop = copy(receivers = receivers.toList())
}

private data class Conjunction(
    override val name: String,
    override val receivers: List<String>,
    val previousPulses: MutableMap<String, Pulse> = mutableMapOf(),
) : Module {
    override val currentState: Any get() = previousPulses.toMap()

    override fun initialize(allModules: List<Module>) {
        previousPulses += allModules.asSequence()
            .filter { name in it.receivers }
            .map { it.name }
            .associateWith { Pulse.LOW }
    }

    override fun receive(source: Module, pulse: Pulse): List<Action> {
        previousPulses[source.name] = pulse
        val nextPulse = if (previousPulses.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
        return send(nextPulse)
    }

    override fun clone(): Conjunction = copy(receivers = receivers.toList(), previousPulses = previousPulses.toMutableMap())
}

private data class Broadcast(
    override val name: String,
    override val receivers: List<String>,
) : Module {
    override val currentState: Any get() = Unit

    override fun receive(source: Module, pulse: Pulse): List<Action> = send(pulse)

    override fun clone(): Broadcast = copy(receivers = receivers.toList())
}

private data class Action(
    val source: Module,
    val pulse: Pulse,
    val receiver: String,
)
