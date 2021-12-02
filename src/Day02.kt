fun main() {
    fun part1(input: List<String>): Int {
        return input.fold(Submarine()) { acc, commandString ->
            acc.apply { runCommand(Command.fromString(commandString)) }
        }.puzzleAnswer1
    }

    fun part2(input: List<String>): Int {
        return input.fold(Submarine()) { acc, commandString ->
            acc.apply { runCommand(Command.fromString(commandString)) }
        }.puzzleAnswer2
    }

    val testInput = readInput("Day02_test_input")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02_input")
    println(part1(input)) //1727835
    println(part2(input)) //1544000595
}

class Submarine {
    private var horizontalPosition: Int = 0
    private var depth: Int = 0
    private var aim: Int = 0

    val puzzleAnswer1: Int get() = horizontalPosition * aim
    val puzzleAnswer2: Int get() = horizontalPosition * depth

    fun runCommand(command: Command) {
        when (command) {
            is Command.Forward -> {
                horizontalPosition += command.unit
                depth += command.unit * aim
            }
            is Command.Down -> {
                aim += command.unit
            }
            is Command.Up -> {
                aim -= command.unit
            }
        }
    }
}

sealed class Command {
    data class Forward(val unit: Int): Command()
    data class Down(val unit: Int): Command()
    data class Up(val unit: Int): Command()

    companion object {
        fun fromString(string: String): Command {
            val (direction, unitString) = string.split(" ")
            val unit = unitString.toInt()
            return when (direction) {
                "forward" -> Forward(unit)
                "down" -> Down(unit)
                "up" -> Up(unit)
                else -> throw IllegalArgumentException()
            }
        }
    }
}