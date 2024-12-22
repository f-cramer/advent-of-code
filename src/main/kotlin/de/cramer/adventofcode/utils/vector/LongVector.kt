package de.cramer.adventofcode.utils.vector

data class LongVector(
    val x: Long,
    val y: Long,
) {
    operator fun plus(other: LongVector): LongVector = LongVector(x + other.x, y + other.y)

    operator fun minus(other: LongVector): LongVector = LongVector(x - other.x, y - other.y)

    operator fun times(factor: Int): LongVector = LongVector(x * factor, y * factor)

    operator fun times(factor: Long): LongVector = LongVector(x * factor, y * factor)

    fun north(): LongVector = copy(y = y - 1)

    fun east(): LongVector = copy(x = x + 1)

    fun south(): LongVector = copy(y = y + 1)

    fun west(): LongVector = copy(x = x - 1)

    companion object {
        val ZERO = LongVector(0, 0)
    }
}

operator fun Long.times(vector: LongVector): LongVector = vector * this

fun Vector.toLong(): LongVector = LongVector(x.toLong(), y.toLong())
