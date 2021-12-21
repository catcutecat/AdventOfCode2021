fun main() {
    fun part1(input: List<String>): Int {
        return TrenchMap(input).applyAlgorithm(2)
    }

    fun part2(input: List<String>): Int {
        return TrenchMap(input).applyAlgorithm(50)
    }

    val testInput = readInput("Day20_test_input")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("Day20_input")
    println(part1(input)) //5498
    println(part2(input)) //16014
}

class TrenchMap(input: List<String>) {
    private val algorithm = BooleanArray(input[0].length) { input[0][it] == '#' }
    private val originalImage = Array(input.size - 2) { row ->
        BooleanArray(input[2 + row].length) { col ->
            input[2 + row][col] == '#'
        }
    }
    private val rowSize = originalImage.size
    private val colSize = originalImage[0].size

    fun applyAlgorithm(count: Int): Int {
        require(count and 1 == 0)
        val additionBorderWidth = count * 2
        var image = Array(rowSize + additionBorderWidth * 2) { BooleanArray(colSize + additionBorderWidth * 2)}.also {
            for (row in originalImage.indices) {
                for (col in originalImage[row].indices) {
                    it[additionBorderWidth + row][additionBorderWidth + col] = originalImage[row][col]
                }
            }
        }
        var isOuterLit = false

        val squareDirections = arrayOf(
            -1 to -1, -1 to 0, -1 to 1,
             0 to -1,  0 to 0,  0 to 1,
             1 to -1,  1 to 0,  1 to 1
        )
        fun getNextPixel(row: Int, col: Int): Boolean {
            val number = squareDirections.map { (r, c) -> row + r to col + c }.fold(0) { acc, (r, c) ->
                val isLit = if (r in image.indices && c in image[r].indices) { image[r][c] } else { isOuterLit }
                (acc shl 1) + if (isLit) { 1 } else { 0 }
            }
            return algorithm[number]
        }

        repeat(count) {
            val newImage = Array(image.size) { row ->
                BooleanArray(image[row].size) { col ->
                    getNextPixel(row, col)
                }
            }
            isOuterLit = if (isOuterLit) { algorithm.last() } else { algorithm.first() }
            image = newImage
        }

        return image.sumOf { it.count { isLit -> isLit } }
    }
}
