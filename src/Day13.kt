import java.awt.Point

fun main() {
    fun part1(input: List<String>): Int {
        return TransparentOrigami(input).dotsCountAfterFold(1)
    }

    fun part2(input: List<String>): Int {
        return TransparentOrigami(input).dotsCountAfterFold()
    }

    val testInput = readInput("Day13_test_input")
    check(part1(testInput) == 17)
    check(part2(testInput) == 16) //See log message for pattern on the folded paper

    val input = readInput("Day13_input")
    println(part1(input)) //751
    println(part2(input)) //95 //See log message for pattern on the folded paper
}

class TransparentOrigami(input: List<String>) {

    private val paper: Array<BooleanArray>
    private val instructions: List<Instruction>

    init {
        var index = 0
        val points = mutableListOf<Point>()
        while (input[index].isNotEmpty()) {
            input[index].split(",").map { it.toInt() }.let { (x, y) ->
                points.add(Point(x, y))
            }
            ++index
        }
        ++index

        val (maxX, maxY) = points.fold(Point(0, 0)) { acc, point ->
            acc.apply {
                x = maxOf(x, point.x)
                y = maxOf(y, point.y)
            }
        }.let { it.x to it.y }

        paper = points.fold(Array(maxX + 1) { BooleanArray(maxY + 1) }) { acc, point ->
            acc.also {
                it[point.x][point.y] = true
            }
        }

        instructions = mutableListOf<Instruction>().also {
            for (i in index..input.lastIndex) {
                it.add(Instruction(input[i]))
            }
        }
    }

    fun dotsCountAfterFold(foldCount: Int = instructions.size): Int {
        val foldedPaper = paper.clone()
        var validX = foldedPaper.lastIndex
        var validY = foldedPaper[validX].lastIndex
        repeat(foldCount) {
            val (direction, position) = instructions[it]
            when {
                direction == FoldDirection.X && position in (validX + 1) / 2.. validX -> {
                    for (diffX in 1..validX - position) {
                        for (y in 0..validY) {
                            foldedPaper[position - diffX][y] = foldedPaper[position - diffX][y] or foldedPaper[position + diffX][y]
                        }
                    }
                    validX = position - 1
                }
                direction == FoldDirection.Y && position in (validY + 1) / 2.. validY -> {
                    for (diffY in 1..validY - position) {
                        for (x in 0..validX) {
                            foldedPaper[x][position - diffY] = foldedPaper[x][position - diffY] or foldedPaper[x][position + diffY]
                        }
                    }
                    validY = position - 1
                }
            }
        }

        if (foldCount == instructions.size) {
            for (y in 0..validY) {
                for (x in 0..validX) {
                    print(if (foldedPaper[x][y]) { '#' } else { '.' })
                }
                println()
            }
        }

        return (0..validX).sumOf { x ->
            (0..validY).count { y ->
                foldedPaper[x][y]
            }
        }
    }

    private class Instruction(input: String) {
        operator fun component1() = direction
        operator fun component2() = position

        val direction: FoldDirection
        val position: Int

        init {
            input.split("=").let {
                direction = FoldDirection.fromChar(it[0].last())
                position = it[1].toInt()
            }
        }
    }

    private enum class FoldDirection {
        X, Y;

        companion object {
            fun fromChar(char: Char): FoldDirection {
                return when(char) {
                    'x' -> X
                    'y' -> Y
                    else -> error("Invalid char $char")
                }
            }
        }
    }
}
