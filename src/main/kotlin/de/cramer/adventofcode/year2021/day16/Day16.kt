package de.cramer.adventofcode.year2021.day16

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import java.util.BitSet
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 31)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 54)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: BitSet): Int {
    fun Packet.getVersionScore(): Int = when (this) {
        is LiteralValuePacket -> version
        is OperatorPacket -> version + subPackets.sumOf { it.getVersionScore() }
    }

    val packet = BitBuffer(input).parsePacket()
    return packet.getVersionScore()
}

private fun problem02(input: BitSet): Long {
    val operators = mapOf<Int, (Long, Long) -> Long>(
        0 to { a, b -> a + b },
        1 to { a, b -> a * b },
        2 to { a, b -> min(a, b) },
        3 to { a, b -> max(a, b) },
        5 to { a, b -> if (a > b) 1 else 0 },
        6 to { a, b -> if (a < b) 1 else 0 },
        7 to { a, b -> if (a == b) 1 else 0 },
    )

    fun Packet.getScore(): Long = when (this) {
        is LiteralValuePacket -> value
        is OperatorPacket -> when (val operator = operators[typeId]) {
            null -> error("cannot find operator for type id $typeId")
            else -> subPackets.drop(1).fold(subPackets[0].getScore()) { acc, packet -> operator(acc, packet.getScore()) }
        }
    }

    val packet = BitBuffer(input).parsePacket()
    return packet.getScore()
}

private fun String.parse(): BitSet {
    val result = BitSet(length * 4)
    var index = 0
    forEach {
        val converted = it.digitToInt(16).toString(2).padStart(4, '0')
        result[index++] = converted[0] == '1'
        result[index++] = converted[1] == '1'
        result[index++] = converted[2] == '1'
        result[index++] = converted[3] == '1'
    }
    return result
}

private data class BitBuffer(
    val bits: BitSet,
    var nextIndex: Int = 0,
) {
    fun get(): Boolean = bits[nextIndex++]

    operator fun get(length: Int): Int {
        var result = 0
        for (i in 1..length) {
            result += if (get()) 1.shl(length - i) else 0
        }
        return result
    }
}

private fun BitBuffer.parsePacket(): Packet {
    val version = this[3]
    return when (val typeId = this[3]) {
        4 -> parseLiteralValuePacket(version)
        else -> parseOperatorPacket(version, typeId)
    }
}

private fun BitBuffer.parseLiteralValuePacket(version: Int): LiteralValuePacket {
    operator fun Int.get(bitIndex: Int): Boolean = this.shr(bitIndex).and(1) == 1

    var result = 0L
    var firstIteration = true
    do {
        val word = this[5]
        if (!firstIteration) {
            result = result shl 4
        } else {
            firstIteration = false
        }
        result += word and 0xF
    } while (word[4])
    return LiteralValuePacket(version, result)
}

private fun BitBuffer.parseOperatorPacket(version: Int, typeId: Int): Packet {
//    println("version = $version")
//    println("typeId = $typeId")

    val lengthTypeId = get()
    val subPackets = if (lengthTypeId) {
        val numberOfSubPackets = this[11]
//        println("numberOfSubPackets = $numberOfSubPackets")
        buildList {
            repeat(numberOfSubPackets) {
                this += parsePacket()
            }
        }
    } else {
        val subPacketLength = this[15]
//        println("subPacketLength = $subPacketLength")
        val index = nextIndex
        buildList {
            while (nextIndex < index + subPacketLength) {
                this += parsePacket()
            }
        }.also {
            require(nextIndex == index + subPacketLength)
        }
    }
    return OperatorPacket(version, typeId, subPackets)
}

private sealed interface Packet {
    val version: Int
    val typeId: Int
}

private data class LiteralValuePacket(
    override val version: Int,
    val value: Long,
) : Packet {
    override val typeId: Int = 4
}

private data class OperatorPacket(
    override val version: Int,
    override val typeId: Int,
    val subPackets: List<Packet>,
) : Packet
