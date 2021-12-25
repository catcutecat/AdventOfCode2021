import kotlin.math.abs

fun main() {
//    fun part1(input: List<String>): Int {
//        return AmphipodDiagram(input).halfDiagramEnergy
//    }
//
//    fun part2(input: List<String>): Int {
//        return AmphipodDiagram(input).fullDiagramEnergy
//    }

    val testInput = AmphipodDiagram(readInput("Day23_test_input"))
    check(testInput.halfDiagramEnergy == 12521)
    check(testInput.fullDiagramEnergy == 44169)

    val input = AmphipodDiagram(readInput("Day23_input"))
    println(input.halfDiagramEnergy) //15111
    println(input.fullDiagramEnergy) //47625
}

class AmphipodDiagram(input: List<String>) {

    val halfDiagramEnergy: Int
    val fullDiagramEnergy: Int

    init {
        fun solve(diagram: Array<Space>): Int {
            var res = Int.MAX_VALUE

            fun move(energyCost: Int) {
                if (energyCost >= res) {
                    return
                }
                if (diagram.all { it.isAllSet }) {
                    res = energyCost
                    return
                }

                for (from in diagram.indices) {
                    (diagram[from].state as? State.Occupied)?.let { (who) ->
                        arrayOf(
                            from + 1..diagram.lastIndex,
                            from - 1 downTo 0
                        ).forEach { toRange ->
                            for (to in toRange) {
                                if (!Space.isSameType(diagram[from], diagram[to])) {
                                    (diagram[to].state as? State.Open)?.let { (acceptTypeMask) ->
                                        if (who.mask and acceptTypeMask > 0) {
                                            val action = MoveAmphipod(diagram[from], diagram[to])
                                            move(energyCost + action.doAction())
                                            action.undoAction()
                                            if (diagram[to] is Room) {
                                                return
                                            }
                                        }
                                    }
                                }
                                if (!diagram[to].canPassThrough) {
                                    break
                                }
                            }
                        }
                    }
                }
            }

            move(0)
            return res
        }

        var index: Int = -1
        halfDiagramEnergy = solve(
            arrayOf(
                Hall(++index),
                Hall(++index),
                Room(++index, Amphipod.A, arrayOf(input[2][3], input[3][3])),
                Hall(++index),
                Room(++index, Amphipod.B, arrayOf(input[2][5], input[3][5])),
                Hall(++index),
                Room(++index, Amphipod.C, arrayOf(input[2][7], input[3][7])),
                Hall(++index),
                Room(++index, Amphipod.D, arrayOf(input[2][9], input[3][9])),
                Hall(++index),
                Hall(++index)
            )
        )

        index = -1
        fullDiagramEnergy = solve(
            arrayOf(
                Hall(++index),
                Hall(++index),
                Room(++index, Amphipod.A, arrayOf(input[2][3], 'D', 'D', input[3][3])),
                Hall(++index),
                Room(++index, Amphipod.B, arrayOf(input[2][5], 'C', 'B', input[3][5])),
                Hall(++index),
                Room(++index, Amphipod.C, arrayOf(input[2][7], 'B', 'A', input[3][7])),
                Hall(++index),
                Room(++index, Amphipod.D, arrayOf(input[2][9], 'A', 'C', input[3][9])),
                Hall(++index),
                Hall(++index)
            )
        )
    }

    private sealed interface Space {
        val position: Int
        val isAllSet: Boolean
        val canPassThrough: Boolean
        val state: State

        fun moveIn(who: Amphipod): MoveInResult
        fun moveOut(): MoveOutResult

        data class MoveInResult(
            val energyCost: Int
        )
        data class MoveOutResult(
            val who: Amphipod,
            val energyCost: Int
        )

        companion object {
            fun isSameType(a: Space, b: Space): Boolean = when (a) {
                is Hall -> b is Hall
                is Room -> b is Room
            }
        }
    }

