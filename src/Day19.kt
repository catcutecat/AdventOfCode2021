import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return BeaconScanner(input).beaconCount
    }

    fun part2(input: List<String>): Int {
        return BeaconScanner(input).largestManhattanDistance
    }

    val testInput = readInput("Day19_test_input")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("Day19_input")
    println(part1(input)) //467
    println(part2(input)) //12226
}

class BeaconScanner(input: List<String>) {

    private val transformArrays = buildList {
        val choices = Array(3) { choice -> BooleanArray(3) { choice == it } }
        fun applySign(index: Int, array: Array<IntArray>) {
            if (index == choices.size) {
                if (array.determinant == 1) {
                    add(Array(3) { array[it].clone() })
                }
            } else {
                applySign(index + 1, array)
                for (col in array[index].indices) {
                    array[index][col] = -array[index][col]
                }
                applySign(index + 1, array)
            }
        }
        fun permutation(index: Int) {
            if (index == choices.lastIndex) {
                applySign(0, Array(3) { row ->
                    IntArray(3) { col ->
                        if (choices[row][col]) { 1 } else { 0 }
                    }
                })
            } else {
                for (i in index..choices.lastIndex) {
                    choices[i] = choices[index].also { choices[index] = choices[i] }
                    permutation(index + 1)
                    choices[i] = choices[index].also { choices[index] = choices[i] }
                }
            }
        }
        permutation(0)
    }

    private val scanners: List<Scanner> = buildList {
        var index = 0
        while (index < input.size) {
            if (!input[index].startsWith("---")) {
                val positions = mutableListOf<Position>()
                while (index < input.size && input[index].isNotEmpty()) {
                    positions.add(Position.fromString(input[index]))
                    ++index
                }
                positions.fold(List(24) { mutableListOf<Position>() }) { acc, position ->
                    position.transforms.forEachIndexed { index, transformedPosition ->
                        acc[index].add(transformedPosition)
                    }
                    acc
                }.map {
                    it.sort()
                    Beacons(it)
                }.let {
                    add(Scanner(it))
                }
            }
            ++index
        }
    }

    private val beaconPositions: Set<Position>
    private val scannerPositions: Array<Position>

    init {
        val alignedBeacons = Array(scanners.size) { Beacons.EMPTY }.also { it[0] = scanners[0].transforms[0] }
        scannerPositions = Array(scanners.size) { Position() }

        do {
            val newAlign = mutableListOf<Int>()
            for (needAlignIndex in scanners.indices) if (alignedBeacons[needAlignIndex].isEmpty()) {
                for (targetBeacons in alignedBeacons) if (targetBeacons.isNotEmpty()) {
                    val (distance, newAlignedBeacons) = scanners[needAlignIndex].alignWith(targetBeacons)
                    if (newAlignedBeacons.isNotEmpty()) {
                        alignedBeacons[needAlignIndex] = newAlignedBeacons
                        newAlign.add(needAlignIndex)
                        scannerPositions[needAlignIndex] = scannerPositions[0].move(distance)
                        break
                    }
                }
            }
        } while (newAlign.isNotEmpty())

        beaconPositions = alignedBeacons.flatMap { it.value }.toSet()
    }

    val beaconCount = beaconPositions.size
    val largestManhattanDistance = scannerPositions.fold(0) { acc1, position1 ->
        maxOf(acc1, scannerPositions.fold(0) { acc2, position2 ->
            maxOf(acc2, Distance(position1, position2).manhattanDistance)
        })
    }

    private data class Scanner(
        val transforms: List<Beacons>
    ) {
        fun alignWith(other: Beacons): AlignResult {
            for (beacons in transforms) {
                val (distance, alignCount) = beacons.alignWith(other)
                if (alignCount >= 12) {
                    val transformed = beacons.value.map {
                        it.move(distance)
                    }
                    return AlignResult(distance, Beacons(transformed))
                }
            }
            return AlignResult()
        }

        data class AlignResult(
            val distance: Distance = Distance(),
            val alignedBeacons: Beacons = Beacons(listOf())
        )
    }

    @JvmInline
    private value class Beacons(val value: List<Position>) {

        fun isEmpty() = value.isEmpty()
        fun isNotEmpty() = value.isNotEmpty()

        companion object {
            val EMPTY = Beacons(listOf())
        }

        fun alignWith(other: Beacons): AlignResult {
            val targetPositions = other.value.toSet()
            for (i in 0..value.size - 12) { // 14157 -> 4212
                for (j in 0..other.value.size - 12) {
                    val distance = Distance(value[i], other.value[j])
                    val alignCount = run {
                        var count = 0
                        for (k in i..value.lastIndex) {
                            if (targetPositions.contains(value[k].move(distance))) {
                                ++count
                            }
                        }
                        count
                    }
                    if (alignCount >= 12) {
                        return AlignResult(distance, alignCount)
                    }
                }
            }
            return AlignResult()
        }

        data class AlignResult(
            val distance: Distance = Distance(),
            val alignCount: Int = 0
        )
    }

    private data class Distance(
        val dx: Int = 0,
        val dy: Int = 0,
        val dz: Int = 0
    ) {
        constructor(from: Position, to: Position) : this(to.x - from.x, to.y - from.y, to.z - from.z)

        val manhattanDistance: Int = abs(dx) + abs(dy) + abs(dz)
    }

    private data class Position(
        val x: Int = 0,
        val y: Int = 0,
        val z: Int = 0
    ): Comparable<Position> {

        companion object {
            fun fromString(input: String): Position {
                return input.split(",").map { it.toInt() }.let { (x, y, z) ->
                    Position(x, y, z)
                }
            }

            fun fromIntArray(input: IntArray): Position {
                return Position(input[0], input[1], input[2])
            }
        }

        fun move(distance: Distance): Position = Position(x + distance.dx, y + distance.dy, z + distance.dz)

        override fun compareTo(other: Position): Int = when {
            this.x != other.x -> this.x compareTo other.x
            this.y != other.y -> this.y compareTo other.y
            else -> this.z compareTo other.z
        }

        val intArray = intArrayOf(x, y, z)
    }

    private val Array<IntArray>.determinant: Int
        get() {
            val (row1, row2, row3) = this
            val (x1, x2, x3) = row1
            val (y1, y2, y3) = row2
            val (z1, z2, z3) = row3
            return x1 * y2 * z3 + x2 * y3 * z1 + x3 * y1 * z2 - x3 * y2 * z1 - x2 * y1 * z3 - x1 * y3 * z2
        }

    private val Position.transforms: List<Position>
        get() = transformArrays.fold(mutableListOf()) { acc, transformArray ->
            acc.add(Position.fromIntArray(intArray.times(transformArray)))
            acc
        }

    private fun IntArray.times(other: Array<IntArray>): IntArray {
        return IntArray(other[0].size) {
            foldIndexed(0) { index, acc, i ->
                acc + other[index][it] * i
            }
        }
    }
}
