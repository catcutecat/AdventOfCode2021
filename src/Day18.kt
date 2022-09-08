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

sealed class SnailfishNumber {
    abstract val magnitude: Int
    protected var onTransformed: (SnailfishNumber) -> Unit = { throw Exception() }

    protected abstract fun clone(): SnailfishNumber

    private class PairNumber private constructor() : SnailfishNumber() {

        override val magnitude: Int get() = 3 * left.magnitude + 2 * right.magnitude

        override fun clone(): SnailfishNumber = create(left.clone(), right.clone())
        override fun toString(): String = "[$left,$right]"

        lateinit var left: SnailfishNumber
            private set
        lateinit var right: SnailfishNumber
            private set

        fun explode(): ExplodeResult = ExplodeResult(RegularNumber(0), left.magnitude, right.magnitude).also {
            onTransformed(it.regularNumber)
        }

        data class ExplodeResult(
            val regularNumber: RegularNumber,
            val leftValue: Int,
            val rightValue: Int
        )

        companion object {
            fun create(leftNumber: SnailfishNumber, rightNumber: SnailfishNumber) = PairNumber().apply {
                left = leftNumber
                right = rightNumber
            }.apply {
                left.onTransformed = {
                    it.onTransformed = left.onTransformed
                    left = it
                }
                right.onTransformed = {
                    it.onTransformed = right.onTransformed
                    right = it
                }
            }
        }
    }

    private class RegularNumber(private var value: Int) : SnailfishNumber() {
        override val magnitude: Int get() = value

        override fun clone(): SnailfishNumber = RegularNumber(value)
        override fun toString(): String = "$value"

        // return: true if it needs to stop split
        fun split(depth: Int): Boolean {
            if (value < 10) return false

            val left = RegularNumber(value / 2)
            val right = RegularNumber((value + 1) / 2)
            PairNumber.create(left, right).apply(onTransformed)

            return depth == MAX_DEPTH || left.split(depth + 1) || right.split(depth + 1)
        }

        fun add(num: Int) = run { value += num }
    }

    operator fun plus(other: SnailfishNumber): SnailfishNumber =
        generateSequence(PairNumber.create(this.clone(), other.clone()), (::reduce)).last()

    companion object {
        private const val MAX_DEPTH = 4

        fun fromString(string: String): SnailfishNumber {
            var i = 0
            fun parse(): SnailfishNumber {
                return if (string[i] == '[') {
                    ++i
                    val left = parse().also { check(string[i++] == ',') }
                    val right = parse().also { check(string[i++] == ']') }
                    PairNumber.create(left, right)
                } else {
                    var value = 0
                    while (string[i] in '0'..'9') {
                        value = value * 10 + (string[i++] - '0')
                    }
                    RegularNumber(value)
                }
            }
            return parse().also { check(i == string.length) }
        }

        private fun reduce(number: SnailfishNumber): SnailfishNumber? {
            val splitList = mutableListOf<Pair<RegularNumber, Int>>()
            var prevRegularNumber: Pair<RegularNumber, Int>? = null
            var toAdd = 0

            fun updatePrevRegularNumber(number: RegularNumber, depth: Int) {
                prevRegularNumber?.takeIf { it.first.magnitude > 9 }?.let { splitList.add(it) }
                prevRegularNumber = number to depth
            }

            fun traverse(number: SnailfishNumber, depth: Int) {
                when (number) {
                    is RegularNumber -> {
                        number.add(toAdd)
                        updatePrevRegularNumber(number, depth)
                        toAdd = 0
                    }

                    is PairNumber -> {
                        if (depth == MAX_DEPTH) {
                            val (regularNumber, left, right) = number.explode()
                            prevRegularNumber?.first?.add(left + toAdd)
                            updatePrevRegularNumber(regularNumber, depth)
                            toAdd = right
                        } else {
                            traverse(number.left, depth + 1)
                            traverse(number.right, depth + 1)
                        }
                    }
                }
            }
            traverse(number, 0)
            prevRegularNumber?.takeIf { it.first.magnitude > 9 }?.let { splitList.add(it) }

            for ((num, depth) in splitList) {
                if (num.split(depth)) return number
            }
            return if (splitList.isEmpty()) null else number
        }
    }
}
