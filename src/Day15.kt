import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        return Chiton(input).smallLowestTotalRisk
    }

    fun part2(input: List<String>): Int {
        return Chiton(input).fullLowestTotalRisk
    }

    val testInput = readInput("Day15_test_input")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15_input")
    println(part1(input)) //739
    println(part2(input)) //3040
}

class Chiton(input: List<String>) {
    private val smallRiskMap = input.foldIndexed(Array(input.size) { IntArray(input[it].length) }) { index, acc, s ->
        for (i in s.indices) {
            acc[index][i] = s[i] - '0'
        }
        acc
    }

    private val riskMap = Array(smallRiskMap.size * 5) { IntArray(smallRiskMap[0].size * 5) }.also {
        fun wrapNumInRange(num: Int): Int {
            return if (num > 9) { num - 9 } else { num }
        }

        for (row in 0..4) {
            for (col in 0..4) {
                val colShift = col * smallRiskMap[0].size
                val rowShift = row * smallRiskMap.size
                for (r in smallRiskMap.indices) {
                    for (c in smallRiskMap[r].indices) {
                        it[r + rowShift][c + colShift] = wrapNumInRange(smallRiskMap[r][c] + col + row)
                    }
                }
            }
        }
    }

    private val minRisk = Array(riskMap.size) { IntArray(riskMap[it].size) { Int.MAX_VALUE } }

    init {
        val directions = arrayOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        val nextStep = PriorityQueue<Step>(compareBy { it.riskSum }).apply { offer(Step(0, 0, 0)) }
        while (nextStep.isNotEmpty()) {
            val (row, col, riskSum) = nextStep.poll()!!
            if (riskSum < minRisk[row][col]) {
                minRisk[row][col] = riskSum
                directions.map { (r, c) -> row + r to col + c }.forEach { (r, c) ->
                    if (r in riskMap.indices && c in riskMap[r].indices && minRisk[r][c] > riskSum + riskMap[r][c]) {
                        nextStep.offer(Step(r, c, riskSum + riskMap[r][c]))
                    }
                }
            }
        }
    }

    val smallLowestTotalRisk = minRisk[smallRiskMap.lastIndex][smallRiskMap.last().lastIndex]

    val fullLowestTotalRisk = minRisk.last().last()

    private data class Step(
        val row: Int,
        val col: Int,
        val riskSum: Int
    )
}
