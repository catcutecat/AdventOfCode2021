fun main() {
    fun part1(input: List<String>): Int {
        return SmokeBasin(input).riskLevel
    }

    fun part2(input: List<String>): Int {
        return SmokeBasin(input).sizeProductOfTopThreeBasins
    }

    val testInput = readInput("Day09_test_input")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09_input")
    println(part1(input)) //496
    println(part2(input)) //902880
}

class SmokeBasin(input: List<String>) {
    private val heightMap = Array(input.size) { row ->
        IntArray(input[row].length) { col ->
            input[row][col] - '0'
        }
    }

    private val directions = arrayOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

    private fun isLowPoint(row: Int, col: Int): Boolean {
        directions.map { (r, c) -> row + r to col + c }.forEach { (r, c) ->
            if (r in heightMap.indices && c in heightMap[r].indices && heightMap[row][col] >= heightMap[r][c]) {
                return false
            }
        }
        return true
    }

    val riskLevel = heightMap.foldIndexed(0) { row, acc, heights ->
        acc + heights.foldIndexed(0) { col, rowAcc, height ->
            rowAcc + if (isLowPoint(row, col)) { height + 1 } else { 0 }
        }
    }

    private val basins = buildList<Int> {
        val visited = Array(heightMap.size) { row ->
            BooleanArray(heightMap[row].size) { col ->
                heightMap[row][col] == 9
            }
        }
        fun countSize(row: Int, col: Int): Int {
            visited[row][col] = true
            return directions.map { (r, c) -> row + r to col + c }.fold(1) { acc, (r, c) ->
                acc + if (r in heightMap.indices && c in heightMap[r].indices && !visited[r][c]) {
                    countSize(r, c)
                } else { 0 }
            }
        }
        for (row in heightMap.indices) {
            for (col in heightMap[row].indices) {
                if (!visited[row][col]) {
                    add(countSize(row, col))
                }
            }
        }
        sortBy { -it }
    }

    val sizeProductOfTopThreeBasins = run {
        var res = 1
        for (i in 0 until minOf(3, basins.size)) {
            res *= basins[i]
        }
        res
    }
}