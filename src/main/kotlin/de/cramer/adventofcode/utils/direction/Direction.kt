package de.cramer.adventofcode.utils.direction

import de.cramer.adventofcode.utils.vector.Vector

enum class Direction(
    val vector: Vector,
) {
    UP(Vector(0, -1)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0)),
}

fun Direction.turnLeft(): Direction = when (this) {
    Direction.UP -> Direction.LEFT
    Direction.DOWN -> Direction.RIGHT
    Direction.LEFT -> Direction.DOWN
    Direction.RIGHT -> Direction.UP
}

fun Direction.turnRight(): Direction = when (this) {
    Direction.UP -> Direction.RIGHT
    Direction.DOWN -> Direction.LEFT
    Direction.LEFT -> Direction.UP
    Direction.RIGHT -> Direction.DOWN
}

fun Direction.opposite(): Direction = when (this) {
    Direction.UP -> Direction.DOWN
    Direction.DOWN -> Direction.UP
    Direction.LEFT -> Direction.RIGHT
    Direction.RIGHT -> Direction.LEFT
}
