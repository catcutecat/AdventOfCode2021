fun main() {
    fun part1(input: List<String>): Int {
        return PacketDecoder(input).versionSum
    }

    fun part2(input: List<String>): Long {
        return PacketDecoder(input).valueSum
    }

    val testInput1 = readInput("Day16_test_input_1")
    check(part1(testInput1) == (6 + 9 + 14 + 16 + 12 + 23 + 31))

    val testInput2 = readInput("Day16_test_input_2")
    check(part2(testInput2) == (3L + 54L + 7L + 9L + 1L + 0L + 0L + 1L))

    val input = readInput("Day16_input")
    println(part1(input)) //917
    println(part2(input)) //2536453523344
}

class PacketDecoder(input: List<String>) {

    private val packets = input.map { Packet.fromHexString(it) }
    val versionSum = packets.sumOf { it.versionSum }
    val valueSum = packets.sumOf { it.value }

    private sealed interface Packet {
        val version: Int
        val typeId: Int
        val value: Long
        val versionSum: Int

        private data class LiteralValue(
            override val version: Int,
            override val value: Long
        ) : Packet {
            companion object {
                const val TYPE_ID: Int = 4
            }
            override val typeId: Int = TYPE_ID
            override val versionSum: Int = version
        }

        private data class Operator(
            override val version: Int,
            override val typeId: Int,
            val subPacket: List<Packet>
        ) : Packet {
            override val versionSum: Int = version + subPacket.sumOf { it.versionSum }
            override val value: Long = when(typeId) {
                0 -> subPacket.sumOf { it.value }
                1 -> subPacket.fold(1) { acc, packet -> acc * packet.value }
                2 -> subPacket.minOf { it.value }
                3 -> subPacket.maxOf { it.value }
                5 -> (subPacket.first().value > subPacket.last().value).longValue
                6 -> (subPacket.first().value < subPacket.last().value).longValue
                7 -> (subPacket.first().value == subPacket.last().value).longValue
                else -> error("Invalid type ID: $typeId")
            }
        }

        companion object {
            fun fromHexString(hex: String): Packet {
                return fromBinary(hexToBinary(hex))
            }

            private fun fromBinary(code: BooleanArray): Packet {
                var index = 0

                fun build(): Packet {
                    val version = getValue(code, index + 0, index + 2)
                    val typeId = getValue(code, index + 3, index + 5)
                    index += 6
                    return when {
                        typeId == LiteralValue.TYPE_ID -> {
                            var value = 0L
                            do {
                                val hasNext = code[index + 0]
                                for (i in 1..4) {
                                    value = (value shl 1) or code[index + i].longValue
                                }
                                index += 5
                            } while (hasNext)
                            LiteralValue(version, value)
                        }
                        !code[index + 0] -> {
                            index += 1
                            val endIndex = index + 15 + getValue(code, index + 0, index + 14)
                            index += 15
                            val subPackets = mutableListOf<Packet>()
                            while (index < endIndex) {
                                subPackets.add(build())
                            }
                            Operator(version, typeId, subPackets)
                        }
                        else -> {
                            index += 1
                            val subPacketSize = getValue(code, index + 0, index + 10)
                            index += 11
                            val subPackets = mutableListOf<Packet>()
                            repeat(subPacketSize) {
                                subPackets.add(build())
                            }
                            Operator(version, typeId, subPackets)
                        }
                    }
                }

                return build()
            }

            private fun getValue(code: BooleanArray, start: Int, end: Int): Int {
                var res = 0
                for (i in start..end) {
                    res = (res shl 1) or code[i].intValue
                }
                return res
            }

            private fun hexToBinary(hex: String): BooleanArray {
                return hex.foldIndexed(BooleanArray(hex.length shl 2)) { index, acc, c ->
                    val value = c.hexValue
                    val startIndex = index shl 2
                    for (i in 0..3) {
                        acc[startIndex + i] = value and (8 shr i) != 0
                    }
                    acc
                }
            }

            private val Char.hexValue: Int
                get() = when (this) {
                    in '0'..'9' -> this - '0'
                    in 'A'..'F' -> this - 'A' + 10
                    else -> error("Not an hexadecimal character: $this")
                }

            private val Boolean.intValue: Int get() = if (this) { 1 } else { 0 }
            private val Boolean.longValue: Long get() = if (this) { 1L } else { 0L }
        }
    }
}
