fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toInt() }.zipWithNext().count { (prev, curr) ->
            prev < curr
        }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.toInt() }.windowed(3).zipWithNext().count { (prev, curr) ->
            prev[0] < curr[2]
        }
    }

    val testInput = readInput("Day01_test_input")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01_input")
    println(part1(input))
    println(part2(input))
}
