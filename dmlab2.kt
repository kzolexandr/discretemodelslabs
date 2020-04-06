package kzolexandr.discretemodelslabs

import java.util.*

class ChinesePostmanProblem internal constructor(vertices: Int) {
    private var n = 0
    var delta: IntArray
    lateinit var neg: IntArray
    private lateinit var pos: IntArray
    var arcs: Array<IntArray>
    var label: Array<Array<Vector<String?>?>>
    var f: Array<IntArray>
    var c: Array<FloatArray>
    var cheapestLabel: Array<Array<String?>>
    var defined: Array<BooleanArray>
    var path: Array<IntArray>
    var basicCost: Float

    fun solve() {
        leastCostPaths()
        checkValid()
        findUnbalanced()
        findFeasible()
    }

    fun addArc(lab: String?, u: Int, v: Int, cost: Float): ChinesePostmanProblem {
        if (!defined[u][v]) label[u][v] = Vector()
        label[u][v]!!.addElement(lab)
        basicCost += cost
        if (!defined[u][v] || c[u][v] > cost) {
            c[u][v] = cost
            cheapestLabel[u][v] = lab
            defined[u][v] = true
            path[u][v] = v
        }
        arcs[u][v]++
        delta[u]++
        delta[v]--
        return this
    }

    private fun leastCostPaths() {
        for (k in 0 until n) for (i in 0 until n) if (defined[i][k]) for (j in 0 until n) if (defined[k][j]
            && (!defined[i][j] || c[i][j] > c[i][k]
                    + c[k][j])
        ) {
            path[i][j] = path[i][k]
            c[i][j] = c[i][k] + c[k][j]
            defined[i][j] = true
            if (i == j && c[i][j] < 0) return
        }
    }

    private fun checkValid() {
        for (i in 0 until n) {
            for (j in 0 until n) if (!defined[i][j]) throw Error("Граф не зв'язний")
            if (c[i][i] < 0) throw Error("Граф має негативний цикл")
        }
    }

    fun cost(): Float {
        return basicCost + phi()
    }

    fun phi(): Float {
        var phi = 0f
        for (i in 0 until n) for (j in 0 until n) phi += c[i][j] * f[i][j]
        return phi
    }

    fun findUnbalanced() {
        var nn = 0
        var np = 0
        for (i in 0 until n) if (delta[i] < 0) nn++ else if (delta[i] > 0) np++
        neg = IntArray(nn)
        pos = IntArray(np)
        np = 0
        nn = np
        for (i in 0 until n)
            if (delta[i] < 0) neg[nn++] = i else if (delta[i] > 0) pos[np++] = i
    }

    private fun findFeasible() {
        val delta = IntArray(n)
        for (i in 0 until n) delta[i] = this.delta[i]
        for (u in neg.indices) {
            val i = neg[u]
            for (v in pos.indices) {
                val j = pos[v]
                f[i][j] = if (-delta[i] < delta[j]) -delta[i] else delta[j]
                delta[i] += f[i][j]
                delta[j] -= f[i][j]
            }
        }
    }

    private fun findPath(from: Int, f: Array<IntArray>): Int
    {
        for (i in 0 until n) if (f[from][i] > 0) return i
        return NONE
    }

    fun printCPT(startVertex: Int) {
        var v = startVertex
        val arcs = Array(n) { IntArray(n) }
        val f = Array(n) { IntArray(n) }
        for (i in 0 until n) for (j in 0 until n) {
            arcs[i][j] = this.arcs[i][j]
            f[i][j] = this.f[i][j]
        }
        while (true) {
            var u = v
            if (findPath(u, f).also { v = it } != NONE) {
                f[u][v]--
                var p: Int
                while (u != v) {
                    p = path[u][v]
                    print("${'a'+u}${'a'+p} ")
                    u = p
                }
            } else {
                val bridgeVertex = path[u][startVertex]
                if (arcs[u][bridgeVertex] == 0) break
                v = bridgeVertex
                for (i in 0 until n)
                    if (i != bridgeVertex && arcs[u][i] > 0) {
                        v = i
                        break
                    }
                arcs[u][v]--
                print("${'a'+u}${'a'+v} ")
            }
        }
    }

    companion object {
        const val NONE = -1
        @JvmStatic
        fun main(args: Array<String>) {
            OpenCPP.test()
        }
    }

    init {
        if (vertices.also { n = it } <= 0) throw Error("Граф порожній")
        delta = IntArray(n)
        defined = Array(n) { BooleanArray(n) }
        label = Array<Array<Vector<String?>?>>(n) { arrayOfNulls(n) }
        c = Array(n) { FloatArray(n) }
        f = Array(n) { IntArray(n) }
        arcs = Array(n) { IntArray(n) }
        cheapestLabel = Array(n) { arrayOfNulls<String>(n) }
        path = Array(n) { IntArray(n) }
        basicCost = 0f
    }
}

internal class OpenCPP(var N: Int) {
    internal inner class Arc(var lab: String, var u: Int, var v: Int, var cost: Float)

    var arcs = Vector<Arc>()
    fun addArc(lab: String?, u: Int, v: Int, cost: Float): OpenCPP {
        if (cost < 0) throw Error("Граф має непарні вершини")
        arcs.addElement(Arc(lab!!, u, v, cost))
        return this
    }

    fun printCPT(startVertex: Int): Float {
        var bestGraph: ChinesePostmanProblem? = null
        var g: ChinesePostmanProblem
        var bestCost = 0f
        var cost: Float
        var i = 0
        do {
            g = ChinesePostmanProblem(N + 1)
            for (j in arcs.indices) {
                val it = arcs.elementAt(j)
                g.addArc(it.lab, it.u, it.v, it.cost)
            }
            cost = g.basicCost
            g.findUnbalanced()
            g.addArc("'virtual start'", N, startVertex, cost)
            g.addArc(
                "'virtual end'",
                if (g.neg.isEmpty()) startVertex else g.neg[i], N, cost
            )
            g.solve()
            if (bestGraph == null || bestCost > g.cost()) {
                bestCost = g.cost()
                bestGraph = g
            }
        } while (++i < g.neg.size)
        bestGraph!!.printCPT(N)
        return cost + bestGraph.phi()
    }

    companion object {
        fun test() {
            val graph = OpenCPP(4)
            graph.addArc("a", 0, 1, 1f).addArc("b", 0, 2, 1f).addArc("c", 1, 2, 1f)
                .addArc("d", 1, 3, 1f).addArc("e", 2, 3, 1f).addArc("f", 3, 0, 1f)
            println("Вхідний граф:")
            println("ab ac bc bd cd da")
            println()
            var besti = 0
            var bestCost = 0f
            for (i in 0..3) {
                println("Рішення №${i+1}")
                val c = graph.printCPT(i)
                println("\nВартість = $c\n")
                if (i == 0 || c < bestCost) {
                    bestCost = c
                    besti = i
                }
            }
            println("Найкраще рішення:")
            graph.printCPT(besti)
            println("\nНайкраща вартість = $bestCost")
        }
    }
}