package kzolexandr.discretemodelslabs

import java.util.*
import kotlin.collections.ArrayList

fun main() {
    val graph = createGraph()
    println("Вхідні дані:")
    println(graph)
    val flows = ArrayList<Flow>()
    println(start(graph, flows))
    var totalWeight = 0
    for (flow in flows) {
        totalWeight += flow.weight
    }
    println("Величина потоку $totalWeight")
}

private fun start(
    graph: ComparableGraph,
    flows: ArrayList<Flow>
): ArrayList<Flow> {
    val start: Char = graph.startPoint
    val end: Char = graph.endPoint
    val availablePoint: MutableList<Char> = graph.pointsList!!
    var point = start
    val flow = Flow()
    while (point < end) {
        try {
            availablePoint.removeAt(availablePoint.indexOf(point))
        } catch (ignored: ArrayIndexOutOfBoundsException) {
        }
        var maxEdge = Edge('A', 'A', 0)
        for (edge in graph.getAdjacentEdges(point)) {
            if (edge.weight > maxEdge.weight) {
                for (p in availablePoint) {
                    if (edge.havePoint(p)) {
                        maxEdge = edge
                    }
                }
            }
        }
        if (maxEdge.weight == 0) {
            if (point == start) {
                return flows
            }
            val previousPoint =
                if (flow[flow.size() - 1].a == point) flow[flow.size() - 1].b else flow[flow.size() - 1].a
            point = previousPoint
            flow.removeLast()
        } else {
            flow.add(maxEdge)
            point = if (point == maxEdge.a) maxEdge.b else maxEdge.a
        }
    }
    var minWeight = Int.MAX_VALUE
    for (edge in flow.edges) {
        if (minWeight > edge.weight) {
            minWeight = edge.weight
        }
    }
    flow.weight = minWeight
    for (edge in flow.edges) {
        edge.weight = edge.weight - minWeight
    }
    flows.add(flow)
    return start(graph, flows)
}

private fun createGraph(): ComparableGraph = ComparableGraph(mutableListOf(
    Edge('A', 'B', 7),
    Edge('A', 'D', 25),
    Edge('B', 'C', 8),
    Edge('B', 'D', 9),
    Edge('B', 'E', 7),
    Edge('C', 'E', 5),
    Edge('D', 'E', 15),
    Edge('D', 'F', 6),
    Edge('E', 'F', 8),
    Edge('E', 'G', 9),
    Edge('F', 'G', 11)
))


class Edge(
    var a: Char,
    var b: Char,
    var weight: Int
) {
    fun havePoint(a: Char) = if (this.a == a) true else b == a

    override fun toString(): String {
        return "Edge{$a $b $weight}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val edge = other as Edge
        return a == edge.a && b == edge.b && weight == edge.weight
    }

    override fun hashCode(): Int {
        return Objects.hash(a, b, weight)
    }
}

class Flow {
    var edges = mutableListOf<Edge>()
    var weight = 0

    operator fun get(index: Int) = edges[index]

    fun size() = edges.size

    fun add(edge: Edge) {
        edges.add(edge)
    }

    override fun toString() = "Flow{edges=$edges, weight=$weight}"

    fun removeLast() {
        edges.removeAt(edges.size - 1)
    }
}


class ComparableGraph(private var edges: MutableList<Edge>) : Comparable<ComparableGraph?> {
    var pointsList: MutableList<Char>? = null

    private val weight: Int
        get() {
            var weight = 0
            for (edge in edges) {
                weight += edge.weight
            }
            return weight
        }

    val startPoint: Char
        get() {
            var point = 'A'
            while (point < 'Z') {
                if (pointsList!!.contains(point)) {
                    return point
                }
                point++
            }
            return ' '
        }

    val endPoint: Char
        get() {
            var c = 'A'
            a@ while (c < 'Z') {
                for (i in edges.indices) {
                    if (edges[i].havePoint(c)) {
                        c++
                        continue@a
                    }
                }
                break
            }
            return --c
        }

    private fun updatePointList() {
        pointsList = ArrayList()
        var c = 'A'
        while (c < 'Z') {
            for (i in edges.indices) {
                if (edges[i].havePoint(c)) {
                    pointsList!!.add(c)
                    break
                }
            }
            c++
        }
    }

    override fun toString(): String {
        return "Graph{" +
                "edges=" + edges.toString() +
                '}'
    }

    fun getAdjacentEdges(a: Char): ArrayList<Edge> {
        val adjacentEdges =
            ArrayList<Edge>()
        for (edge in edges) {
            if (edge.havePoint(a)) {
                adjacentEdges.add(edge)
            }
        }
        return adjacentEdges
    }

    override fun compareTo(other: ComparableGraph?) = when {
        other == null -> -1
        weight == other.weight -> 0
        weight < other.weight -> -1
        else -> 1
    }

    init {
        updatePointList()
    }
}