    private sealed interface State {
        data class Open(val acceptTypeMask: Int): State
        data class Occupied(val who: Amphipod): State
        object Freeze: State
    }

    private class MoveAmphipod(val from: Space, val to: Space) {
        fun doAction(): Int = from.moveOut().let { (who, energyCost) ->
            to.moveIn(who).energyCost + energyCost + who.energyCost * abs(from.position - to.position)
        }
        fun undoAction(): Int = to.moveOut().let { (who, energyCost) ->
            from.moveIn(who).energyCost + energyCost + who.energyCost * abs(from.position - to.position)
        }
    }

    private class Hall(
        override val position: Int
    ): Space {
        override val isAllSet: Boolean get() = state is State.Open
        override val canPassThrough: Boolean get() = state is State.Open
        override var state: State = State.Open(Amphipod.allMask)
            private set

        override fun moveIn(who: Amphipod): Space.MoveInResult {
            assert(state is State.Open)
            state = State.Occupied(who)
            return Space.MoveInResult(0)
        }

        override fun moveOut(): Space.MoveOutResult {
            assert(state is State.Occupied)
            val who = (state as State.Occupied).who
            state = State.Open(Amphipod.allMask)
            return Space.MoveOutResult(who, 0)
        }
    }

    private class Room(
        override val position: Int,
        val type: Amphipod,
        initialAmphipods: Array<Char>
    ): Space {
        override val isAllSet: Boolean get() = state == State.Freeze
        override val canPassThrough: Boolean = true
        override val state: State
            get() = when {
                isClean && isFull -> State.Freeze
                isClean -> State.Open(type.mask)
                else -> State.Occupied((rooms.first { it is RoomState.Occupied } as RoomState.Occupied).who)
            }

        private val isClean: Boolean get() = dirtyCount == 0
        private val isFull: Boolean get() = occupiedCount == rooms.size
        private val isEmpty: Boolean get() = occupiedCount == 0

        private val rooms: Array<RoomState>
        private var dirtyCount: Int
        private var occupiedCount: Int

        init {
            rooms = Array(initialAmphipods.size) {
                RoomState.Occupied(Amphipod.fromChar(initialAmphipods[it]))
            }
            dirtyCount = rooms.sumOf { if (it is RoomState.Occupied && it.who != type) 1.toInt() else 0 }
            occupiedCount = rooms.count { it is RoomState.Occupied }
        }

        override fun moveIn(who: Amphipod): Space.MoveInResult {
            assert(!isFull)
            for (i in rooms.indices.reversed()) {
                if (rooms[i] == RoomState.Empty) {
                    rooms[i] = RoomState.Occupied(who)
                    if (who != type) ++dirtyCount
                    ++occupiedCount
                    return Space.MoveInResult(who.energyCost * (i + 1))
                }
            }
            error("The room is full.")
        }

        override fun moveOut(): Space.MoveOutResult {
            assert(!isEmpty)
            for (i in rooms.indices) {
                (rooms[i] as? RoomState.Occupied)?.let { (who) ->
                    rooms[i] = RoomState.Empty
                    if (who != type) --dirtyCount
                    --occupiedCount
                    return Space.MoveOutResult(who, who.energyCost * (i + 1))
                }
            }
            error("The room is empty.")
        }

        private sealed interface RoomState {
            object Empty: RoomState
            data class Occupied(val who: Amphipod): RoomState
        }
    }

    private enum class Amphipod(val energyCost: Int, val mask: Int) {
        A(1, 1 shl 0),
        B(10, 1 shl 1),
        C(100, 1 shl 2),
        D(1000, 1 shl 3);

        companion object {
            fun fromChar(c: Char): Amphipod = when (c) {
                'A' -> A
                'B' -> B
                'C' -> C
                'D' -> D
                else -> error("No such Amphipod type: $c")
            }

            val allMask = A.mask or B.mask or C.mask or D.mask
        }
    }
}
