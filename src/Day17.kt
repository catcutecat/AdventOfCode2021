fun main() {
    fun part1(input: List<String>): Int {
        return TrickShot(input[0]).highestY
    }

    fun part2(input: List<String>): Int {
        return TrickShot(input[0]).validCount
    }

    val testInput = readInput("Day17_test_input")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17_input")
    println(part1(input)) //3160
    println(part2(input)) //1928
}

class TrickShot(input: String) {
    private val rangeX: IntRange
    private val rangeY: IntRange
    val validCount: Int
    val highestY: Int
    init {
        """-?\d+""".toRegex().findAll(input).map { it.value.toInt() }.take(4).toList().let { (x1, x2, y1, y2) ->
            rangeX = x1..x2
            rangeY = y1..y2
        }

        var count = 0
        highestY = run {
            var res = 0
            for (x in 0..rangeX.last) {
                val stepRange = getStepRange(x)
                if (stepRange.isEmpty()) {
                    continue
                }
                for (y in -1000..1000) {
                    val lastY = stepRange.last.let { lastStep ->
                        y * lastStep + (0 - (lastStep - 1)) * lastStep / 2
                    }
                    if (lastY > rangeY.last) {
                        break
                    }
                    var maxY = 0
                    var currentY = 0
                    var vy = y
                    fun move() {
                        currentY += vy
                        --vy
                        maxY = maxOf(maxY, currentY)
                    }
                    for (step in 1 until stepRange.first) {
                        move()
                    }
                    for (step in stepRange) {
                        move()
                        if (currentY < rangeY.first) {
                            break
                        }
                        if (currentY in rangeY) {
                            ++count
                            res = maxOf(res, maxY)
                            break
                        }
                    }
                }
            }
            res
        }

        validCount = count
    }

    private fun getStepRange(vx: Int): IntRange {
        val maxX = (vx + 1) * vx / 2
        if (vx > rangeX.last || maxX < rangeX.first) {
            return IntRange.EMPTY
        }

        val minStep: Int = run {
            for (step in 1..vx) { //max vx steps
                val currentX = (vx + vx - (step - 1)) * step / 2
                if (currentX in rangeX) {
                    return@run step
                }
            }
            return IntRange.EMPTY
        }

        val maxStep: Int = run {
            for (step in minStep..vx) {
                val currentX = (vx + vx - (step - 1)) * step / 2
                if (currentX > rangeX.last) {
                    return@run step - 1
                }
            }
            1000
        }
        return minStep..maxStep
    }
}
