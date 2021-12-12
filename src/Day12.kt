fun main() {
    fun part1(input: List<String>): Int {
        return PassagePathing(input).totalPathCount(false)
    }

    fun part2(input: List<String>): Int {
        return PassagePathing(input).totalPathCount(true)
    }

    val testInput1 = readInput("Day12_test_input_1")
    check(part1(testInput1) == 10)
    check(part2(testInput1) == 36)

    val testInput2 = readInput("Day12_test_input_2")
    check(part1(testInput2) == 19)
    check(part2(testInput2) == 103)

    val testInput3 = readInput("Day12_test_input_3")
    check(part1(testInput3) == 226)
    check(part2(testInput3) == 3509)

    val input = readInput("Day12_input")
    println(part1(input)) //4338
    println(part2(input)) //114189
}

class PassagePathing(input: List<String>) {
    private val isBigCave: List<Boolean>
    private val neighbors: List<List<Int>>
    private val startIndex: Int
    private val endIndex: Int

    init {
        isBigCave = mutableListOf()
        neighbors = mutableListOf<MutableList<Int>>()

        fun addNewCave(name: String) {
            isBigCave.add(name[0].isUpperCase())
            neighbors.add(mutableListOf())
        }

        val nameToIndex = mutableMapOf<String, Int>()
        input.forEach { path ->
            val (name1, name2) = path.split("-")
            val id1 = nameToIndex[name1] ?: isBigCave.size.also { nameToIndex[name1] = it; addNewCave(name1) }
            val id2 = nameToIndex[name2] ?: isBigCave.size.also { nameToIndex[name2] = it; addNewCave(name2) }
            neighbors[id1].add(id2)
            neighbors[id2].add(id1)
        }

        startIndex = nameToIndex["start"]!!
        endIndex = nameToIndex["end"]!!
    }

    fun totalPathCount(canVisitOneSmallCaveTwice: Boolean): Int {
        var res = 0
        val visitCount = IntArray(isBigCave.size)
        fun traverse(id: Int, canForceVisitSmall: Boolean) {
            if (id == endIndex) {
                ++res
            } else {
                ++visitCount[id]
                neighbors[id].forEach {
                    if (isBigCave[it] || visitCount[it] == 0 || (canForceVisitSmall && it != startIndex)) {
                        traverse(it, canForceVisitSmall && (isBigCave[it] || visitCount[it] == 0))
                    }
                }
                --visitCount[id]
            }
        }
        traverse(startIndex, canVisitOneSmallCaveTwice)
        return res
    }
}
