package kzolexandr.discretemodelslabs

import java.util.*

fun main() {
    val graph1 = createGraph1()
    val graph2 = createGraph2()

    println("Матриця інцидентності першого графа:\n${matrixToString(graph1.adj)}")
    println("Матриця інцидентності другого графа:\n${matrixToString(graph2.adj)}")

    print("Результат: ")
    println(if (graph1.isomorphic(graph2)) "Графи ізоморфні" else "Графи не ізоморфні")
}

private fun createGraph1() = NonOrientedGraph(8).apply {
    adj = arrayOf(
        intArrayOf(0, 0, 0, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 0, 1, 1, 0, 1),
        intArrayOf(0, 0, 0, 0, 1, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1, 1),
        intArrayOf(1, 1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 1, 0, 0, 0, 0),
        intArrayOf(1, 0, 1, 1, 0, 0, 0, 0),
        intArrayOf(0, 1, 1, 1, 0, 0, 0, 0)
    )
}

private fun createGraph2() = NonOrientedGraph(8).apply {
    adj = arrayOf(
        intArrayOf(0, 1, 0, 1, 1, 0, 0, 0),
        intArrayOf(1, 0, 1, 0, 0, 1, 0, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 1, 0),
        intArrayOf(1, 0, 1, 0, 0, 0, 0, 1),
        intArrayOf(1, 0, 0, 0, 0, 1, 0, 1),
        intArrayOf(0, 1, 0, 0, 1, 0, 1, 0),
        intArrayOf(0, 0, 1, 0, 0, 1, 0, 1),
        intArrayOf(0, 0, 0, 1, 1, 0, 1, 0)
    )
}

private fun matrixToString(matrix: Array<IntArray>) = StringBuilder().apply {
    matrix.forEach { array ->
        array.forEach {
            append("$it ")
        }
        append("\n")
    }
}.toString()

