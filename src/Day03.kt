fun main() {
    fun part1(input: List<String>): Int {
        return input.fold(IntArray(input.first().length)) { acc, binaryString ->
            for (i in acc.indices) {
                acc[i] += if (binaryString[i] == '1') { 1 } else { -1 }
            }
            acc
        }.map {
            if (it > 0) { 1 } else { 0 }
        }.fold(0 to 0) { (gamma, epsilon), gammaBit ->
            (gamma shl 1) + gammaBit to ((epsilon shl 1) + (gammaBit xor 1))
        }.let { (gamma, epsilon) ->
            gamma * epsilon
        }
    }


    fun part2(input: List<String>): Int {
        return LifeSupport(input).lifeSupportRating
    }

    val testInput = readInput("Day03_test_input")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03_input")
    println(part1(input)) //3958484
    println(part2(input)) //1613181
}

class LifeSupport(input: List<String>) {

    private val oxygenGeneratorRating: Int
    private val co2ScrubberRating: Int

    val lifeSupportRating: Int

    private val root = Node(0)

    init {
        input.forEach { data ->
            var curr = root
            for (bit in data) {
                val index = bit - '0'
                curr = curr.next[index] ?: run { Node(index).also { curr.next[index] = it } }
                ++curr.count
            }
        }
        oxygenGeneratorRating = run {
            var res = 0
            var curr = root
            while (curr.next.any { it != null }) {
                val (next0, next1) = curr.next
                curr = when {
                    next0 != null && next1 != null -> {
                        if (next0.count > next1.count) {
                            next0
                        } else {
                            next1
                        }
                    }
                    else -> next0 ?: next1!!
                }
                res = (res shl 1) + curr.value
            }
            res
        }
        co2ScrubberRating = run {
            var res = 0
            var curr = root
            while (curr.next.any { it != null }) {
                val (next0, next1) = curr.next
                curr = when {
                    next0 != null && next1 != null -> {
                        if (next1.count < next0.count) {
                            next1
                        } else {
                            next0
                        }
                    }
                    else -> next0 ?: next1!!
                }
                res = (res shl 1) + curr.value
            }
            res
        }

        lifeSupportRating = oxygenGeneratorRating * co2ScrubberRating
    }


    private data class Node(
        val value: Int,
        var count: Int = 0,
        val next: Array<Node?> = arrayOf(null, null)
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Node

            if (count != other.count) return false
            if (!next.contentEquals(other.next)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = count
            result = 31 * result + next.contentHashCode()
            return result
        }
    }
}
