package de.cramer.adventofcode.year2022.day19

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runTest
import kotlin.math.ceil

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 33)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 3472)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input) }
}

private fun problem01(input: List<Blueprint>): Int {
    val initialState = State(robots = Amount(ore = 1), remainingRounds = 24)
    return input.sumOf { it.index * initialState.simulate(it) }
}

private fun problem02(input: List<Blueprint>): Int {
    val initialState = State(robots = Amount(ore = 1), remainingRounds = 32)
    return input.asSequence()
        .take(3)
        .map { initialState.simulate(it) }
        .reduce { acc, it -> acc * it }
}

private fun State.simulate(blueprint: Blueprint): Int {
    val costsForOneRobotOfEachType = (blueprint.oreRobotCosts + blueprint.clayRobotCosts + blueprint.obsidianRobotCosts + blueprint.geodeRobotCosts).copy(geodes = Int.MAX_VALUE - 2)

    fun ceilDiv(x: Int, y: Int) = ceil(x / y.toDouble()).toInt()

    fun State.getTimeToGetResources(expectedResources: Amount): Int? {
        if (expectedResources.ore > 0 && robots.ore == 0) return null
        if (expectedResources.clay > 0 && robots.clay == 0) return null
        if (expectedResources.obsidian > 0 && robots.obsidian == 0) return null
        if (expectedResources.geodes > 0 && robots.geodes == 0) return null

        val oreTime = if (expectedResources.ore == 0) 0 else ceilDiv(expectedResources.ore - resources.ore, robots.ore)
        val clayTime = if (expectedResources.clay == 0) 0 else ceilDiv(expectedResources.clay - resources.clay, robots.clay)
        val obsidianTime = if (expectedResources.obsidian == 0) 0 else ceilDiv(expectedResources.obsidian - resources.obsidian, robots.obsidian)
        val geodesTime = if (expectedResources.geodes == 0) 0 else ceilDiv(expectedResources.geodes - resources.geodes, robots.geodes)
        return maxOf(oreTime, clayTime, obsidianTime, geodesTime)
    }

    fun State.getRobotChoices(costs: Amount, newRobot: Amount, selector: Amount.() -> Int): Sequence<State> {
        val resourceDemand = costsForOneRobotOfEachType.selector()
        val currentRobots = robots.selector()
        if (resourceDemand > currentRobots) {
            val availableResources = resources.selector()
            if (availableResources <= resourceDemand + 2) {
                val time = getTimeToGetResources(costs)
                if (time != null && time < remainingRounds) {
                    return sequenceOf(copy(resources = resources - costs + robots * time, robots = robots + newRobot, remainingRounds = remainingRounds - time))
                }
            }
        }

        return emptySequence()
    }

    fun State.getOreRobotChoices() = getRobotChoices(blueprint.oreRobotCosts, Amount(ore = 1), Amount::ore)
    fun State.getClayRobotChoices() = getRobotChoices(blueprint.clayRobotCosts, Amount(clay = 1), Amount::clay)
    fun State.getObsidianRobotChoices() = getRobotChoices(blueprint.obsidianRobotCosts, Amount(obsidian = 1), Amount::obsidian)
    fun State.getGeodeRobotChoices() = getRobotChoices(blueprint.geodeRobotCosts, Amount(geodes = 1), Amount::geodes)

    fun State.getDoNothingChoices() = sequence {
        yield(copy(resources = resources + robots * (remainingRounds - 1), remainingRounds = 1))
    }

    fun State.impl(): Int {
        if (remainingRounds <= 0) {
            return resources.geodes
        }

        val choices = getOreRobotChoices() + getClayRobotChoices() + getObsidianRobotChoices() + getGeodeRobotChoices() + getDoNothingChoices()
        return choices
            .map { it.copy(resources = it.resources + robots, remainingRounds = it.remainingRounds - 1) }
            .maxOf { it.impl() }
    }

    return impl()
}

private fun String.parse(): List<Blueprint> {
    fun String.toBlueprint(): Blueprint {
        val (index, r1) = substringAfter("Blueprint ").split(":", limit = 2)
        val (oreRobotOreCosts, r2) = r1.substringAfter("Each ore robot costs ").split(" ore.", limit = 2)
        val (clayRobotOreCosts, r3) = r2.substringAfter("Each clay robot costs ").split(" ore.", limit = 2)
        val (obsidianRobotOreCosts, r4) = r3.substringAfter("Each obsidian robot costs ").split(" ore and ", limit = 2)
        val (obsidianRobotClayCosts, r5) = r4.split(" clay.", limit = 2)
        val (geodeRobotOreCosts, r6) = r5.substringAfter("Each geode robot costs ").split(" ore and ", limit = 2)
        val geodeRobotObsidianCosts = r6.takeWhile { it.isDigit() }

        return Blueprint(
            index.toInt(),
            Amount(oreRobotOreCosts.toInt(), 0, 0),
            Amount(clayRobotOreCosts.toInt(), 0, 0),
            Amount(obsidianRobotOreCosts.toInt(), obsidianRobotClayCosts.toInt(), 0),
            Amount(geodeRobotOreCosts.toInt(), 0, geodeRobotObsidianCosts.toInt()),
        )
    }

    return lineSequence()
        .map { it.toBlueprint() }
        .toList()
}

private data class Blueprint(
    val index: Int,
    val oreRobotCosts: Amount,
    val clayRobotCosts: Amount,
    val obsidianRobotCosts: Amount,
    val geodeRobotCosts: Amount,
)

private data class State(
    val resources: Amount = Amount(),
    val robots: Amount = Amount(),
    val remainingRounds: Int,
)

private data class Amount(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,
) {
    operator fun plus(other: Amount): Amount = Amount(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geodes + other.geodes)
    operator fun minus(other: Amount): Amount = Amount(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geodes - other.geodes)
    operator fun times(factor: Int): Amount = Amount(factor * ore, factor * clay, factor * obsidian, factor * geodes)
}
