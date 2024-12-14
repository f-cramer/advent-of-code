package de.cramer.adventofcode.year2024.day14

import de.cramer.adventofcode.Vector
import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 12)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 1)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    fun List<Vector>.getSafetyFactor(mapSize: Vector): Int {
        val topLeft = mutableListOf<Vector>()
        val topRight = mutableListOf<Vector>()
        val bottomLeft = mutableListOf<Vector>()
        val bottomRight = mutableListOf<Vector>()

        val leftMaxX = (mapSize.x - 1) / 2 - 1
        val rightMinX = leftMaxX + 2
        val topMaxY = (mapSize.y - 1) / 2 - 1
        val bottomMinY = topMaxY + 2

        for (vector in this) {
            if (vector.x <= leftMaxX) {
                if (vector.y <= topMaxY) {
                    topLeft.add(vector)
                } else if (vector.y >= bottomMinY) {
                    bottomLeft.add(vector)
                }
            } else if (vector.x >= rightMinX) {
                if (vector.y <= topMaxY) {
                    topRight.add(vector)
                } else if (vector.y >= bottomMinY) {
                    bottomRight.add(vector)
                }
            }
        }

        return topLeft.size * topRight.size * bottomLeft.size * bottomRight.size
    }

    return input.getPositionsAfterSeconds(100).getSafetyFactor(input.mapSize)
}

private fun problem02(input: Input): Int {
    var i = 1
    val numberOfRobots = input.robots.size
    while (true) {
        val positions = input.getPositionsAfterSeconds(i).toSet()
        if (positions.size == numberOfRobots) {
            return i
        }
        i++
    }
}

private fun Input.getPositionsAfterSeconds(n: Int): List<Vector> {
    return robots.map {
        val x = (it.position.x + it.velocity.x * n).mod(mapSize.x)
        val y = (it.position.y + it.velocity.y * n).mod(mapSize.y)
        Vector(x, y)
    }
}

private fun String.parse(): Input {
    val lineRegex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
    fun String.parseRobot(): Robot {
        val (px, py, vx, vy) = lineRegex.matchEntire(this)!!.destructured
        return Robot(Vector(px.toInt(), py.toInt()), Vector(vx.toInt(), vy.toInt()))
    }

    val lines = lines()
    val (mapSizeX, mapSizeY) = lines.first().split(",")
    val mapSize = Vector(mapSizeX.toInt(), mapSizeY.toInt())
    val robots = lines.drop(1)
        .map { it.parseRobot() }
    return Input(mapSize, robots)
}

private data class Input(
    val mapSize: Vector,
    val robots: List<Robot>,
)

private data class Robot(
    val position: Vector,
    val velocity: Vector,
)

private operator fun Vector.contains(inner: Vector): Boolean = inner.x in 0..<x && inner.y in 0..<y
