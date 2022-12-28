package de.cramer.adventofcode.year2022.day22

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.get
import de.cramer.adventofcode.isValidIndex
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runTest
import de.cramer.adventofcode.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 6032)
    val testResult02 = problem02(testInput, ::getTestDataWrapper)
    checkTestResult(testResult02, 5031)

    val input = readInput().parse()
    runTest("result01") { problem01(input) }
    runTest("result02") { problem02(input, ::getRealDataWrapper) }
}

private fun problem01(input: Input): Int {
    return input.getPassword { position, direction ->
        var jump = position
        var afterJump = jump - direction.vector
        while (input.tiles.isValidIndex(afterJump) && input.tiles[afterJump] != Tile.EMPTY) {
            jump = afterJump
            afterJump -= direction.vector
        }
        jump to direction
    }
}

private fun problem02(input: Input, wrapper: (Int, Int) -> (Vector, MoveDirection) -> Pair<Vector, MoveDirection>): Int {
    val yLength = input.tiles.size
    val xLength = input.tiles.indices.maxOf { input.tiles[it].size }
    return input.getPassword(wrapper(xLength, yLength))
}

private fun Input.getPassword(wrapper: (Vector, MoveDirection) -> Pair<Vector, MoveDirection>): Int {
    val (tiles, instructions) = this

    var position = Vector(tiles[0].indexOfFirst { it == Tile.OPEN }, 0)
    var direction = MoveDirection.EAST
    val positionAndDirections = mutableMapOf(position to direction)

    for (instruction in instructions) {
        when (instruction) {
            is TurnInstruction -> {
                direction = direction.turn(instruction.direction)
                positionAndDirections[position] = direction
            }

            is MoveInstructon -> {
                for (i in 0 until instruction.numberOfTiles) {
                    var next = position + direction.vector
                    var nextDirection = direction
                    if (!tiles.isValidIndex(next) || tiles[next] == Tile.EMPTY) {
                        val result = wrapper(position, direction)
                        next = result.first
                        nextDirection = result.second
                    }

                    if (tiles.isValidIndex(next) && tiles[next] == Tile.OPEN) {
                        position = next
                        direction = nextDirection
                        positionAndDirections[position] = direction
                    } else {
                        break
                    }
                }
            }
        }
    }

    return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction.score
}

private fun String.parse(): Input {
    fun Char.toTile(): Tile = when (this) {
        ' ' -> Tile.EMPTY
        '#' -> Tile.WALL
        '.' -> Tile.OPEN
        else -> error("cannot parse $this to tile")
    }

    fun String.toTiles(): List<List<Tile>> {
        val lines = lines()
        val maxLineLength = lines.maxOf { it.length }
        return lines.map { line ->
            val lineLength = line.length
            (0 until lineLength).map { line[it].toTile() } + (lineLength until maxLineLength).map { Tile.EMPTY }
        }
    }

    fun Char.toDirection() = when (this) {
        'L' -> TurnDirection.LEFT
        'R' -> TurnDirection.RIGHT
        else -> error("cannot parse $this to direction")
    }

    fun String.toInstructions(): List<Instruction> {
        if (isEmpty()) {
            return emptyList()
        }

        return buildList {
            var string = this@toInstructions
            while (string.isNotEmpty()) {
                string = if (string[0].isDigit()) {
                    val digits = string.takeWhile { it: Char -> it.isDigit() }
                    add(MoveInstructon(digits.toInt()))
                    string.substring(digits.length)
                } else {
                    add(TurnInstruction(string[0].toDirection()))
                    string.substring(1)
                }
            }
        }
    }

    val (tiles, path) = splitByEmptyLines()
    return Input(tiles.toTiles(), path.toInstructions())
}

private data class Input(
    val tiles: List<List<Tile>>,
    val instructions: List<Instruction>,
)

private enum class Tile {
    OPEN, WALL, EMPTY
}

private sealed interface Instruction

private data class MoveInstructon(
    val numberOfTiles: Int,
) : Instruction

private data class TurnInstruction(
    val direction: TurnDirection,
) : Instruction

private enum class TurnDirection {
    LEFT, RIGHT
}

private enum class MoveDirection(
    val vector: Vector,
    val score: Int,
) {
    NORTH(Vector(0, -1), 3),
    EAST(Vector(1, 0), 0),
    SOUTH(Vector(0, 1), 1),
    WEST(Vector(-1, 0), 2),
}

