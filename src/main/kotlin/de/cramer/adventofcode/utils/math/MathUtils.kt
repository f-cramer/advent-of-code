package de.cramer.adventofcode.utils.math

import kotlin.math.max

val IntRange.size: Int
    get() = (last - first + 1).coerceAtLeast(0)

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

fun lcm(a: Long, b: Long): Long {
    val larger = max(a, b)
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun lcm(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1..<numbers.size) {
        result = lcm(result, numbers[i])
    }
    return result
}

fun lcm(vararg numbers: Long): Long = lcm(numbers.toList())
