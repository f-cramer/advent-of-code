package de.cramer.adventofcode.year2023.day19

import de.cramer.adventofcode.utils.checkTestResult
import de.cramer.adventofcode.utils.math.size
import de.cramer.adventofcode.utils.readInput
import de.cramer.adventofcode.utils.readTestInput
import de.cramer.adventofcode.utils.runProblem01
import de.cramer.adventofcode.utils.runProblem02
import de.cramer.adventofcode.utils.splitByEmptyLines

fun main() {
    val testInput = readTestInput().parse()
    val testResult01 = problem01(testInput)
    checkTestResult(testResult01, 19114)
    val testResult02 = problem02(testInput)
    checkTestResult(testResult02, 167409079868000L)

    val input = readInput().parse()
    runProblem01 { problem01(input) }
    runProblem02 { problem02(input) }
}

private fun problem01(input: Input): Int {
    val workflowsByName = input.workflows.associateBy { it.name }
    return input.parts.sumOf { part ->
        var workflowName = "in"
        while (workflowName != "A" && workflowName != "R") {
            val workflow = workflowsByName.getValue(workflowName)
            workflowName = workflow.rules.firstNotNullOf {
                it.next(part)
            }
        }

        if (workflowName == "A") part.x + part.m + part.a + part.s else 0
    }
}

private fun problem02(input: Input): Long {
    val workflowsByName = input.workflows.associateBy { it.name }

    val queue = ArrayDeque<Pair<RangePart, String>>().apply {
        val range = 1..4000
        this += RangePart(range, range, range, range) to "in"
    }

    val accepted = mutableSetOf<RangePart>()
    while (queue.isNotEmpty()) {
        val (part, workflowName) = queue.removeFirst()
        if (workflowName == "A") {
            accepted += part
            continue
        } else if (workflowName == "R" || !part.isValid) {
            continue
        }

        val workflow = workflowsByName.getValue(workflowName)
        var remainingPart = part
        workflow.rules.forEach { rule ->
            when (val result = rule.apply(remainingPart)) {
                is NextWorkflow -> {
                    queue += remainingPart to result.workflowName
                }

                is Split -> {
                    if (result.workflowPart.isValid) {
                        queue += result.workflowPart to result.workflowName
                    }
                    remainingPart = result.remainingPart
                }
            }
        }
    }

    return accepted.sumOf {
        it.x.size.toLong() * it.m.size * it.a.size * it.s.size
    }
}

private fun String.parse(): Input {
    val partPattern = """\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}""".toRegex()
    fun String.parsePart(): Part = partPattern.find(this)!!.let {
        val (x, m, a, s) = it.destructured
        Part(x.toInt(), m.toInt(), a.toInt(), s.toInt())
    }

    fun String.parseRule(): Rule {
        val separatorIndex = indexOf(':')
        return if (separatorIndex < 0) {
            FallbackRule(this)
        } else {
            val target = substring(separatorIndex + 1)
            val check = substring(0, separatorIndex)
            val accessor = when (check.first()) {
                'x' -> Accessor.X
                'm' -> Accessor.M
                'a' -> Accessor.A
                's' -> Accessor.S
                else -> error("cannot parse ${check.first()}")
            }
            val value = check.substring(2).toInt()

            when (check[1]) {
                '>' -> GreaterThanRule(accessor, value, target)
                '<' -> LessThanRule(accessor, value, target)
                else -> error("cannot parse operator ${check[1]}")
            }
        }
    }

    fun String.parseWorkflow(): Workflow {
        val separatorIndex = indexOf('{')
        val name = substring(0, separatorIndex)
        val rulesStrings = substring(separatorIndex + 1, length - 1).split(',')
        val rules = rulesStrings.map { it.parseRule() }
        return Workflow(name, rules)
    }

    val (workflowsString, partsString) = splitByEmptyLines()
    val parts = partsString.lines()
        .map { it.parsePart() }
    val workflows = workflowsString.lines()
        .map { it.parseWorkflow() }
    return Input(parts, workflows)
}

private data class Input(
    val parts: List<Part>,
    val workflows: List<Workflow>,
)

private data class Part(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
)

private data class RangePart(
    val x: IntRange,
    val m: IntRange,
    val a: IntRange,
    val s: IntRange,
) {
    val isValid by lazy {
        x.size > 0 && m.size > 0 && a.size > 0 && s.size > 0
    }
}

private data class Workflow(
    val name: String,
    val rules: List<Rule>,
)

private sealed interface RangedRuleResult

private data class NextWorkflow(val workflowName: String) : RangedRuleResult

private data class Split(
    val workflowPart: RangePart,
    val workflowName: String,
    val remainingPart: RangePart,
) : RangedRuleResult

private sealed interface Rule {
    fun next(part: Part): String?

    fun apply(part: RangePart): RangedRuleResult
}

private data class GreaterThanRule(
    private val accessor: Accessor,
    private val value: Int,
    private val target: String,
) : Rule {
    override fun next(part: Part): String? = if (accessor(part) > value) target else null

    override fun apply(part: RangePart): RangedRuleResult {
        val valueRange = accessor(part)
        val newValueRange = value + 1..valueRange.last
        val newPart = accessor.apply(part, newValueRange)

        val remainingValueRange = valueRange.first..value
        val remainingPart = accessor.apply(part, remainingValueRange)

        return Split(newPart, target, remainingPart)
    }
}

private data class LessThanRule(
    private val accessor: Accessor,
    private val value: Int,
    private val target: String,
) : Rule {
    override fun next(part: Part): String? = if (accessor(part) < value) target else null

    override fun apply(part: RangePart): RangedRuleResult {
        val valueRange = accessor(part)
        val newValueRange = valueRange.first..<value
        val newPart = accessor.apply(part, newValueRange)

        val remainingValueRange = value..valueRange.last
        val remainingPart = accessor.apply(part, remainingValueRange)

        return Split(newPart, target, remainingPart)
    }
}

private data class FallbackRule(
    private val target: String,
) : Rule {
    override fun next(part: Part): String = target

    override fun apply(part: RangePart): RangedRuleResult = NextWorkflow(target)
}

private enum class Accessor {
    X,
    M,
    A,
    S,
    ;

    operator fun invoke(part: Part) = when (this) {
        X -> part.x
        M -> part.m
        A -> part.a
        S -> part.s
    }

    operator fun invoke(part: RangePart) = when (this) {
        X -> part.x
        M -> part.m
        A -> part.a
        S -> part.s
    }

    fun apply(part: RangePart, value: IntRange) = when (this) {
        X -> part.copy(x = value)
        M -> part.copy(m = value)
        A -> part.copy(a = value)
        S -> part.copy(s = value)
    }
}
