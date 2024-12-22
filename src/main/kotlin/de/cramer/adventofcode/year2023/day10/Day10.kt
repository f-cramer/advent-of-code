package de.cramer.adventofcode.year2023.day10

import de.cramer.adventofcode.utils.Vector
import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.get
import de.cramer.adventofcode.utils.isValidIndex
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.readTestInput2
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.vectors
import java.util.ArrayDeque
import kotlin.math.abs

fun main() {
    val testInput = readTestInput().parse()
    val testInput2 = readTestInput2().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 8)
    val testResult02 = problem02(testInput2)
    checkTestResult(testResult02, 10)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(map: List<List<Pipe>>): Int {
    val steps = getPath(map).count()
    return steps / 2
}

private fun problem02(map: List<List<Pipe>>): Int {
    val path = getPath(map).toList()

    fun Edge.isOutside(): Boolean = map.isValid(this) && run {
        val firstIndex = path.indexOf(northWest)
        val secondIndex = path.indexOf(southEast)
        val result = firstIndex < 0 || secondIndex < 0 || (abs(firstIndex - secondIndex) != 1 && abs(firstIndex - secondIndex) != path.size - 1)
        return result
    }

    fun floodFill(edge: Edge): Set<Edge> = buildSet {
        val q = ArrayDeque<Edge>()
        fun addIfValid(position: Edge) {
            if (map.isValid(position) && position !in this && position !in q) {
                q.addLast(position)
            }
        }

        q.addLast(edge)
        while (q.isNotEmpty()) {
            val n = q.removeFirst()
            if (n.isOutside()) {
                this += n
                n.getAdjacentEdges().forEach {
                    addIfValid(it)
                }
            }
        }
    }

    val filled = floodFill(Edge(Vector(-1, 0), Vector.ZERO)).asSequence()
        .flatMap { listOf(it.northWest, it.southEast) }
        .filter { map.isValidIndex(it) }
        .toSet()
    val innerTiles = map.vectors().toList() - path.toSet() - filled
    return innerTiles.size
}

private fun String.parse(): List<List<Pipe>> {
    return lineSequence()
        .map { l -> l.map { it.toPipe() } }
        .toList()
}

private fun getPath(map: List<List<Pipe>>): Sequence<Vector> {
    return sequence {
        val start = map.vectors().find { map[it] == Pipe.START }!!
        yield(start)
        var last: Vector? = null
        var current = start
        var firstStep = true
        while (firstStep || map[current] != Pipe.START) {
            firstStep = false
            val tmpCurrent = current
            current = map[current].getConnections(current, map).toList()
                .first { it != last }
            last = tmpCurrent
            if (map[current] != Pipe.START) {
                yield(current)
            }
        }
    }
}

private fun Char.toPipe(): Pipe = when (this) {
    '|' -> Pipe.VERTICAL
    '-' -> Pipe.HORIZONTAL
    'L' -> Pipe.NORTH_EAST
    'J' -> Pipe.NORTH_WEST
    '7' -> Pipe.SOUTH_WEST
    'F' -> Pipe.SOUTH_EAST
    '.' -> Pipe.NONE
    'S' -> Pipe.START
    else -> error("cannot convert $this to Pipe")
}

private enum class Pipe {
    VERTICAL {
        override fun getConnections(position: Vector, map: List<List<Pipe>>) =
            position.north() to position.south()
    },
    HORIZONTAL {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> =
            position.west() to position.east()
    },
    NORTH_EAST {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> =
            position.north() to position.east()
    },
    NORTH_WEST {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> =
            position.north() to position.west()
    },
    SOUTH_WEST {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> =
            position.south() to position.west()
    },
    SOUTH_EAST {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> =
            position.south() to position.east()
    },
    START {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> {
            val possibleConnections = listOf(
                position.north(),
                position.east(),
                position.south(),
                position.west(),
            )
            val (first, second) = possibleConnections
                .filter {
                    if (map.isValidIndex(it)) {
                        val pipe = map[it]
                        if (pipe != NONE) {
                            return@filter true
                        }
                    }
                    false
                }
                .filter {
                    val (first, second) = map[it].getConnections(it, map)
                    first == position || second == position
                }
            return first to second
        }
    },
    NONE {
        override fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector> =
            throw UnsupportedOperationException()
    },
    ;

    abstract fun getConnections(position: Vector, map: List<List<Pipe>>): Pair<Vector, Vector>
}

private data class Edge(
    val northWest: Vector,
    val southEast: Vector,
) {
    init {
        require(
            northWest.x == southEast.x && southEast.y - northWest.y == 1 ||
                northWest.y == southEast.y && southEast.x - northWest.x == 1,
        ) {
            "not a valid edge"
        }
    }

    fun getAdjacentEdges(): Set<Edge> {
        return if (northWest.x == southEast.x) {
            setOf(
                Edge(northWest.west(), northWest),
                Edge(northWest.west(), southEast.west()),
                Edge(southEast.west(), southEast),
                Edge(northWest, northWest.east()),
                Edge(northWest.east(), southEast.east()),
                Edge(southEast, southEast.east()),
            )
        } else if (northWest.y == southEast.y) {
            setOf(
                Edge(northWest.north(), northWest),
                Edge(northWest.north(), southEast.north()),
                Edge(southEast.north(), southEast),
                Edge(northWest, northWest.south()),
                Edge(northWest.south(), southEast.south()),
                Edge(southEast, southEast.south()),
            )
        } else {
            emptySet()
        }
    }
}

private fun List<List<*>>.isValid(edge: Edge): Boolean =
    isValidIndex(edge.northWest) || isValidIndex(edge.southEast)