class NonOrientedGraph(
    var v: Int
) {
    var adj = Array(v) { IntArray(v) } //adjacency matrix
    private var v20 = 0
    private var v21= 0

    private fun recIsomorphic(
        two: NonOrientedGraph,
        toVisit: LinkedList<Any>,
        visited: BooleanArray,
        nbVisited: Int,
        currentPermutation: IntArray,
        print: Boolean
    ): Boolean {
        if (print) {
            println("call")
        }
        if (toVisit.isEmpty()) {
            if (print) {
                println("all visited:")
                for (bloum in 0 until v) {
                    if (visited[bloum]) {
                        print(" $bloum")
                    }
                }
            }
            var x = 0
            var y = 0
            while (x < v && adj[x][y] == two.adj[currentPermutation[x]][currentPermutation[y]]) {
                y++
                if (y == v) {
                    y = 0
                    x++
                }
            }
            return true
        }
        val nextVertex = toVisit.remove() as Int
        if (visited[nextVertex]) {
            return recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
        }
        visited[nextVertex] = true
        if (getOutDegree(nextVertex) == 0) {
            return if (two.getOutDegree(currentPermutation[nextVertex]) == 0) {
                recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
            } else {
                false
            }
        }
        if (getOutDegree(nextVertex) == 1) {
            return if (two.getOutDegree(currentPermutation[nextVertex]) == 1) {
                var sonThis = 0
                while (adj[nextVertex][sonThis] == 0) {
                    sonThis++
                }
                var sonTwo = 0
                while (two.adj[currentPermutation[nextVertex]][sonTwo] == 0) {
                    sonTwo++
                }
                if (currentPermutation[sonThis] >= 0) {
                    if (currentPermutation[sonThis] != sonTwo) {
                        false
                    } else {
                        toVisit.add(sonThis)
                        recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                    }
                } else {
                    toVisit.add(sonThis)
                    var delp = 0
                    while (delp < v && currentPermutation[delp] != sonTwo) {
                        delp++
                    }
                    if (delp < v) {
                        return false
                    }
                    currentPermutation[sonThis] = sonTwo
                    recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                }
            } else {
                false
            }
        }
        if (getOutDegree(nextVertex) == 2) {
            return if (two.getOutDegree(currentPermutation[nextVertex]) == 2) {
                var sonThis1 = 0
                while (adj[nextVertex][sonThis1] == 0) {
                    sonThis1++
                }
                var sonThis2 = sonThis1 + 1
                if (adj[nextVertex][sonThis1] == 2) {
                    sonThis2 = sonThis1
                } else {
                    while (adj[nextVertex][sonThis2] == 0) {
                        sonThis2++
                    }
                }
                var sonTwo1 = 0
                while (two.adj[currentPermutation[nextVertex]][sonTwo1] == 0) {
                    sonTwo1++
                }
                var sonTwo2 = sonTwo1 + 1
                if (two.adj[currentPermutation[nextVertex]][sonTwo1] == 2) {
                    sonTwo2 = sonTwo1
                } else {
                    while (two.adj[currentPermutation[nextVertex]][sonTwo2] == 0) {
                        sonTwo2++
                    }
                }
                if (sonTwo2 == sonTwo1 != (sonThis1 == sonThis2)) {
                    return false
                }
                if (currentPermutation[sonThis1] >= 0) {
                    if (currentPermutation[sonThis1] == sonTwo2) { //then sonThis2 has to be associated with sonTwo1
                        if (currentPermutation[sonThis2] >= 0) {
                            if (currentPermutation[sonThis2] == sonTwo1) {
                                toVisit.add(sonThis1)
                                toVisit.add(sonThis2)
                                recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                            } else {
                                false
                            }
                        } else {
                            toVisit.add(sonThis1)
                            toVisit.add(sonThis2)
                            var delp = 0
                            while (delp < v && currentPermutation[delp] != sonTwo1) {
                                delp++
                            }
                            if (delp < v) {
                                return false
                            }
                            currentPermutation[sonThis2] = sonTwo1
                            recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                        }
                    } else {
                        if (currentPermutation[sonThis1] == sonTwo1) { //then sonThis2 has to be associated with sonTwo2
                            if (currentPermutation[sonThis2] >= 0) {
                                if (currentPermutation[sonThis2] == sonTwo2) {
                                    toVisit.add(sonThis1)
                                    toVisit.add(sonThis2)
                                    recIsomorphic(
                                        two,
                                        toVisit,
                                        visited,
                                        nbVisited + 1,
                                        currentPermutation,
                                        print
                                    )
                                } else {
                                    false
                                }
                            } else {
                                toVisit.add(sonThis1)
                                toVisit.add(sonThis2)
                                var delp = 0
                                while (delp < v && currentPermutation[delp] != sonTwo2) {
                                    delp++
                                }
                                if (delp < v) {
                                    return false
                                }
                                currentPermutation[sonThis2] = sonTwo2
                                recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                            }
                        } else {
                            false
                        }
                    }
                } else {
                    if (currentPermutation[sonThis2] >= 0) {
                        if (currentPermutation[sonThis2] == sonTwo1) {
                            toVisit.add(sonThis1)
                            toVisit.add(sonThis2)
                            var delp = 0
                            while (delp < v && currentPermutation[delp] != sonTwo2) {
                                delp++
                            }
                            if (delp < v) {
                                return false
                            }
                            currentPermutation[sonThis1] = sonTwo2
                            recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                        } else {
                            if (currentPermutation[sonThis2] == sonTwo2) {
                                toVisit.add(sonThis1)
                                toVisit.add(sonThis2)
                                var delp = 0
                                while (delp < v && currentPermutation[delp] != sonTwo1) {
                                    delp++
                                }
                                if (delp < v) {
                                    return false
                                }
                                currentPermutation[sonThis1] = sonTwo1
                                recIsomorphic(two, toVisit, visited, nbVisited + 1, currentPermutation, print)
                            } else {
                                false
                            }
                        }
                    } else {
                        toVisit.add(sonThis1)
                        toVisit.add(sonThis2)
                        val toVisit2 = toVisit.clone() as LinkedList<Any>
                        val visited2 = visited.clone()
                        val toVisit3 = toVisit.clone() as LinkedList<Any>
                        val visited3 = visited.clone()
                        val currentPermutation2 = currentPermutation.clone()
                        val currentPermutation3 = currentPermutation.clone()
                        var delp = 0
                        while (delp < v && currentPermutation[delp] != sonTwo1) {
                            delp++
                        }
                        if (delp < v) {
                            return false
                        }
                        delp = 0
                        while (delp < v && currentPermutation[delp] != sonTwo2) {
                            delp++
                        }
                        if (delp < v) {
                            return false
                        }
                        currentPermutation3[sonThis1] = sonTwo1
                        currentPermutation3[sonThis2] = sonTwo2
                        currentPermutation2[sonThis1] = sonTwo2
                        currentPermutation2[sonThis2] = sonTwo1
                        val firstTry =
                            recIsomorphic(two, toVisit3, visited3, nbVisited + 1, currentPermutation3, print)
                        if (firstTry) {
                            true
                        } else {
                            recIsomorphic(two, toVisit2, visited2, nbVisited + 1, currentPermutation2, print)
                        }
                    }
                }
            } else {
                false
            }
        }
        return true
    }

    fun isomorphic(two: NonOrientedGraph): Boolean {
        if (v != two.v || v20 != two.v20 || v21 != two.v21) return false
        var i = 0
        var j = 0
        while (i < v && adj[i][j] == two.adj[i][j]) {
            j++
            if (j == v) {
                j = 0
                i++
            }
        }
        return if (i == v) {
            true
        } else {
            val toVisit: LinkedList<Any> = LinkedList()
            toVisit.add(0)
            val visited = BooleanArray(v)
            val currentPermutation = IntArray(v)
            currentPermutation[0] = 0
            for (bla in 1 until v) {
                currentPermutation[bla] = -1
            }
            for (bla in 0 until v) {
                visited[bla] = false
            }
            recIsomorphic(two, toVisit, visited, 0, currentPermutation, false)
        }
    }

    private fun getOutDegree(p: Int): Int {
        var total = 0
        for (y in 0 until v) total += adj[p][y]
        return total
    }
}