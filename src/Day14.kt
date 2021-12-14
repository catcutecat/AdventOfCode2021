fun main() {
    fun part1(input: List<String>): Long {
        return ExtendedPolymerization(input).apply { move(10) }.quantityRange
    }

    fun part2(input: List<String>): Long {
        return ExtendedPolymerization(input).apply { move(40) }.quantityRange
    }

    val testInput = readInput("Day14_test_input")
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529L)

    val input = readInput("Day14_input")
    println(part1(input)) //2003
    println(part2(input)) //2276644000111
}

class ExtendedPolymerization(input: List<String>) {
    private val template = input[0]
    private val rules = Array(26) { IntArray(26) { -1 } }.also {
        for (i in 2..input.lastIndex) {
            val (left, right, mid) = arrayOf(input[i][0] - 'A', input[i][1] - 'A', input[i].last() - 'A')
            it[left][right] = mid
        }
    }
    private var current = Array(26) { LongArray(26) }.also {
        for (i in 0 until template.lastIndex) {
            val (left, right) = arrayOf(template[i] - 'A', template[i + 1] - 'A')
            ++it[left][right]
        }
    }
    private val elementCount = LongArray(26).also {
        template.forEach { element ->
            ++it[element - 'A']
        }
    }

    fun move(step: Int = 1) {
        repeat(step) {
            val new = Array(26) { LongArray(26) }
            for (left in current.indices) {
                for (right in current[left].indices) {
                    val count = current[left][right]
                    val mid = rules[left][right]
                    if (count > 0 && mid != -1) {
                        new[left][mid] += count
                        new[mid][right] += count
                        elementCount[mid] += count
                    }
                }
            }
            current = new
        }
    }

    val quantityRange: Long get() = elementCount.filter { it > 0 }.let { it.maxOrNull()!! - it.minOrNull()!! }
}
