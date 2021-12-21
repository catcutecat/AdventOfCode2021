fun main() {
    fun part1(input: List<String>): Int {
        return DiracDice(input).playUsingDeterministicDie().run { scoreOfTheLosingPlayer * numberOfTimeRolled }
    }

    fun part2(input: List<String>): Long {
        return DiracDice(input).playUsingDiracDice().winningUniverses
    }

    val testInput = readInput("Day21_test_input")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315L)

    val input = readInput("Day21_input")
    println(part1(input)) //888735
    println(part2(input)) //647608359455719
}

class DiracDice(input: List<String>) {

    private val startPositions = input.map { it.substringAfter(": ").toInt() }

    fun playUsingDeterministicDie(): DeterministicGameResult {

        var deterministicDie = 1
        fun nextThreeDeterministicDieResult(): Int {
            var res = 0
            repeat(3) {
                res += deterministicDie
                ++deterministicDie
                if (deterministicDie > 100) {
                    deterministicDie = 1
                }
            }
            return res
        }

        val positions = startPositions.toIntArray()
        val scores = IntArray(positions.size)
        var round = 0
        var player = 1
        while (scores[player] < 1000) {
            player = player xor 1
            positions[player] = (positions[player] + nextThreeDeterministicDieResult()) % 10
            scores[player] += if (positions[player] == 0) { 10 } else { positions[player] }
            ++round
        }
        return DeterministicGameResult(
            scoreOfTheLosingPlayer = scores[player xor 1],
            numberOfTimeRolled = round * 3
        )
    }

    /**
     * 1 1 1 = 1 -> Move 3 in 1 universe
     *
     * 1 1 2 = 3 -> Move 4 in 3 universes
     *
     * 1 1 3 = 3
     * 1 2 2 = 3 -> Move 5 in 6 universes
     *
     * 1 2 3 = 6
     * 2 2 2 = 1 -> Move 6 in 7 universes
     *
     * 1 3 3 = 3
     * 2 2 3 = 3 -> Move 7 in 6 universes
     *
     * 2 3 3 = 3 -> Move 8 in 3 universes
     *
     * 3 3 3 = 1 -> Move 9 in 1 universe
     */

    fun playUsingDiracDice(): DiracGameResult {
        val diracDiceResults = arrayOf(
            DiracDiceResult(3, 1),
            DiracDiceResult(4, 3),
            DiracDiceResult(5, 6),
            DiracDiceResult(6, 7),
            DiracDiceResult(7, 6),
            DiracDiceResult(8, 3),
            DiracDiceResult(9, 1)
        )
        fun winningUniverses(positions: IntArray, scores: IntArray, player: Int): LongArray {
            return when {
                scores[0] >= 21 -> longArrayOf(1, 0)
                scores[1] >= 21 -> longArrayOf(0, 1)
                else -> {
                    diracDiceResults.fold(LongArray(2)) { acc, (move, numberOfUniverse) ->
                        val newPositions = positions.clone().also {
                            it[player] = (it[player] + move) % 10
                        }
                        val newScores = scores.clone().also {
                            it[player] += if (newPositions[player] == 0) { 10 } else { newPositions[player] }
                        }
                        winningUniverses(newPositions, newScores, player xor 1).forEachIndexed { index, count ->
                            acc[index] += count * numberOfUniverse
                        }
                        acc
                    }
                }
            }
        }
        return DiracGameResult(winningUniverses(startPositions.toIntArray(), IntArray(startPositions.size), 0).maxOf { it })
    }

    data class DeterministicGameResult(
        val scoreOfTheLosingPlayer: Int,
        val numberOfTimeRolled: Int
    )

    data class DiracGameResult(
        val winningUniverses: Long
    )

    data class DiracDiceResult(
        val move: Int,
        val numberOfUniverse: Int
    )
}
