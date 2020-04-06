package kzolexandr.discretemodelslabs

import java.util.*

fun main() {
    val graph = createGraph()
    println("Вхідна матриця:\n$graph")

    var state = TspState(0, null, true)
    val states = Stack<TspState>().apply { push(state) }
    graph.enter(state.numOfCities)
    
    var shortest: TspState? = null
    state = states.pop()
    
    while (!state.isStartPoint || state.nextIndex < graph.count) {
        val index = state.nextIndex++
        
        if (index >= graph.count) {
            graph.leave(state.numOfCities)
            state = states.pop()
        } else if (graph.hasNode(state.numOfCities, index) && graph.enter(index)) {
            states.push(state)
            state = TspState(index, state)
        }

        if (graph.allVisited()) {
            if (shortest == null) shortest = state
            else if (shortest.calculateLength(graph) > state.calculateLength(graph)) shortest = state
        }
    }

    val result = shortest.toString()
    println("Результат:")
    println(result)
    for (s in result.split(" ")) {
        print((s.toInt() + 65).toChar() + " ")
    }
}

private fun createGraph() = AdjacencyGraph(7).apply {
    addNode('A', 'B', 7)
    addNode('A', 'D', 5)
    addNode('B', 'C', 8)
    addNode('B', 'D', 9)
    addNode('B', 'E', 7)
    addNode('C', 'E', 5)
    addNode('D', 'E', 9)
    addNode('D', 'F', 6)
    addNode('E', 'F', 8)
    addNode('E', 'G', 9)
    addNode('F', 'G', 5)
}

class AdjacencyGraph(val count: Int) {
    private var matrix = arrayOf<IntArray>()
    private var marks = booleanArrayOf()

    init {
        for (i in 0 until count) {
            var array = intArrayOf()
            for (j in 0 until count) {
                array += 0
            }
            matrix += array
            marks += false
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (i in matrix.indices) {
            for (element in matrix[i]) {
                result.append("$element ")
            }
            result.append("\n")
        }
        return result.toString()
    }

    fun addNode(a: Char, b: Char, weight: Int) {
        val first = (a - 65).toInt()
        val second = (b - 65).toInt()
        matrix[first][second] = weight
        matrix[second][first] = weight
    }

    fun getNode(a: Int, b: Int) = matrix[a][b]

    fun hasNode(a: Int, b: Int) = matrix[a][b] != 0

    fun enter(pos: Int) = if (marks[pos]) {
        false
    } else {
        marks[pos] = true
        true
    }

    fun leave(pos: Int) {
        marks[pos] = false
    }

    fun allVisited() = !marks.contains(false)
}

class TspState(
    val numOfCities: Int,
    private val previousState: TspState?,
    val isStartPoint: Boolean = false
) {
    var nextIndex = 0

    fun calculateLength(graph: AdjacencyGraph): Int {
        var currentState = this
        var sum = 0

        while (currentState.previousState != null) {
            sum += graph.getNode(currentState.previousState!!.numOfCities, currentState.numOfCities)
            currentState = currentState.previousState!!
        }

        return sum
    }

    override fun toString(): String = if (previousState == null) {
        numOfCities.toString()
    } else {
        "$previousState $numOfCities"
    }
}