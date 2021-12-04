fun main() {
    fun part1(input: List<String>): Int {
        return BingoSystem(input).apply { play() }.firstWinnerScore
    }

    fun part2(input: List<String>): Int {
        return BingoSystem(input).apply { play() }.lastWinnerScore
    }

    val testInput = readInput("Day04_test_input")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04_input")
    println(part1(input)) //32844
    println(part2(input)) //4920
}

class BingoSystem(input: List<String>) {
    private val numbers = input[0].split(",").map { it.toInt() }
    private val boards: List<Board> = mutableListOf<Board>().apply {
        for (i in 2..input.lastIndex step 6) {
            add(Board(input.subList(i, i + 5)))
        }
    }

    var firstWinnerScore: Int = -1
        private set

    var lastWinnerScore: Int = -1
        private set

    fun play(): Int {
        for (num in numbers) {
            drawNumber(num).takeIf { it > -1 }?.let {
                if (firstWinnerScore == -1) {
                    firstWinnerScore = it
                }
                lastWinnerScore = it
            }
        }
        return 0
    }

    /**
     * Draw a number and gets max winning score when any board won.
     *
     * @param number the number to draw.
     * @return the max winning score, or -1 if no board win.
     */
    private fun drawNumber(number: Int): Int {
        return boards.fold(-1) { acc, board ->
            if (board.score == -1) {
                maxOf(acc, board.drawNumber(number))
            } else {
                acc
            }
        }
    }

    private class Board(input: List<String>) {
        private val numberPosition: Map<Int, Pair<Int, Int>>
        private val isMarked = Array(5) { BooleanArray(5) }
        private val rowMarkCount = IntArray(5)
        private val colMarkCount = IntArray(5)
        private var unMarkedSum: Int

        var score: Int = -1
            private set

        init {
            val board = input.map { it.trim().split("""\s+""".toRegex()).map { it.toInt() } }
            numberPosition = board.foldIndexed(mutableMapOf()) { row, acc, ints ->
                ints.forEachIndexed { col, number ->
                    acc[number] = row to col
                }
                acc
            }
            unMarkedSum = board.fold(0) { acc, ints -> acc + ints.sum() }
        }

        /**
         * Draw a number and gets winning score when won.
         *
         * @param number the number to draw.
         * @return the winning score, or -1 if not yet win.
         */
        fun drawNumber(number: Int): Int {
            if (score == -1) {
                score = numberPosition[number]?.takeIf { (row, col) ->
                    !isMarked[row][col]
                }?.let { (row, col) ->
                    isMarked[row][col] = true
                    unMarkedSum -= number
                    ++rowMarkCount[row]
                    ++colMarkCount[col]
                    if (rowMarkCount[row] == 5 || colMarkCount[col] == 5) {
                        unMarkedSum * number
                    } else {
                        -1
                    }
                } ?: -1
            }
            return score
        }
    }
}