private fun MoveDirection.turn(turnDirection: TurnDirection): MoveDirection = when (turnDirection) {
    TurnDirection.LEFT -> when (this) {
        MoveDirection.NORTH -> MoveDirection.WEST
        MoveDirection.EAST -> MoveDirection.NORTH
        MoveDirection.SOUTH -> MoveDirection.EAST
        MoveDirection.WEST -> MoveDirection.SOUTH
    }

    TurnDirection.RIGHT -> when (this) {
        MoveDirection.NORTH -> MoveDirection.EAST
        MoveDirection.EAST -> MoveDirection.SOUTH
        MoveDirection.SOUTH -> MoveDirection.WEST
        MoveDirection.WEST -> MoveDirection.NORTH
    }
}

private data class Face(
    val xRange: IntRange,
    val yRange: IntRange,
) {
    operator fun contains(vector: Vector): Boolean = vector.x in xRange && vector.y in yRange
}

private fun wrap(position: Vector, fromFace: Face, fromDirection: MoveDirection, toFace: Face, toDirection: MoveDirection, moveUpsideDown: Boolean): Pair<Vector, MoveDirection> {
    val fromOffset = when (fromDirection) {
        MoveDirection.NORTH, MoveDirection.SOUTH -> position.x - fromFace.xRange.first
        MoveDirection.EAST, MoveDirection.WEST -> position.y - fromFace.yRange.first
    }

    val variableCoordinate = if (moveUpsideDown) {
        when (toDirection) {
            MoveDirection.NORTH, MoveDirection.SOUTH -> toFace.xRange.last - fromOffset
            MoveDirection.EAST, MoveDirection.WEST -> toFace.yRange.last - fromOffset
        }
    } else {
        when (toDirection) {
            MoveDirection.NORTH, MoveDirection.SOUTH -> toFace.xRange.first + fromOffset
            MoveDirection.EAST, MoveDirection.WEST -> toFace.yRange.first + fromOffset
        }
    }

    return when (toDirection) {
        MoveDirection.NORTH -> Vector(variableCoordinate, toFace.yRange.last)
        MoveDirection.EAST -> Vector(toFace.xRange.first, variableCoordinate)
        MoveDirection.SOUTH -> Vector(variableCoordinate, toFace.yRange.first)
        MoveDirection.WEST -> Vector(toFace.xRange.last, variableCoordinate)
    } to toDirection
}

private fun getTestDataWrapper(xLength: Int, yLength: Int): (Vector, MoveDirection) -> Pair<Vector, MoveDirection> {
    // side numbering
    //     1
    // 2 3 4
    //     5 6

    val side1 = Face(xLength / 2 until xLength / 4 * 3, 0 until yLength / 3)
    val side2 = Face(0 until xLength / 4, yLength / 3 until yLength / 3 * 2)
    val side3 = Face(xLength / 4 until xLength / 2, yLength / 3 until yLength / 3 * 2)
    val side4 = Face(xLength / 2 until xLength / 4 * 3, yLength / 3 until yLength / 3 * 2)
    val side5 = Face(xLength / 2 until xLength / 4 * 3, yLength / 3 * 2 until yLength)
    val side6 = Face(xLength / 4 * 3 until xLength, yLength / 3 * 2 until yLength)

    return { position, direction ->
        when (position) {
            in side1 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side1, direction, side2, MoveDirection.SOUTH, true)
                MoveDirection.EAST -> wrap(position, side1, direction, side6, MoveDirection.WEST, true)
                MoveDirection.SOUTH -> error("cannot happen (side1, south)")
                MoveDirection.WEST -> wrap(position, side1, direction, side3, MoveDirection.SOUTH, true)
            }

            in side2 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side2, direction, side1, MoveDirection.SOUTH, true)
                MoveDirection.EAST -> error("cannot happen (side2, east)")
                MoveDirection.SOUTH -> wrap(position, side2, direction, side5, MoveDirection.NORTH, true)
                MoveDirection.WEST -> wrap(position, side2, direction, side6, MoveDirection.NORTH, true)
            }

            in side3 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side3, direction, side1, MoveDirection.EAST, false)
                MoveDirection.EAST -> error("cannot happen (side3, east)")
                MoveDirection.SOUTH -> wrap(position, side3, direction, side5, MoveDirection.EAST, true)
                MoveDirection.WEST -> error("cannot happen (side3, west)")
            }

            in side4 -> when (direction) {
                MoveDirection.NORTH -> error("cannot happen(side4, north)")
                MoveDirection.EAST -> wrap(position, side4, direction, side6, MoveDirection.SOUTH, true)
                MoveDirection.SOUTH -> error("cannot happen(side4, south)")
                MoveDirection.WEST -> error("cannot happen(side4, west)")
            }

            in side5 -> when (direction) {
                MoveDirection.NORTH -> error("cannot happen(side5, north)")
                MoveDirection.EAST -> error("cannot happen(side5, east)")
                MoveDirection.SOUTH -> wrap(position, side5, direction, side2, MoveDirection.NORTH, true)
                MoveDirection.WEST -> wrap(position, side5, direction, side3, MoveDirection.NORTH, true)
            }

            in side6 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side6, direction, side4, MoveDirection.WEST, true)
                MoveDirection.EAST -> wrap(position, side6, direction, side1, MoveDirection.WEST, true)
                MoveDirection.SOUTH -> wrap(position, side6, direction, side2, MoveDirection.EAST, true)
                MoveDirection.WEST -> error("cannot happen(side6, west)")
            }

            else -> error("position $position not supported")
        }
    }
}

