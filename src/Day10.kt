import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        return SyntaxScoring(input).totalSyntaxErrorScore
    }

    fun part2(input: List<String>): Long {
        return SyntaxScoring(input).middleAutocompleteScores
    }

    val testInput = readInput("Day10_test_input")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10_input")
    println(part1(input)) //370407
    println(part2(input)) //3249889609L
}

class SyntaxScoring(input: List<String>) {

    val totalSyntaxErrorScore: Int

    private val allAutocompleteScores: List<Long>

    init {
        input.map { ChunkValidator.validate(it) }.let { validateResults ->
            totalSyntaxErrorScore = validateResults.sumOf { it.syntaxErrorScore }
            allAutocompleteScores = validateResults.map { it.autocompleteScore }.filter { it > 0 }.sorted()
        }
    }

    val middleAutocompleteScores = allAutocompleteScores[allAutocompleteScores.lastIndex shr 1]

    private object ChunkValidator {
        private val validPairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
        private val errorPoint = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
        private val autocompletePoint = mapOf('(' to 1L, '[' to 2L, '{' to 3L, '<' to 4L)

        fun validate(string: String): ChuckValidateResult {
            val stack = Stack<Char>()
            string.forEach {
                when {
                    validPairs.contains(it) -> stack.add(it)
                    stack.isNotEmpty() && validPairs[stack.peek()]!! == it -> stack.pop()
                    else -> return ChuckValidateResult(errorPoint[it]!!, 0L)
                }
            }
            return ChuckValidateResult(0, stack.foldRight(0L) { c, acc ->
                acc * 5L + autocompletePoint[c]!!
            })
        }

        data class ChuckValidateResult(
            val syntaxErrorScore: Int,
            val autocompleteScore: Long
        )
    }
}
