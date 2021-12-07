import kotlin.math.abs
import kotlin.random.Random

fun main() {
    fun part1(input: String): Int {
        return CrabSubmarine(input).minFuelPartOne
    }

    fun part2(input: String): Long {
        return CrabSubmarine(input).minFuelPartTwo
    }

    val testInput = readInput("Day07_test_input")[0]
    check(part1(testInput) == 37)
    check(part2(testInput) == 168L)

    val input = readInput("Day07_input")[0]
    println(part1(input)) //349812
    println(part2(input)) //99763899
}

class CrabSubmarine(input: String) {

    private val positions = input.split(",").map { it.toInt() }.toIntArray()

    /**
     * O(n) - Quick Select
     */
    private val medianPosition = run {

        fun partition(range: IntRange): IntRange {
            var nextLess = range.first
            var nextGreater = range.last
            var curr = nextLess
            val target = positions[Random.nextInt(nextLess, nextGreater + 1)]
            while (curr <= nextGreater) {
                when {
                    positions[curr] < target -> {
                        positions[curr] = positions[nextLess].also { positions[nextLess] = positions[curr] }
                        ++nextLess
                        ++curr
                    }
                    positions[curr] == target -> {
                        ++curr
                    }
                    positions[curr] > target -> {
                        positions[curr] = positions[nextGreater].also { positions[nextGreater] = positions[curr] }
                        --nextGreater
                    }
                }
            }
            return nextLess..nextGreater
        }

        val midIndex = positions.size / 2
        var rangeToPartition = 0..positions.lastIndex
        var midRange: IntRange
        do {
            partition(rangeToPartition).let {
                midRange = it
                rangeToPartition = when {
                    midIndex < it.first -> rangeToPartition.first until it.first
                    midIndex > it.last -> it.last + 1..rangeToPartition.last
                    else -> it
                }
            }
        } while (midIndex !in midRange)

        positions[midIndex]
    }

    val minFuelPartOne = positions.fold(0) { acc, position ->
        acc + abs(position - medianPosition)
    }

    /**
     * O(n log m) - Binary Search
     */
    val minFuelPartTwo = run {
        val fuel = mutableMapOf<Int, Long>() //position, fuel need
        fun countFuel(target: Int): Long {
            return fuel[target] ?: positions.fold(0L) { acc, position ->
                acc + abs(target - position).toLong().let { (1L + it) * it / 2L }
            }.also { fuel[target] = it }
        }

        var left = positions.minOrNull()!!
        var right = positions.maxOrNull()!!
        while (left < right) {
            val mid1 = left + (right - left) / 2
            val mid2 = mid1 + 1
            if (countFuel(mid1) < countFuel(mid2)) {
                right = mid1
            } else {
                left = mid2
            }
        }
        countFuel(left)
    }
}