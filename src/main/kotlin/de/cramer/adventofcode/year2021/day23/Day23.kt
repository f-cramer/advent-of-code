package de.cramer.adventofcode.year2021.day23

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 12521)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 44169)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: State): Int {
    return input.organizeAmphipods()
}

private fun problem02(input: State): Int {
    val extendedInput = input.configuration.toMutableList()
    extendedInput.addAll(
        extendedInput.indexOfFirst { l -> l.any { it.isLetter() } } + 1,
        listOf(
            " #D#C#B#A#".toList(),
            " #D#B#A#C#".toList(),
        ),
    )
    return State(extendedInput).organizeAmphipods()
}

private fun String.parse(): State {
    val lines = lines()
    val inputs = lines.subList(1, lines.size - 1)
        .map { it.toList().subList(1, it.length - 1).toList() }
    return State(inputs)
}

private data class State(
    val configuration: List<List<Char>>,
) {
    private val hallway = configuration[0]
    private val rooms = configuration.drop(1)
    private val destinationRooms = destinationRoomIndices.mapValues { (amphipod, index) ->
        Room(amphipod, index, rooms.map { r -> r[index] })
    }
    private val legalHallwayIndices
        get() = possibleHallwayIndices.filter { hallway[it] == '.' }

    fun isFinished() = destinationRooms.values.all { it.hasOnlyValidAmphipods() }

    fun getNextPossibleStates(): Sequence<StateWithCost> = sequence {
        // amphipods in hallway that can move
        hallway.asSequence()
            .withIndex()
            .filter { (_, state) -> state.isLetter() && destinationRooms.getValue(state).hasOnlyValidAmphipodsOrEmpty() }
            .forEach { (index, amphipod) ->
                val room = destinationRooms.getValue(amphipod)
                if (isHallwayClear(index, room.index)) {
                    val y = room.content.lastIndexOf('.') + 1
                    val newCost = (abs(index - room.index) + y) * moveCosts.getValue(amphipod)
                    val newConfiguration = configuration.map { row -> row.toMutableList() }.apply {
                        this[0][index] = '.'
                        this[y][room.index] = amphipod
                    }
                    yield(StateWithCost(State(newConfiguration), newCost))
                }
            }
        // rooms with wrong amphipods
        destinationRooms.values.asSequence()
            .filter { it.hasAmphipodsWithWrongType() }
            .forEach { room ->
                val (moveIndex, wrongAmphipod) = room.content.withIndex().first { it.value != '.' }
                val y = moveIndex + 1
                legalHallwayIndices.forEach {
                    if (isHallwayClear(it, room.index)) {
                        val cost = (abs(room.index - it) + y) * moveCosts.getValue(wrongAmphipod)
                        val newConfiguration = configuration.map { row -> row.toMutableList() }.apply {
                            this[y][room.index] = '.'
                            this[0][it] = wrongAmphipod
                        }
                        yield(StateWithCost(State(newConfiguration), cost))
                    }
                }
            }
    }

    private fun isHallwayClear(start: Int, end: Int): Boolean =
        hallway.slice(if (start > end) end until start else (start + 1)..end).all { it == '.' }

    companion object {
        private val moveCosts = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
        private val destinationRoomIndices = listOf('A', 'B', 'C', 'D').associateWith { (it.code - 'A'.code + 1) * 2 }
        private val possibleHallwayIndices = listOf(0, 1, 3, 5, 7, 9, 10)
    }
}

private fun State.organizeAmphipods(): Int {
    val nextStates = PriorityQueue<StateWithCost>().also {
        it += StateWithCost(this, 0)
    }
    val visited = mutableSetOf<StateWithCost>()
    val minimumCosts = mutableMapOf<State, Int>().withDefault { Int.MAX_VALUE }

    while (nextStates.isNotEmpty()) {
        val (state, cost) = nextStates.poll().also { visited += it }
        state.getNextPossibleStates()
            .filterNot { it in visited }
            .forEach {
                val newCost = cost + it.cost
                if (newCost < minimumCosts.getValue(it.state)) {
                    minimumCosts[it.state] = newCost
                    nextStates += StateWithCost(it.state, newCost)
                }
            }
    }

    return minimumCosts.entries.first { (state, _) -> state.isFinished() }.value
}

private data class StateWithCost(val state: State, val cost: Int) : Comparable<StateWithCost> {
    override fun compareTo(other: StateWithCost) = cost.compareTo(other.cost)
}

private data class Room(
    val amphipod: Char,
    val index: Int,
    val content: List<Char>,
) {
    fun hasOnlyValidAmphipods() = content.all { it == amphipod }

    fun hasOnlyValidAmphipodsOrEmpty() = content.all { it == '.' || it == amphipod }

    fun hasAmphipodsWithWrongType() = !hasOnlyValidAmphipodsOrEmpty()
}
