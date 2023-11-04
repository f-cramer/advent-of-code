package de.cramer.adventofcode.year2020.day12

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 25)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 286)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Instruction>): Int {
    val ship = ShipWithFacing(Vector.ZERO, MoveDirection.EAST)
    input.forEach { it(ship) }
    return ship.position.let { abs(it.x) + abs(it.y) }
}

private fun problem02(input: List<Instruction>): Int {
    val ship = ShipWithWaypoint(Vector.ZERO, Vector(10, 1))
    input.forEach { it(ship) }
    return ship.position.let { abs(it.x) + abs(it.y) }
}

private fun String.parse(): List<Instruction> {
    fun String.toInstruction(): Instruction {
        val code = this[0]
        val value = substring(1).toInt()
        return when (code) {
            'N' -> AbsoluteMovementInstruction(MoveDirection.NORTH, value)
            'S' -> AbsoluteMovementInstruction(MoveDirection.SOUTH, value)
            'E' -> AbsoluteMovementInstruction(MoveDirection.EAST, value)
            'W' -> AbsoluteMovementInstruction(MoveDirection.WEST, value)
            'L' -> TurnInstruction(TurnDirection.LEFT, value)
            'R' -> TurnInstruction(TurnDirection.RIGHT, value)
            'F' -> ForwardMovementInstruction(value)
            else -> error("cannot parse $this to instruction")
        }
    }

    return lineSequence()
        .map { it.toInstruction() }
        .toList()
}

private data class ShipWithFacing(
    var position: Vector,
    var facing: MoveDirection,
)

private data class ShipWithWaypoint(
    var position: Vector,
    var relativeWaypoint: Vector,
)

private sealed interface Instruction {
    operator fun invoke(ship: ShipWithFacing)

    operator fun invoke(ship: ShipWithWaypoint)
}

private data class ForwardMovementInstruction(
    val value: Int,
) : Instruction {
    override fun invoke(ship: ShipWithFacing) {
        ship.position += ship.facing.vector * value
    }

    override fun invoke(ship: ShipWithWaypoint) {
        ship.position += ship.relativeWaypoint * value
    }
}

private data class AbsoluteMovementInstruction(
    val facing: MoveDirection,
    val value: Int,
) : Instruction {
    override fun invoke(ship: ShipWithFacing) {
        ship.position += facing.vector * value
    }

    override fun invoke(ship: ShipWithWaypoint) {
        ship.relativeWaypoint += facing.vector * value
    }
}

private data class TurnInstruction(
    val direction: TurnDirection,
    val degrees: Int,
) : Instruction {
    private val turnDirection: TurnDirection = if (degrees < 0) {
        if (direction == TurnDirection.LEFT) TurnDirection.RIGHT else TurnDirection.LEFT
    } else {
        direction
    }

    private val turnTimes = abs(degrees / 90)

    override fun invoke(ship: ShipWithFacing) {
        repeat(turnTimes) {
            ship.facing = when (ship.facing) {
                MoveDirection.NORTH -> when (turnDirection) {
                    TurnDirection.LEFT -> MoveDirection.WEST
                    TurnDirection.RIGHT -> MoveDirection.EAST
                }

                MoveDirection.EAST -> when (turnDirection) {
                    TurnDirection.LEFT -> MoveDirection.NORTH
                    TurnDirection.RIGHT -> MoveDirection.SOUTH
                }

                MoveDirection.SOUTH -> when (turnDirection) {
                    TurnDirection.LEFT -> MoveDirection.EAST
                    TurnDirection.RIGHT -> MoveDirection.WEST
                }

                MoveDirection.WEST -> when (turnDirection) {
                    TurnDirection.LEFT -> MoveDirection.SOUTH
                    TurnDirection.RIGHT -> MoveDirection.NORTH
                }
            }
        }
    }

    override fun invoke(ship: ShipWithWaypoint) {
        repeat(turnTimes) {
            ship.relativeWaypoint = when (turnDirection) {
                TurnDirection.LEFT -> Vector(-ship.relativeWaypoint.y, ship.relativeWaypoint.x)
                TurnDirection.RIGHT -> Vector(ship.relativeWaypoint.y, -ship.relativeWaypoint.x)
            }
        }
    }
}

private enum class MoveDirection(
    val vector: Vector,
) {
    NORTH(Vector(0, 1)),
    EAST(Vector(1, 0)),
    SOUTH(Vector(0, -1)),
    WEST(Vector(-1, 0)),
}

private enum class TurnDirection {
    LEFT,
    RIGHT,
}