private fun getRealDataWrapper(xLength: Int, yLength: Int): (Vector, MoveDirection) -> Pair<Vector, MoveDirection> {
    // side numbering
    //   1 2
    //   3
    // 4 5
    //   6

    val side1 = Face(xLength / 3 until xLength / 3 * 2, 0 until yLength / 4)
    val side2 = Face(xLength / 3 * 2 until xLength, 0 until yLength / 4)
    val side3 = Face(xLength / 3 until xLength / 3 * 2, yLength / 4 until yLength / 2)
    val side4 = Face(0 until xLength / 3, yLength / 2 until yLength / 4 * 3)
    val side5 = Face(xLength / 3 until xLength / 3 * 2, yLength / 2 until yLength / 4 * 3)
    val side6 = Face(0 until xLength / 3, yLength / 4 * 3 until yLength)

    return { position, direction ->
        when (position) {
            in side1 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side1, direction, side6, MoveDirection.EAST, false)
                MoveDirection.EAST -> error("cannot happen (side1, east)")
                MoveDirection.SOUTH -> error("cannot happen (side1, south)")
                MoveDirection.WEST -> wrap(position, side1, direction, side4, MoveDirection.EAST, true)
            }

            in side2 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side2, direction, side6, MoveDirection.NORTH, false)
                MoveDirection.EAST -> wrap(position, side2, direction, side5, MoveDirection.WEST, true)
                MoveDirection.SOUTH -> wrap(position, side2, direction, side3, MoveDirection.WEST, false)
                MoveDirection.WEST -> error("cannot happen (side2, west)")
            }

            in side3 -> when (direction) {
                MoveDirection.NORTH -> error("cannot happen (side3, north)")
                MoveDirection.EAST -> wrap(position, side3, direction, side2, MoveDirection.NORTH, false)
                MoveDirection.SOUTH -> error("cannot happen (side3, south)")
                MoveDirection.WEST -> wrap(position, side3, direction, side4, MoveDirection.SOUTH, false)
            }

            in side4 -> when (direction) {
                MoveDirection.NORTH -> wrap(position, side4, direction, side3, MoveDirection.EAST, false)
                MoveDirection.EAST -> error("cannot happen(side4, east)")
                MoveDirection.SOUTH -> error("cannot happen(side4, south)")
                MoveDirection.WEST -> wrap(position, side4, direction, side1, MoveDirection.EAST, true)
            }

            in side5 -> when (direction) {
                MoveDirection.NORTH -> error("cannot happen(side5, north)")
                MoveDirection.EAST -> wrap(position, side5, direction, side2, MoveDirection.WEST, true)
                MoveDirection.SOUTH -> wrap(position, side5, direction, side6, MoveDirection.WEST, false)
                MoveDirection.WEST -> error("cannot happen(side5, west)")
            }

            in side6 -> when (direction) {
                MoveDirection.NORTH -> error("cannot happen(side6, north)")
                MoveDirection.EAST -> wrap(position, side6, direction, side5, MoveDirection.NORTH, false)
                MoveDirection.SOUTH -> wrap(position, side6, direction, side2, MoveDirection.SOUTH, false)
                MoveDirection.WEST -> wrap(position, side6, direction, side1, MoveDirection.SOUTH, false)
            }

            else -> error("position $position not supported")
        }
    }
}
