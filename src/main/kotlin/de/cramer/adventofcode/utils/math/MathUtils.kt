package de.cramer.adventofcode.utils.math

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
