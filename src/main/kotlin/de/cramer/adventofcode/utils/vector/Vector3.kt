package de.cramer.adventofcode.utils.vector

data class Vector3(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    operator fun plus(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)

    companion object {
        val ZERO = Vector3(0, 0, 0)
    }
}
