package de.cramer.adventofcode.year2020.day21

import de.cramer.adventofcode.checkTestResult
import de.cramer.adventofcode.readInput
import de.cramer.adventofcode.readTestInput
import de.cramer.adventofcode.runProblem01
import de.cramer.adventofcode.runProblem02

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 5)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, "mxmxvkd,sqjhc,fvjkl")

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: List<Food>): Int {
    val candidates = input.getAllergenCanidates()
    val allCandidateIngredients = candidates.values.flatten().toSet()
    val ingredientsWithoutAllergens = input.flatMap { it.ingredients }
        .filter { it !in allCandidateIngredients }
    return input.flatMap { it.ingredients }
        .count { it in ingredientsWithoutAllergens }
}

private fun problem02(input: List<Food>): String {
    val allergenCanidates = input.getAllergenCanidates()
        .mapValues { (_, v) -> v.toMutableSet() }
        .toMutableMap()
    val ingredientsByAllergen = buildMap {
        while (allergenCanidates.isNotEmpty()) {
            val (allergen, ingredients) = allergenCanidates.entries.first { (_, v) -> v.size == 1 }
                .also { (k, _) -> allergenCanidates.remove(k) }
            val ingredient = ingredients.single()
            this[allergen] = ingredient
            allergenCanidates.forEach { (_, v) -> v -= ingredient }
        }
    }

    return ingredientsByAllergen.entries
        .sortedBy { (k, _) -> k.value }
        .joinToString(separator = ",") { it.value.value }
}

private fun String.parse(): List<Food> {
    fun String.toFood(): Food {
        val (ingredients, allergens) = split(" (contains ")
        return Food(
            ingredients.split(" ").map { Ingredient(it) }.toSet(),
            allergens.substring(0, allergens.length - 1).split(", ").map { Allergen(it) }.toSet(),
        )
    }

    return lineSequence()
        .map { it.toFood() }
        .toList()
}

private fun List<Food>.getAllergenCanidates() = asSequence()
    .flatMap { f -> f.allergens.map { i -> i to f.ingredients } }
    .groupingBy { it.first }
    .aggregate<Pair<Allergen, Set<Ingredient>>, Allergen, Set<Ingredient>> { _, accumulator, element, first ->
        if (first || accumulator == null) {
            element.second
        } else {
            accumulator intersect element.second
        }
    }

private data class Food(
    val ingredients: Set<Ingredient>,
    val allergens: Set<Allergen>,
)

@JvmInline
private value class Ingredient(val value: String)

@JvmInline
private value class Allergen(val value: String)
