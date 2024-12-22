package de.cramer.adventofcode.utils

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.time.measureTimedValue

private val numberFormatter = DecimalFormat.getNumberInstance()

fun readInput() = readFile("input")

fun readTestInput() = readFile("input-test")

fun readTestInput2() = readFile("input-test-2")

fun <T> checkTestResult(actual: T, expected: T) {
    require(actual == expected) { "wrong test result: is $actual, but should have been $expected" }
}

fun checkTestResult(actual: Int, expected: Int) {
    require(actual == expected) { "wrong test result: is ${numberFormatter.format(actual)}, but should have been ${numberFormatter.format(expected)} (off by ${numberFormatter.format(abs(expected - actual))} or ${String.format("%.3f", abs(expected - actual) / expected.toDouble() * 100)}%)" }
}

fun checkTestResult(actual: Long, expected: Long) {
    require(actual == expected) { "wrong test result: is ${numberFormatter.format(actual)}, but should have been ${numberFormatter.format(expected)} (off by ${numberFormatter.format(abs(expected - actual))} or ${String.format("%.3f", abs(expected - actual) / expected.toDouble() * 100)}%)" }
}

fun runProblem01(test: () -> Any) {
    runTest("result01", test)
}

fun runProblem02(test: () -> Any) {
    runTest("result02", test)
}

fun <T> runTest(name: String, test: () -> T) {
    val (result, duration) = measureTimedValue(test)
    println("$name = $result (in $duration)")
}

fun String.splitByEmptyLines(numberOfEmptyLines: Int = 1): List<String> =
    split(multipleEoL(numberOfEmptyLines + 1))

private fun readFile(fileName: String): String {
    val stackTrace = Thread.currentThread().stackTrace
    val thisPackageName = getPackageName(stackTrace[1].className)
    val callerPackageName = getCallerPackageName(thisPackageName, stackTrace)

    val path = "$callerPackageName/$fileName"
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
        ?: error("could not find file at classpath resource $path")

    return stream.use {
        stream.reader().readText().trimEnd()
    }
}

private fun getCallerPackageName(thisPackageName: String, stackTrace: Array<StackTraceElement>): String {
    for (i in 2..<stackTrace.size) {
        val stackTracePackageName = getPackageName(stackTrace[i].className)
        if (stackTracePackageName != thisPackageName) {
            return stackTracePackageName
        }
    }

    @Suppress("ThrowingExceptionsWithoutMessageOrCause")
    val cause = Exception()
    cause.stackTrace = stackTrace
    throw IllegalStateException("could not find caller package name for causes stack trace", cause)
}

private fun getPackageName(className: String) =
    className.substringBeforeLast(".").replace(".", "/")

private val eol = Regex("""\r\n|\r|\n""")

@Suppress("RegExpSimplifiable")
private fun multipleEoL(count: Int) = "(?:${eol.pattern}){$count}+".toRegex()

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

val IntRange.size: Int
    get() = (last - first).coerceAtLeast(0)

fun <T, U> Collection<T>.cartesianProduct(other: Collection<U>): Sequence<Pair<T, U>> = sequence {
    forEach { t ->
        other.forEach { u ->
            yield(t to u)
        }
    }
}

fun <T, U> Sequence<T>.cartesianProduct(other: Sequence<U>): Sequence<Pair<T, U>> = flatMap { t ->
    other.map { u -> t to u }
}

fun <T, U> Sequence<T>.cartesianProduct(other: Collection<U>): Sequence<Pair<T, U>> = cartesianProduct(other.asSequence())

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
