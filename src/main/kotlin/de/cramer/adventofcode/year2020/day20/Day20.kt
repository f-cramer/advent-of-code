package de.cramer.adventofcode.year2020.day20

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 20899048083289)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 273)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<List<Pair<Int, Tile>>>): Long {
    val edge1 = input.first().run { first().first.toLong() * last().first.toLong() }
    val edge2 = input.last().run { first().first.toLong() * last().first.toLong() }
    return edge1 * edge2
}

private fun problem02(input: List<List<Pair<Int, Tile>>>): Int {
    val bitmap = buildList {
        for (tiles in input) {
            val iy = size
            for ((_, tile) in tiles) {
                for (y in 1 until tile.height - 1) {
                    val irow = getOrElse(iy + y - 1) { StringBuilder().also(this::add) }
                    for (x in 1 until tile.width - 1) irow.append(if (tile[x, y]) '#' else '.')
                }
            }
        }
    }
    val dragons = BasicTile(
        """
            ..................#.
            #....##....##....###
            .#..#..#..#..#..#...
        """.trimIndent().lines()
    ).variants()
    for ((y, row) in bitmap.withIndex()) {
        for (x in row.indices) {
            for (dragon in dragons) {
                val any = lazy {
                    (0 until dragon.height).any { dy ->
                        val line = bitmap[y + dy]
                        x + dragon.width >= line.length || (0 until dragon.width).any { dx ->
                            dragon[dx, dy] && line[x + dx] == '.'
                        }
                    }
                }
                if (y + dragon.height >= bitmap.size || any.value) {
                    continue
                }

                for (dy in 0 until dragon.height) {
                    val line = bitmap[y + dy]
                    for (dx in 0 until dragon.width) {
                        if (dragon[dx, dy]) {
                            line[x + dx] = 'O'
                        }
                    }
                }
            }
        }
    }
    return bitmap.sumOf { l -> l.count { it == '#' } }
}

private fun String.parse(): List<List<Pair<Int, Tile>>> {
    val tiles = mutableMapOf<Int, BasicTile>().apply {
        val lineIterator = lines().iterator()
        for (title in lineIterator) {
            require(title.substring(0, 5) == "Tile " && title.last() == ':')
            this[title.substring(5, title.length - 1).toInt()] = BasicTile(
                lineIterator.asSequence().takeWhile { it.isNotEmpty() }.toList()
            )
        }
    }
    val borders = mutableMapOf<Int, MutableSet<Int>>().apply {
        for ((id, basicTile) in tiles) {
            for (tile in basicTile.variants()) {
                getOrPut(tile.top) { mutableSetOf() }.add(id)
            }
        }
    }
    return assembleImage(tiles, borders)
}

private sealed interface Tile {
    val width: Int
    val height: Int

    operator fun get(x: Int, y: Int): Boolean

    val top: Int
        get() = (0 until width).fold(0) { acc, x -> (acc shl 1) or if (this[x, 0]) 1 else 0 }
    val left: Int
        get() = (0 until height).fold(0) { acc, y -> (acc shl 1) or if (this[0, y]) 1 else 0 }
    val bottom: Int
        get() = (0 until width).fold(0) { acc, x -> (acc shl 1) or if (this[x, height - 1]) 1 else 0 }
    val right: Int
        get() = (0 until height).fold(0) { acc, y -> (acc shl 1) or if (this[width - 1, y]) 1 else 0 }

    fun variants(): List<Tile> = listOf(
        this,
        TransposeTile(this),
        FlipTile(this),
        TransposeTile(FlipTile(this)),
        FlipTile(TransposeTile(this)),
        TransposeTile(FlipTile(TransposeTile(this))),
        FlipTile(TransposeTile(FlipTile(this))),
        TransposeTile(FlipTile(TransposeTile(FlipTile(this)))),
    )
}

private class BasicTile(
    override val width: Int,
    override val height: Int,
    private val bits: BooleanArray,
) : Tile {
    init {
        require(bits.size == width * height)
    }

    constructor(lines: List<String>) : this(
        width = lines.sumOf { it.length } / lines.size,
        height = lines.size,
        bits = lines.flatMap { row -> row.map { it == '#' } }.toBooleanArray(),
    )

    override operator fun get(x: Int, y: Int): Boolean = bits[x + y * width]
}

private class FlipTile(private val tile: Tile) : Tile {
    override val width: Int
        get() = tile.width
    override val height: Int
        get() = tile.height

    override operator fun get(x: Int, y: Int): Boolean = tile[x, height - 1 - y]
}

private class TransposeTile(private val tile: Tile) : Tile {
    override val width: Int
        get() = tile.height
    override val height: Int
        get() = tile.width

    override operator fun get(x: Int, y: Int): Boolean = tile[y, x]
}

private fun assembleImage(
    tiles: Map<Int, Tile>,
    borders: Map<Int, Set<Int>>,
): List<List<Pair<Int, Tile>>> {
    val unused = tiles.keys.toMutableSet()
    val destination = mutableListOf(mutableListOf<Pair<Int, Tile>>())

    fun impl(): Boolean {
        if (unused.isEmpty()) {
            return true
        }

        val lastRow = destination.last()

        if (destination.size < 2 || destination.first().size > lastRow.size) {
            val left = lastRow.lastOrNull()?.second?.right
            val top = destination.getOrNull(destination.lastIndex - 1)?.get(lastRow.size)?.second?.bottom
            val candidates = when {
                left != null -> borders[left]
                top != null -> borders[top]
                else -> tiles.keys
            }?.filter { it in unused }.orEmpty()

            for (id in candidates) {
                if (!unused.remove(id)) {
                    continue
                }

                for (variant in tiles.getValue(id).variants()) {
                    if (left != null && variant.left != left || top != null && variant.top != top) {
                        continue
                    }

                    lastRow += id to variant
                    if (impl()) {
                        return true
                    }
                    lastRow.removeLast()
                }
                unused += id
            }
        }

        if (destination.size == 1 && lastRow.isNotEmpty() || destination.size > 1 && destination.first().size <= lastRow.size) {
            val nextRow = mutableListOf<Pair<Int, Tile>>().also(destination::add)
            val top = lastRow.first().second.bottom
            val candidates = borders[top]?.filter { it in unused }.orEmpty()
            for (id in candidates) {
                if (!unused.remove(id)) {
                    continue
                }
                for (variant in tiles.getValue(id).variants()) {
                    if (variant.top != top) {
                        continue
                    }

                    nextRow += id to variant
                    if (impl()) {
                        return true
                    }

                    nextRow.removeLast()
                }
                unused += id
            }
            destination.removeLast()
        }

        return false
    }

    require(impl())
    return destination
}
