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
    println(part2(input)) //1634946868992
}

class LanternfishCounter(input: String, day: Int) {

    /**
     *     0  1  2  3  4  5  6  7  8
     *   -----------------------------
     * 0 [ 0, 0, 0, 0, 0, 0, 1, 0, 1 ]
     * 1 [ 1, 0, 0, 0, 0, 0, 0, 0, 0 ]
     * 2 [ 0, 1, 0, 0, 0, 0, 0, 0, 0 ]
     * 3 [ 0, 0, 1, 0, 0, 0, 0, 0, 0 ]
     * 4 [ 0, 0, 0, 1, 0, 0, 0, 0, 0 ]
     * 5 [ 0, 0, 0, 0, 1, 0, 0, 0, 0 ]
     * 6 [ 0, 0, 0, 0, 0, 1, 0, 0, 0 ]
     * 7 [ 0, 0, 0, 0, 0, 0, 1, 0, 0 ]
     * 8 [ 0, 0, 0, 0, 0, 0, 0, 1, 0 ]
     */
    private val matrix = Array(9) { row ->
        LongArray(9) { col ->
            when {
                row == 0 && col == 6 -> 1L
                row == 0 && col == 8 -> 1L
                row - 1 == col -> 1L
                else -> 0L
            }
        }
    }

    private val pow: Array<LongArray> = run {
        var res = Array(9) { row ->
            LongArray(9) { col ->
                if (row == col) { 1 } else { 0 }
            }
        }
        var exp = day
        var mul = matrix
        while (exp > 0) {
            if (exp and 1 == 1) {
                res = res.times(mul)
            }
            mul = mul.times(mul)
            exp = exp shr 1
        }
        res
    }

    private fun Array<LongArray>.times(other: Array<LongArray>): Array<LongArray> {
        val res = Array(size) { LongArray(other[it].size) }
        for (row in 0..res.lastIndex) {
            for (col in 0..res[row].lastIndex) {
                res[row][col] = this[row].foldIndexed(0) { index, acc, num ->
                    acc + num * other[index][col]
                }
            }
        }
        return res
    }

    val value = input.split(",").map { it.toInt() }.fold(LongArray(9)) { acc, num ->
        ++acc[num]
        acc
    }.let { fishCount ->
        /**
         * O(n)
         */
//        repeat(day) {
//            val zero = fishCount[0]
//            for (i in 0..7) {
//                fishCount[i] = fishCount[i + 1]
//            }
//            fishCount[8] = zero
//            fishCount[6] += zero
//        }
//        fishCount.sum()
        /**
         * O(log n)
         */
        arrayOf(fishCount).times(pow)[0].sum()
    }
}