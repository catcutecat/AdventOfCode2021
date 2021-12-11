fun main() {
    fun part1(input: List<String>): Int {
        return DumboOctopus(input).apply { stepForward(100) }.totalFlashCount
    }

    fun part2(input: List<String>): Int {
        return DumboOctopus(input).apply { stepForwardUntilAllFlash() }.currentStep
    }

    val testInput = readInput("Day11_test_input")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11_input")
    println(part1(input)) //1785
    println(part2(input)) //354
}

class DumboOctopus(input: List<String>) {

    companion object {
        private const val FLASH_ENERGY_LEVEL = 10
    }

    private val octopus = Array(input.size) { row -> IntArray(input[row].length) { col -> input[row][col] - '0' } }
    private val steps = Array(octopus.size) { IntArray(octopus[it].size) }

    private val octopusCount = octopus.size * octopus[0].size

    var totalFlashCount: Int = 0
        private set

    var currentStep = 0
        private set

    private val directions = arrayOf(0 to 1, 0 to -1, 1 to 0, -1 to 0, 1 to 1, 1 to -1, -1 to 1, -1 to -1)

    fun stepForwardUntilAllFlash() {
        do {
            val previousFlashCount = totalFlashCount
            stepForward(1)
        } while(totalFlashCount - previousFlashCount != octopusCount)
    }

    fun stepForward(step: Int) {
        repeat(step) {
            ++currentStep
            for (row in octopus.indices) {
                for (col in octopus[row].indices) {
                    stepForwardIfNeeded(row, col)
                }
            }
        }
    }

    private fun stepForwardIfNeeded(row: Int, col: Int) {
        if (steps[row][col] != currentStep) {
            ++steps[row][col]
            increaseEnergy(row, col)
        }
    }

    private fun increaseEnergy(row: Int, col: Int) {
        ++octopus[row][col]
        flashIfNeeded(row, col)
    }

    private fun flashIfNeeded(row: Int, col: Int) {
        if (octopus[row][col] == FLASH_ENERGY_LEVEL) {
            octopus[row][col] = 0
            ++totalFlashCount
            directions.map { (r, c) -> row + r to col + c }.forEach { (r, c) ->
                if (r in octopus.indices && c in octopus[r].indices) {
                    stepForwardIfNeeded(r, c)
                    if (octopus[r][c] != 0) {
                        increaseEnergy(r, c)
                    }
                }
            }
        }
    }
}
