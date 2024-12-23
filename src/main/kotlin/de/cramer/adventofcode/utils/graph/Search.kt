package de.cramer.adventofcode.utils.graph

import java.util.PriorityQueue

typealias Neighbours<T> = (T) -> Iterable<T>
typealias Cost<T> = (T, T) -> Int
typealias Heuristic<T> = (T) -> Int

fun <T : Any> findShortestPath(
    start: T,
    isEnd: T.() -> Boolean,
    neighbours: Neighbours<T>,
    cost: Cost<T> = { _, _ -> 1 },
    heuristic: Heuristic<T> = { 0 },
    maximumCost: Int = Int.MAX_VALUE,
): SearchResult<T> {
    val queue = PriorityQueue<Costed<T>>().apply {
        this += Costed(start, 0, heuristic(start))
    }

    var end: T? = null
    val visited = mutableMapOf(start to Seen<T>(0, null))

    while (end == null) {
        if (queue.isEmpty()) {
            error("no path found")
        }

        val (currentVertex, currentCost) = queue.poll()!!
        if (currentCost > maximumCost) continue
        end = if (currentVertex.isEnd()) currentVertex else null

        val next = neighbours(currentVertex)
            .filter { it !in visited }
            .map { next -> Costed(next, currentCost + cost(currentVertex, next), heuristic(next)) }

        queue += next
        visited += next.associate { it.vertex to Seen(it.cost, currentVertex) }
    }

    return SearchResult(start, end, visited)
}

class SearchResult<T : Any>(
    val start: T,
    val end: T,
    private val vertices: Map<T, Seen<T>>,
) {
    val cost by lazy { vertices.getValue(end).cost }
    val path by lazy { getPath(end, emptyList()) }

    private tailrec fun getPath(endVertex: T, pathEnd: List<T>): List<T> {
        val previous = vertices[endVertex]?.previous
        val path = listOf(endVertex) + pathEnd
        return if (previous == null) path else getPath(previous, path)
    }
}

fun <T : Any> findShortestPathToAll(
    start: T,
    neighbours: Neighbours<T>,
    cost: Cost<T> = { _, _ -> 1 },
): MultiPathSearchResult<T> {
    val initial = listOf(Costed(start, 0, 0))
    val visited = findShortestPathToAll(initial, neighbours, cost)
    return MultiPathSearchResult(start, visited)
}

fun <T : Any> findShortestPathToAllFromAny(
    start: Map<T, Int>,
    neighbours: Neighbours<T>,
    cost: Cost<T> = { _, _ -> 1 },
): MultiPathSearchResult<T> {
    val initial = start.map { (k, v) -> Costed(k, v, 0) }
    val visited = findShortestPathToAll(initial, neighbours, cost)
    return MultiPathSearchResult(start.keys, visited)
}

private fun <T : Any> findShortestPathToAll(
    start: List<Costed<T>>,
    neighbours: Neighbours<T>,
    cost: Cost<T>,
): Map<T, Seen<T>> {
    val queue = PriorityQueue<Costed<T>>()
    queue += start
    val visited = start.associateTo(mutableMapOf()) { it.vertex to Seen<T>(it.cost, null) }

    while (queue.isNotEmpty()) {
        val (currentVertex, currentCost) = queue.poll()!!

        val next = neighbours(currentVertex)
            .filter { it !in visited }
            .map { next -> Costed(next, currentCost + cost(currentVertex, next), 0) }

        queue += next
        visited += next.associate { it.vertex to Seen(it.cost, currentVertex) }
    }
    return visited
}

class MultiPathSearchResult<T : Any>(
    val start: Set<T>,
    private val vertices: Map<T, Seen<T>>,
) {
    constructor(start: T, result: Map<T, Seen<T>>) : this(setOf(start), result)

    fun getCost(vertex: T): Int = vertices[vertex]?.cost ?: throw IllegalStateException("Result for $vertex not available")

    fun getPath(end: T) = getPath(end, emptyList())
    fun getVertexInPath(end: T, startCondition: (T?) -> Boolean) = getPathItem(end, startCondition) ?: throw IllegalStateException("No path found")

    fun seen(): Set<T> = vertices.keys

    tailrec fun getStart(endVertex: T): T {
        val previous = vertices[endVertex]?.previous
        return if (previous == null) endVertex else getStart(previous)
    }

    private tailrec fun getPath(endVertex: T, pathEnd: List<T>): List<T> {
        val previous = vertices[endVertex]?.previous
        val p = listOf(endVertex) + pathEnd
        return if (previous == null) p else getPath(previous, p)
    }

    private tailrec fun getPathItem(endVertex: T, startCondition: (T?) -> Boolean = { it == null }): T? {
        val previous = vertices[endVertex]?.previous
        return if (previous == null) {
            null
        } else if (startCondition(previous)) {
            previous
        } else {
            getPathItem(previous, startCondition)
        }
    }
}

data class Seen<T>(val cost: Int, val previous: T?)

private data class Costed<T>(val vertex: T, val cost: Int, val heuristic: Int) : Comparable<Costed<T>> {
    override fun compareTo(other: Costed<T>): Int = (cost + heuristic).compareTo(other.cost + other.heuristic)
}
