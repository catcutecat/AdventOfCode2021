fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toInt() }.windowed(2).count {
            it.first() < it.last()
        }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.toInt() }.windowed(4).count {
            it.first() < it.last()
        }
    }

    val testInput = readInput("Day01_test_input")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01_input")
    println(part1(input)) //1154
    println(part2(input)) //1127
}
