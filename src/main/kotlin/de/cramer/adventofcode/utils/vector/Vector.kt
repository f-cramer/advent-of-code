package de.cramer.adventofcode.utils.vector

data class Vector(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Vector): Vector = Vector(x + other.x, y + other.y)

    operator fun minus(other: Vector): Vector = Vector(x - other.x, y - other.y)

    operator fun times(factor: Int): Vector = Vector(x * factor, y * factor)

    fun north(): Vector = copy(y = y - 1)

    fun east(): Vector = copy(x = x + 1)

    fun south(): Vector = copy(y = y + 1)

    fun west(): Vector = copy(x = x - 1)

    companion object {
        val ZERO = Vector(0, 0)
    }
}

operator fun Int.times(vector: Vector): Vector = vector * this

fun List<List<*>>.vectors(): Sequence<Vector> = sequence {
    for (y in indices) {
        for (x in this@vectors[y].indices) {
            yield(Vector(x, y))
        }
    }
}

fun List<List<*>>.isValidIndex(point: Vector): Boolean = point.y in indices && point.x in this[point.y].indices

operator fun <T> List<List<T>>.get(point: Vector): T = this[point.y][point.x]

operator fun <T> List<MutableList<T>>.set(point: Vector, value: T) {
    this[point.y][point.x] = value
}
