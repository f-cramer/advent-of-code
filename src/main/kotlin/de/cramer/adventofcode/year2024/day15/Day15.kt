package de.cramer.adventofcode.year2024.day15

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.get
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02
import de.cramer.adventofcode.set
import de.cramer.adventofcode.splitByEmptyLines
import de.cramer.adventofcode.vectors

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 10092)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 9021)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    var robot = input.robot
    val map = input.map.map { it.toMutableList() }.toMutableList()

    for (instruction in input.instructions) {
        val nextPosition = robot + instruction.vector
        when (map[nextPosition]) {
            Tile.EMPTY -> {
                map[robot] = Tile.EMPTY
                robot = nextPosition
            }

            Tile.WALL -> {
                // do nothing
            }

            Tile.BOX -> {
                var nextBoxPosition = nextPosition + instruction.vector
                while (map[nextBoxPosition] == Tile.BOX) {
                    nextBoxPosition += instruction.vector
                }

                when (map[nextBoxPosition]) {
                    Tile.EMPTY -> {
                        map[robot] = Tile.EMPTY
                        map[nextPosition] = Tile.EMPTY
                        map[nextBoxPosition] = Tile.BOX
                        robot = nextPosition
                    }

                    Tile.WALL -> {
                        // do nothing
                    }

                    Tile.BOX -> error("cannot happen")
                }
            }
        }
    }

    fun List<List<Tile>>.sumOfBoxesGpsCoordinates(): Int {
        return vectors()
            .filter { this[it] == Tile.BOX }
            .sumOf { it.x + it.y * 100 }
    }
    return map.sumOfBoxesGpsCoordinates()
}

private fun problem02(input: Input): Int {
    val map = input.map.flatMapIndexed { y, line ->
        line.flatMapIndexed inner@{ x, tile ->
            val newTileType = when (tile) {
                Tile.EMPTY -> return@inner emptyList()
                Tile.BOX -> WideTileType.BOX
                Tile.WALL -> WideTileType.WALL
            }
            listOf(WideTile(newTileType, Vector(2 * x, y)))
        }
    }.associateBy { it.position }.toMutableMap()

    var robot = input.robot.copy(x = 2 * input.robot.x)

    for (instruction in input.instructions) {
        val nextPosition = robot + instruction.vector
        val nextPositionTile = map.find(nextPosition)
        if (nextPositionTile == null) {
            robot = nextPosition
        } else {
            when (nextPositionTile.type) {
                WideTileType.WALL -> {
                    // do nothing
                }

                WideTileType.BOX -> {
                    val boxBasePosition = nextPositionTile.position
                    val positionsToMove: Set<Vector> = buildSet {
                        this += boxBasePosition
                        val stack = ArrayDeque<Vector>()
                        stack += boxBasePosition + instruction.vector
                        stack += boxBasePosition + Vector(1, 0) + instruction.vector

                        while (stack.isNotEmpty()) {
                            val position = stack.removeFirst()
                            if (position in this) {
                                continue
                            }

                            val tile = map.find(position) ?: continue
                            when (tile.type) {
                                WideTileType.WALL -> {
                                    clear()
                                    return@buildSet
                                }

                                WideTileType.BOX -> {
                                    val basePosition = if (position in map) position else position - Vector(1, 0)
                                    this += basePosition
                                    val nextBasePosition = basePosition + instruction.vector
                                    if (nextBasePosition != position) {
                                        stack += nextBasePosition
                                    }
                                    val nextEndPosition = basePosition + Vector(1, 0) + instruction.vector
                                    if (nextEndPosition != position) {
                                        stack += nextEndPosition
                                    }
                                }
                            }
                        }
                    }

                    if (positionsToMove.isNotEmpty()) {
                        val sortedPositions = positionsToMove.sortedByDescending { it.x * instruction.vector.x + it.y * instruction.vector.y }
                        robot = nextPosition
                        for (position in sortedPositions) {
                            map -= position
                            val newPosition = position + instruction.vector
                            map[newPosition] = WideTile(WideTileType.BOX, newPosition)
                        }
                    }
                }
            }
        }
    }

    fun Map<Vector, WideTile>.sumOfBoxesGpsCoordinates(): Int {
        return values.asSequence()
            .filter { it.type == WideTileType.BOX }
            .map { it.position }
            .sumOf { it.x + it.y * 100 }
    }
    return map.sumOfBoxesGpsCoordinates()
}

private fun Map<Vector, WideTile>.find(position: Vector): WideTile? = this[position] ?: this[position.copy(x = position.x - 1)]

private fun String.parse(): Input {
    val (mapString, instructionString) = splitByEmptyLines()

    fun Char.parseTile(): Tile = when (this) {
        '.' -> Tile.EMPTY
        'O' -> Tile.BOX
        '#' -> Tile.WALL
        else -> error("cannot parse $this to tile")
    }

    var robot: Vector = Vector.ZERO
    val map = mapString.lines()
        .mapIndexed { y, l ->
            l.mapIndexed { x, c ->
                if (c == '@') {
                    robot = Vector(x, y)
                    Tile.EMPTY
                } else {
                    c.parseTile()
                }
            }
        }

    fun Char.parseDirection(): Direction = when (this) {
        '^' -> Direction.UP
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        '>' -> Direction.RIGHT
        else -> error("cannot parse $this to direction")
    }

    val instructions = instructionString.replace("""\s+""".toRegex(), "")
        .map { it.parseDirection() }

    return Input(robot, map, instructions)
}

private data class Input(
    val robot: Vector,
    val map: List<List<Tile>>,
    val instructions: List<Direction>,
)

private enum class Tile {
    EMPTY,
    BOX,
    WALL,
}

private enum class Direction(
    val vector: Vector,
) {
    UP(Vector(0, -1)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
}

private data class WideTile(
    val type: WideTileType,
    val position: Vector,
)

private enum class WideTileType {
    BOX,
    WALL,
}
