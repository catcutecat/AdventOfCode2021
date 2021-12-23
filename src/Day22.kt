fun main() {
    fun part1(input: List<String>): Long {
        return ReactorReboot(input).run(ReactorReboot.Command.INITIALIZATION)
    }

    fun part2(input: List<String>): Long {
        return ReactorReboot(input).run(ReactorReboot.Command.REBOOT)
    }

    val testInput1 = readInput("Day22_test_input_1")
    check(part1(testInput1) == 39L)

    val testInput2 = readInput("Day22_test_input_2")
    check(part1(testInput2) == 590784L)

    val testInput3 = readInput("Day22_test_input_3")
    check(part1(testInput3) == 474140L)
    check(part2(testInput3) == 2758514936282235L)

    val input = readInput("Day22_input")
    println(part1(input)) //606484
    println(part2(input)) //1162571910364852
}

class ReactorReboot(input: List<String>) {

    private val instructions = input.map { Instruction.fromString(it) }

    fun run(command: Command): Long {
        val (allX, allY, allZ) = instructions.fold(Array(3) { sortedSetOf<Int>() }) { acc, instruction ->
            if (command == Command.REBOOT || instruction.isSmall) {
                acc[0].add(instruction.xRange.first)
                acc[0].add(instruction.xRange.last)
                acc[1].add(instruction.yRange.first)
                acc[1].add(instruction.yRange.last)
                acc[2].add(instruction.zRange.first)
                acc[2].add(instruction.zRange.last)
            }
            acc
        }.map { it.toList() }

        val xIndex = allX.mapIndexed { index, i -> i to index }.toMap()
        val yIndex = allY.mapIndexed { index, i -> i to index }.toMap()
        val zIndex = allZ.mapIndexed { index, i -> i to index }.toMap()
        fun getIndices(instruction: Instruction, zConstraint: IntRange) = listOf(
            xIndex[instruction.xRange.first]!!..xIndex[instruction.xRange.last]!!,
            yIndex[instruction.yRange.first]!!..yIndex[instruction.yRange.last]!!,
            maxOf(zIndex[instruction.zRange.first]!!, zConstraint.first)..minOf(zIndex[instruction.zRange.last]!!, zConstraint.last)
        )

        var onCount = 0L

        val maxSize = 2097152
        val maxZSize = maxOf(2, maxSize / allX.size / allY.size)

        for (zStartIndex in 0..allZ.lastIndex step (maxZSize - 1)) {

            val states = Array(allX.size) { Array(allY.size) { Array(minOf(maxZSize, allZ.size - zStartIndex)) { State() } } }

            for (instruction in instructions) if (command == Command.REBOOT || instruction.isSmall) {
                val turnOn = instruction.turnOn
                val (xIndices, yIndices, zIndices) = getIndices(instruction, zStartIndex until zStartIndex + maxZSize)

                for (z in zIndices) {
                    for (y in yIndices) {
                        for (x in xIndices) {
                            states[x][y][z - zStartIndex].apply {
                                selfOn = turnOn
                                if (x != xIndices.last) { xSideOn = turnOn }
                                if (y != yIndices.last) { ySideOn = turnOn }
                                if (z != zIndices.last) { zSideOn = turnOn }
                                if (x != xIndices.last && y != yIndices.last) { xyFaceOn = turnOn }
                                if (y != yIndices.last && z != zIndices.last) { yzFaceOn = turnOn }
                                if (z != zIndices.last && x != xIndices.last) { zxFaceOn = turnOn }
                                if (x != xIndices.last && y != yIndices.last && z != zIndices.last) { innerOn = turnOn }
                            }
                        }
                    }
                }
            }

            val isLastBlock = zStartIndex + maxZSize - 1 > allZ.lastIndex

            for (x in states.indices) {
                for (y in states[x].indices) {
                    for (z in states[x][y].indices) {
                        val xLength = if (x != states.lastIndex) { allX[x + 1] - allX[x] - 1 } else { 0 }.toLong()
                        val yLength = if (y != states[x].lastIndex) { allY[y + 1] - allY[y] - 1 } else { 0 }.toLong()
                        val zRealIndex = z + zStartIndex
                        val lastZIndex = minOf(allZ.lastIndex, zStartIndex + maxZSize - 1)
                        val zLength = if (zRealIndex != lastZIndex) { allZ[zRealIndex + 1] - allZ[zRealIndex] - 1 } else { 0 }.toLong()

                        if (z != states[x][y].lastIndex || isLastBlock) {
                            states[x][y][z].run {
                                if (selfOn) { onCount += 1L }
                                if (xSideOn) { onCount += xLength }
                                if (ySideOn) { onCount += yLength }
                                if (zSideOn) { onCount += zLength }
                                if (xyFaceOn) { onCount += xLength * yLength }
                                if (yzFaceOn) { onCount += yLength * zLength }
                                if (zxFaceOn) { onCount += zLength * xLength }
                                if (innerOn) { onCount += xLength * yLength * zLength }
                            }
                        }
                    }
                }
            }
        }

        return onCount
    }

    enum class Command {
        INITIALIZATION, REBOOT
    }

    private data class State(
        var selfOn: Boolean = false,
        var xSideOn: Boolean = false,
        var ySideOn: Boolean = false,
        var zSideOn: Boolean = false,
        var xyFaceOn: Boolean = false,
        var yzFaceOn: Boolean = false,
        var zxFaceOn: Boolean = false,
        var innerOn: Boolean = false
    )

    private data class Instruction(
        val turnOn: Boolean,
        val xRange: IntRange,
        val yRange: IntRange,
        val zRange: IntRange
    ) {
        val isSmall = xRange.first >= -50 && xRange.last <= 50
                && yRange.first >= -50 && yRange.last <= 50
                && zRange.first >= -50 && zRange.last <= 50

        companion object {
            fun fromString(string: String): Instruction {
                val turnOn = string[1] == 'n'
                val (xRange, yRange, zRange) = Regex("""(-?\d+)..(-?\d+)""").findAll(string).take(3).map {
                    val (first, last) = it.destructured
                    first.toInt()..last.toInt()
                }.toList()
                return Instruction(turnOn, xRange, yRange, zRange)
            }
        }
    }
}
