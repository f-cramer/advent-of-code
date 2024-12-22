package de.cramer.adventofcode.year2022.day7

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import kotlin.math.max

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 95437)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 24933642)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Directory): Long {
    return input.allSubDirectories().asSequence()
        .map { it.totalSize() }
        .filter { it < 100000 }
        .sum()
}

private fun problem02(input: Directory): Long {
    val totalDiskSpace = 70000000
    val neededDiskSpaceForUpdate = 30000000
    val freeDiskSpace = totalDiskSpace - input.totalSize()
    val diskSpaceToFree = max(0, neededDiskSpaceForUpdate - freeDiskSpace)

    return input.allSubDirectories().asSequence()
        .map { it.totalSize() }
        .filter { it >= diskSpaceToFree }
        .min()
}

private fun String.parse(): Directory {
    val lines = lineSequence()
        .map { it.parseLine() }
        .toList().listIterator()

    var currentDirectory: Directory? = null
    fun requireCurrentDirectory(): Directory =
        currentDirectory ?: error("current directory not set")

    lines.forEach {
        when (it) {
            is Cd -> {
                currentDirectory = when (val directoryName = it.directoryName) {
                    "/" -> Directory("/", null)
                    ".." -> requireCurrentDirectory().parent ?: error("current directory does not have a parent")
                    else -> requireCurrentDirectory().directories.firstOrNull { d -> d.name == directoryName }
                }
            }

            is Ls -> {
                val directory = requireCurrentDirectory()
                directory.directories.clear()
                directory.files.clear()
                while (lines.hasNext()) {
                    when (val next = lines.next()) {
                        is DirectoryName -> directory.directories += Directory(next.directoryName, directory)
                        is File -> directory.files += next
                        else -> {
                            lines.previous()
                            break
                        }
                    }
                }
            }

            else -> error("cannot parse $it on its own")
        }
    }

    var directory = requireCurrentDirectory()
    while (directory.parent != null) {
        directory = directory.parent!!
    }
    return directory
}

private val fileLine = """(\d+) (.+)""".toRegex()

private fun String.parseLine(): Line {
    fun String.parseCommand(): Command = when {
        startsWith("cd") -> Cd(substring(2).trim())
        startsWith("ls") -> Ls
        else -> error("cannot parse $this to command")
    }

    return when {
        startsWith('$') -> substring(1).trim().parseCommand()
        startsWith("dir") -> DirectoryName(substring(3).trim())
        else -> {
            val (_, size, name) = fileLine.matchEntire(this)?.groupValues ?: error("cannot parse $this to line")
            File(name, size.toLong())
        }
    }
}

private class Directory(
    val name: String,
    val parent: Directory?,
    val directories: MutableList<Directory> = mutableListOf(),
    val files: MutableList<File> = mutableListOf(),
) {
    override fun toString(): String {
        return "Directory(name=$name)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Directory

        if (name != other.name) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }
}

private fun Directory.allSubDirectories(): List<Directory> {
    val allDirectories = mutableListOf<Directory>()
    fun Directory.fillDirectories() {
        allDirectories += this
        directories.forEach { it.fillDirectories() }
    }
    fillDirectories()
    return allDirectories
}

private fun Directory.totalSize(): Long =
    files.sumOf { it.size } + directories.sumOf { it.totalSize() }

private data class DirectoryName(
    val directoryName: String,
) : Line

private data class File(
    val name: String,
    val size: Long,
) : Line

private sealed interface Command : Line

private data class Cd(
    val directoryName: String,
) : Command

private object Ls : Command {
    override fun toString(): String = "Ls"
}

private sealed interface Line
