fun main() {
    fun part1(input: List<String>): Int {
        return SevenSegmentSearch(input).totalCountOf1478
    }

    fun part2(input: List<String>): Int {
        return SevenSegmentSearch(input).totalValue
    }

    val testInput = readInput("Day08_test_input")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08_input")
    println(part1(input)) //521
    println(part2(input)) //1016804
}

/**
 *     0:      1:      2:      3:      4:
 *    aaaa    ....    aaaa    aaaa    ....
 *   b    c  .    c  .    c  .    c  b    c
 *   b    c  .    c  .    c  .    c  b    c
 *    ....    ....    dddd    dddd    dddd
 *   e    f  .    f  e    .  .    f  .    f
 *   e    f  .    f  e    .  .    f  .    f
 *    gggg    ....    gggg    gggg    ....
 *
 *     5:      6:      7:      8:      9:
 *    aaaa    aaaa    aaaa    aaaa    aaaa
 *   b    .  b    .  .    c  b    c  b    c
 *   b    .  b    .  .    c  b    c  b    c
 *    dddd    dddd    ....    dddd    dddd
 *   .    f  e    f  .    f  e    f  .    f
 *   .    f  e    f  .    f  e    f  .    f
 *    gggg    gggg    ....    gggg    gggg
 *
 *
 *  digit  pattern  length
 *    1    __c__f_    2
 *    7    a_c__f_    3
 *    4    _bcd_f_    4
 *    8    abcdefg    7
 *
 *    2    a_cde_g    5
 *    3    a_cd_fg    5
 *    5    ab_d_fg    5
 *
 *    0    abc_efg    6
 *    6    ab_defg    6
 *    9    abcd_fg    6
 */

class SevenSegmentSearch(input: List<String>) {

    private val displays = input.map { Display(it) }
    val totalCountOf1478 = displays.sumOf { it.countOf1478 }
    val totalValue = displays.sumOf { it.value }

    private class Display(input: String) {
        private val allPatterns: List<Pattern>
        private val outputPatterns: List<Pattern>
        init {
            input.split(" | ").let { (patternString, outputString) ->
                allPatterns = patternString.split(" ").map { Pattern(it) }
                outputPatterns = outputString.split(" ").map { Pattern(it) }
            }
        }
        private val maskToDigit: Map<Int, Int> = run {
            val res = mutableMapOf<Int, Int>()

            val masksFromLength = allPatterns.fold(Array(8) { mutableListOf<Int>() }) { acc, (length, mask) ->
                acc[length].add(mask)
                acc
            }
            val digitToMask = mutableMapOf<Int, Int>()

            fun associateDigitWithMask(digit: Int, mask: Int) {
                digitToMask[digit] = mask
                res[mask] = digit
            }

            associateDigitWithMask(1, masksFromLength[2][0])
            associateDigitWithMask(7, masksFromLength[3][0])
            associateDigitWithMask(4, masksFromLength[4][0])
            associateDigitWithMask(8, masksFromLength[7][0])

            val mask7 = digitToMask[7]!!

            associateDigitWithMask(3, masksFromLength[5].first { it and mask7 == mask7 })

            val mask3 = digitToMask[3]!!

            associateDigitWithMask(9, masksFromLength[6].first { it and mask3 == mask3 })

            val mask9 = digitToMask[9]!!

            associateDigitWithMask(5, masksFromLength[5].first { it != mask3 && it and mask9 == it })

            val mask5 = digitToMask[5]!!

            associateDigitWithMask(6, masksFromLength[6].first { it != mask9 && it and mask5 == mask5 })

            val mask6 = digitToMask[6]!!

            associateDigitWithMask(0, masksFromLength[6].first { it != mask6 && it != mask9 })
            associateDigitWithMask(2, masksFromLength[5].first { it != mask3 && it != mask5 })

            res
        }

        val countOf1478 = outputPatterns.count { it.is1478 }
        val value = outputPatterns.fold(0) { acc, pattern ->
            acc * 10 + maskToDigit[pattern.mask]!!
        }

        private class Pattern(input: String) {
            operator fun component1() = length
            operator fun component2() = mask

            val length = input.length
            val mask = input.fold(0) { acc, c ->
                acc or (1 shl c - 'a')
            }
            val is1478 = length == 2 || length == 3 || length == 4 || length == 7
        }
    }
}
