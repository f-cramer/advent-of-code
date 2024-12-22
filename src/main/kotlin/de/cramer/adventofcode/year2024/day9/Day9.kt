package de.cramer.adventofcode.year2024.day9

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 1928L)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 2858L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: IntArray): Long {
    val blocks = input.parseFileSystem().allBlocks

    var start = 0
    var end = blocks.lastIndex
    while (true) {
        var block = blocks[start]
        while (block >= 0) {
            start++
            block = blocks[start]
        }

        block = blocks[end]
        while (block < 0) {
            end--
            block = blocks[end]
        }

        if (start >= end) {
            break
        }

        blocks[start] = blocks[end]
        blocks[end] = -1
    }

    return blocks.foldIndexed(0L) { index, acc, i ->
        if (i >= 0) acc + (index * i).toLong() else acc
    }
}

private fun problem02(input: IntArray): Long {
    val (_, originalFiles, originalFreeSpaces) = input.parseFileSystem()

    val files = originalFiles.toSortedMap()
    val freeSpaces = originalFreeSpaces.toSortedSet(compareBy { it.startIndex })
    files.keys.toSortedSet(reverseOrder()).forEach { id ->
        val fileSpace = files.getValue(id)
        val freeSpaceToMoveTo = freeSpaces.firstOrNull {
            it.startIndex < fileSpace.startIndex && it.size >= fileSpace.size
        } ?: return@forEach

        freeSpaces -= freeSpaceToMoveTo
        files[id] = Space(freeSpaceToMoveTo.startIndex, freeSpaceToMoveTo.startIndex + fileSpace.size - 1)
        if (freeSpaceToMoveTo.size > fileSpace.size) {
            freeSpaces += Space(freeSpaceToMoveTo.startIndex + fileSpace.size, freeSpaceToMoveTo.endIndex)
        }
    }

    return files.entries.sumOf { (id, space) ->
        (space.startIndex..space.endIndex).sumOf {
            it.toLong() * id
        }
    }
}

private fun IntArray.parseFileSystem(): FileSystem {
    val blocks = mutableListOf<Int>()
    val files = mutableMapOf<Int, Space>()
    val freeSpaces = mutableListOf<Space>()
    forEachIndexed { index, i ->
        if (index % 2 == 0) {
            val fileId = index / 2
            files[fileId] = Space(blocks.size, blocks.size + i - 1)
            for (j in 0..<i) {
                blocks += fileId
            }
        } else {
            freeSpaces += Space(blocks.size, blocks.size + i - 1)
            for (j in 0..<i) {
                blocks += -1
            }
        }
    }
    return FileSystem(blocks, files, freeSpaces)
}

private fun String.parse(): IntArray {
    return toCharArray()
        .map { it.digitToInt() }
        .toIntArray()
}

private data class FileSystem(
    val allBlocks: MutableList<Int>,
    val files: MutableMap<Int, Space>,
    val freeSpaces: MutableList<Space>,
)

private data class Space(
    val startIndex: Int,
    val endIndex: Int,
) {
    val size: Int
        get() = endIndex - startIndex + 1
}
