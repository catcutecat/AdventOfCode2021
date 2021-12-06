fun main() {
    fun part1(input: String): Long {
        return LanternfishCounter(input, 80).value
    }

    fun part2(input: String): Long {
        return LanternfishCounter(input, 256).value
    }

    val testInput = readInput("Day06_test_input")[0]
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539L)

    val input = readInput("Day06_input")[0]
    println(part1(input)) //361169
    println(part2(input)) //
}

class LanternfishCounter(input: String, day: Int) {
    val value = input.split(",").map { it.toInt() }.fold(LongArray(9)) { acc, num ->
        ++acc[num]
        acc
    }.let { fishCount ->
        repeat(day) {
            val zero = fishCount[0]
            for (i in 0..7) {
                fishCount[i] = fishCount[i + 1]
            }
            fishCount[8] = zero
            fishCount[6] += zero
        }
        fishCount.sum()
    }
}