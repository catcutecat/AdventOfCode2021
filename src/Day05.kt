import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return HydrothermalVent(input).getBasicDangerPointCount()
    }

    fun part2(input: List<String>): Int {
        return HydrothermalVent(input).getAdvanceDangerPointCount()
    }

    val testInput = readInput("Day05_test_input")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05_input")
    println(part1(input)) //7644
    println(part2(input)) //18627
}

class HydrothermalVent(input: List<String>) {
    private val basicMap: Array<IntArray>
    private val advanceMap: Array<IntArray>

    init {
        input.map { line ->
            line.split(" -> ").flatMap { point ->
                point.split(",").map { it.toInt() }
            }
        }.let {
            val size = it.maxOf { points -> points.maxOrNull()!! } + 1
            basicMap = it.fold(Array(size) { IntArray(size) }) { acc, (x1, y1, x2, y2) ->
                if (x1 == x2) {
                    for (y in minOf(y1, y2)..maxOf(y1, y2)) {
                        ++acc[x1][y]
                    }
                } else if (y1 == y2) {
                    for (x in minOf(x1, x2)..maxOf(x1, x2)) {
                        ++acc[x][y1]
                    }
                }
                acc
            }
            advanceMap = it.fold(Array(size) { IntArray(size) }) { acc, (x1, y1, x2, y2) ->
                if (x1 == x2) {
                    for (y in minOf(y1, y2)..maxOf(y1, y2)) {
                        ++acc[x1][y]
                    }
                } else if (y1 == y2) {
                    for (x in minOf(x1, x2)..maxOf(x1, x2)) {
                        ++acc[x][y1]
                    }
                } else if (abs(x1 - x2) == abs(y1 - y2)) {
                    var x = x1
                    var y = y1
                    val dx = if (x2 > x1) { 1 } else { -1 }
                    val dy = if (y2 > y1) { 1 } else { -1 }
                    while (x != x2) {
                        ++acc[x][y]
                        x += dx
                        y += dy
                    }
                    ++acc[x][y]
                }
                acc
            }
        }
    }

    fun getBasicDangerPointCount(minValue: Int = 2): Int {
        return basicMap.fold(0) { acc, row ->
            acc + row.count { it >= minValue }
        }
    }

    fun getAdvanceDangerPointCount(minValue: Int = 2): Int {
        return advanceMap.fold(0) { acc, row ->
            acc + row.count { it >= minValue }
        }
    }
}