package kzolexandr.discretemodelslabs

fun main() {
    val graph = createGraph()
    println("Заданий граф:")
    println(graph)

    val spanningTree = buildSpanningTree(graph)
    println("Максимальне остове дерево за алгоритом Прима:")
    println(spanningTree)
}

private fun createGraph() = Graph().apply {
    addNodes(
        listOf(
            Node('a', 'b', 2),
            Node('a', 'h', 1),
            Node('a', 'i', 8),
            Node('b', 'c', 2),
            Node('b', 'i', 10),
            Node('c', 'd', 1),
            Node('c', 'i', 8),
            Node('d', 'e', 3),
            Node('d', 'i', 8),
            Node('e', 'i', 7),
            Node('f', 'i', 6),
            Node('g', 'h', 4),
            Node('g', 'i', 5),
            Node('h', 'i', 6)
        )
    )
}

private fun buildSpanningTree(graph: Graph): Graph {
    val spanningTree = Graph()
    val excludedNodes = mutableSetOf<Node>()

    graph.getRelatedNodes(graph.getFirstEdge()).getMaxWeightNode().also {
        spanningTree.addNode(it)
        excludedNodes.add(it)
    }

    while (true) {
        val relatedNodes = graph.getRelatedNodes(spanningTree.getEdges()).minus(excludedNodes)
        if (relatedNodes.isEmpty()) break

        relatedNodes.getMaxWeightNode().let {
            excludedNodes.add(it)
            spanningTree.addNode(it)
            if (isCyclic(spanningTree)) {
                spanningTree.removeNode(it)
            }
        }
    }

    return spanningTree
}

private fun isCyclic(spanningTree: Graph): Boolean {
    val graph = Graph()

    spanningTree.getAllNodes().forEach {
        if (graph.getEdges().containsAll(listOf(it.firstEdge, it.secondEdge))) return true
        graph.addNode(it)
    }

    return false
}

fun Collection<Node>.getMaxWeightNode(): Node {
    var maxWeightNode = first()
    forEach {
        if (it.weight > maxWeightNode.weight) maxWeightNode = it
    }
    return maxWeightNode
}

class Graph {
    private val adjacencyNodesTree = mutableMapOf<Char, MutableSet<Node>>()

    override fun toString() = getAllNodes().toString()

    fun addNode(node: Node) {
        listOf(node.firstEdge, node.secondEdge).forEach {
            adjacencyNodesTree.getOrPut(it) { mutableSetOf() }.add(node)
        }
    }

    fun addNodes(nodes: Collection<Node>) {
        nodes.forEach { addNode(it) }
    }

    fun removeNode(node: Node) {
        listOf(node.firstEdge, node.secondEdge).forEach {
            adjacencyNodesTree[it]!!.apply {
                remove(node)
                if (isEmpty()) adjacencyNodesTree.remove(it)
            }
        }
    }

    fun getAllNodes() = mutableSetOf<Node>().apply {
        adjacencyNodesTree.forEach { adjNodes ->
            adjNodes.value.forEach { add(it) }
        }
    }

    fun getRelatedNodes(edge: Char) = adjacencyNodesTree[edge]!!

    fun getRelatedNodes(edges: Collection<Char>) = mutableSetOf<Node>().apply {
        edges.forEach { edge ->
            adjacencyNodesTree[edge]!!.forEach {
                add(it)
            }
        }
    }

    fun getFirstEdge() = adjacencyNodesTree.keys.first()

    fun getEdges() = adjacencyNodesTree.keys
}

class Node(
    val firstEdge: Char,
    val secondEdge: Char,
    val weight: Int
) {
    override fun toString(): String = "$firstEdge$secondEdge $weight"
}