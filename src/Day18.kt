fun main() {
    fun part1(input: List<String>): Int {
        return input.iterator().asSequence()
            .map(SnailfishNumber::fromString)
            .reduce { acc, num -> acc + num }
            .magnitude
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map(SnailfishNumber::fromString)
        var res = 0
        for (i in numbers.indices) {
            for (j in numbers.indices) {
                if (i != j) res = maxOf(res, (numbers[i] + numbers[j]).magnitude)
            }
        }
        return res
    }

    check(part1(readInput("Day18_test_input_1")) == 143)
    check(part1(readInput("Day18_test_input_2")) == 1384)
    check(part1(readInput("Day18_test_input_3")) == 445)
    check(part1(readInput("Day18_test_input_4")) == 791)
    check(part1(readInput("Day18_test_input_5")) == 1137)
    check(part1(readInput("Day18_test_input_6")) == 3488)

    check(part1(readInput("Day18_test_input_7")) == 4140)
    check(part2(readInput("Day18_test_input_7")) == 3993)

    val input = readInput("Day18_input")
    println(part1(input)) //3981
    println(part2(input)) //4687
}

class SnailfishNumber private constructor() {

    private var left: SnailfishNumber? = null
    private var right: SnailfishNumber? = null
    private val isRegularNumber get() = left == null

    var magnitude: Int = 0
        private set

    constructor(left: SnailfishNumber, right: SnailfishNumber) : this() {
        this.left = left
        this.right = right
        this.magnitude = 3 * left.magnitude + 2 * right.magnitude
    }

    constructor(magnitude: Int) : this() {
        this.magnitude = magnitude
    }

    override fun toString(): String {
        return if (isRegularNumber) { "$magnitude" } else { "[$left,$right]" }
    }

    operator fun plus(other: SnailfishNumber): SnailfishNumber {
        return SnailfishNumber(this.clone(), other.clone()).reduced()
    }

    private fun clone(): SnailfishNumber {
        return if (isRegularNumber) {
            SnailfishNumber(magnitude)
        } else {
            SnailfishNumber(left!!.clone(), right!!.clone())
        }
    }

    private fun reduced(): SnailfishNumber {
        do {
            val numberToSplit = explode()
        } while (split(numberToSplit))

        updateMagnitude()
        return this
    }

    private fun explode(): List<Pair<SnailfishNumber, Int>> {
        val toSplit = mutableListOf<Pair<SnailfishNumber, Int>>()
        fun addToSplit(number: SnailfishNumber, depth: Int) {
            if (toSplit.isEmpty() || toSplit.last().second < 4 && toSplit.last().first != number) {
                toSplit.add(number to depth)
            }
        }

        var prevRegularNumber: SnailfishNumber? = null
        var prevRegularNumberDepth = 0
        var add = 0

        fun traverse(number: SnailfishNumber, depth: Int = 0) {
            when {
                number.isRegularNumber -> {
                    number.magnitude += add
                    if (number.magnitude > 9) addToSplit(number, depth)
                    add = 0
                    prevRegularNumber = number
                    prevRegularNumberDepth = depth
                }
                depth == 4 -> {
                    check(number.left!!.isRegularNumber)
                    check(number.right!!.isRegularNumber)
                    prevRegularNumber?.let {
                        it.magnitude += number.left!!.magnitude + add
                        if (it.magnitude > 9) addToSplit(it, prevRegularNumberDepth)
                    }
                    add = number.right!!.magnitude
                    number.apply {
                        left = null
                        right = null
                        magnitude = 0
                    }
                    prevRegularNumber = number
                    prevRegularNumberDepth = depth
                }
                else -> {
                    traverse(number.left!!, depth + 1)
                    traverse(number.right!!, depth + 1)
                }
            }
        }
        traverse(this)

        return toSplit
    }

    private fun split(list: List<Pair<SnailfishNumber, Int>>): Boolean {
        if (list.isEmpty()) {
            return false
        }
        for ((num, depth) in list) {
            if (num.split(depth)) break
        }
        return true
    }

    private fun split(depth: Int): Boolean {
        if (magnitude < 10) return false
        left = SnailfishNumber(magnitude / 2)
        right = SnailfishNumber((magnitude + 1) / 2)
        updateMagnitude()
        return depth == 4 || left!!.split(depth + 1) || right!!.split(depth + 1)
    }

    private fun updateMagnitude() {
        if (!isRegularNumber) {
            left!!.updateMagnitude()
            right!!.updateMagnitude()
            magnitude = 3 * left!!.magnitude + 2 * right!!.magnitude
        }
    }

    companion object {
        fun fromString(string: String): SnailfishNumber {
            return build(string, 0).first
        }

        private fun build(string: String, index: Int): Pair<SnailfishNumber, Int> {
            var i = index
            while (string[i] == ',' || string[i] == ']') ++i
            return if (string[i] == '[') {
                val (leftNum, j) = build(string, i + 1)
                val (rightNum, k) = build(string, j)
                return SnailfishNumber(leftNum, rightNum) to k
            } else {
                var magnitude = 0
                while (string[i] in '0'..'9') {
                    magnitude = magnitude * 10 + (string[i] - '0')
                    ++i
                }
                SnailfishNumber(magnitude) to i
            }
        }
    }
}
