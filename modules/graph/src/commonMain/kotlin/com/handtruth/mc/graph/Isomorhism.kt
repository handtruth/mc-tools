package com.handtruth.mc.graph

private fun <V, E> Graph.Vertex<V, E>.getChildrenSize(): Int {
    return edges.count { it.source === this }
}

private fun <V, E> Graph.Vertex<V, E>.getParentsSize(): Int {
    return edges.count { it.target === this }
}

internal fun interface Equator<in T> {
    fun equals(a: T, b: T): Boolean
}

private object IgnoreEquator : Equator<Any?> {
    override fun equals(a: Any?, b: Any?) = true
}

internal object ValueEquator : Equator<Any?> {
    override fun equals(a: Any?, b: Any?): Boolean = a == b
}

internal fun <V, E> getIsomorphism(
    graph1: Graph<V, E>,
    graph2: Graph<V, E>,
    vertexEquator: Equator<V> = IgnoreEquator,
    edgeEquator: Equator<E> = IgnoreEquator
): Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>? {
    if (graph1.vertices.size != graph2.vertices.size) {
        return null
    }
    if (graph1.edges.size != graph2.edges.size) {
        return null
    }
    val nodeList1 = graphToList(graph1)
    val nodeList2 = graphToList(graph2)
    val comparator = Comparator<Graph.Vertex<V, E>> { a, b ->
        val outDegree1 = a.getChildrenSize()
        val outDegree2 = b.getChildrenSize()
        if (outDegree1 != outDegree2) {
            outDegree1.compareTo(outDegree2)
        } else {
            val inDegree1 = a.getParentsSize()
            val inDegree2 = b.getParentsSize()
            inDegree1.compareTo(inDegree2)
        }
    }
    nodeList1.sortWith(comparator)
    nodeList2.sortWith(comparator)
    for (i in nodeList1.indices) {
        if (nodeList1[i].getChildrenSize() != nodeList2[i].getChildrenSize()) {
            return null
        }
        if (nodeList1[i].getParentsSize() != nodeList2[i].getParentsSize()) {
            return null
        }
    }
    return bruteForceIsomorphism(nodeList1, nodeList2, vertexEquator, edgeEquator)
}

private class PermutationEnumerator(length: Int) {
    val indices: IntArray = IntArray(length)
    private var initial = false
    fun reset() {
        initial = true
        for (i in indices.indices) {
            indices[i] = i
        }
    }

    operator fun next(): IntArray? {
        if (initial) {
            initial = false
            return indices
        }
        var i = indices.size - 2
        while (i >= 0 && indices[i] > indices[i + 1]) {
            --i
        }
        if (i == -1) {
            return null
        }
        var j = i + 1
        var minValue = indices[j]
        var minIndex = j
        while (j < indices.size) {
            if (indices[i] < indices[j] && indices[j] < minValue) {
                minValue = indices[j]
                minIndex = j
            }
            ++j
        }
        var tmp = indices[i]
        indices[i] = indices[minIndex]
        indices[minIndex] = tmp
        ++i
        j = indices.size - 1
        while (i < j) {
            tmp = indices[i]
            indices[i] = indices[j]
            indices[j] = tmp
            ++i
            --j
        }
        return indices
    }

    init {
        reset()
    }
}

private fun <V, E> graphToList(graph: Graph<V, E>): MutableList<Graph.Vertex<V, E>> {
    return graph.vertices.toMutableList()
}

private fun <V, E> bruteForceIsomorphism(
    nodeList1: List<Graph.Vertex<V, E>>,
    nodeList2: List<Graph.Vertex<V, E>>,
    vertexEquator: Equator<V>,
    edgeEquator: Equator<E>
): Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>? {
    val list1 = ArrayList<MutableList<Graph.Vertex<V, E>>>()
    val list2 = ArrayList<MutableList<Graph.Vertex<V, E>>>()
    list1.add(ArrayList())
    list1[0].add(nodeList1[0])
    list2.add(ArrayList())
    list2[0].add(nodeList2[0])
    var previousInDegree = nodeList1[0].getParentsSize()
    var previousOutDegree = nodeList2[0].getChildrenSize()
    for (i in 1 until nodeList1.size) {
        val currentNode = nodeList1[i]
        val currentInDegree = currentNode.getParentsSize()
        val currentOutDegree = currentNode.getChildrenSize()
        if (previousInDegree != currentInDegree || previousOutDegree != currentOutDegree) {
            val newSubList1 = ArrayList<Graph.Vertex<V, E>>()
            val newSubList2 = ArrayList<Graph.Vertex<V, E>>()
            newSubList1.add(currentNode)
            newSubList2.add(nodeList2[i])
            list1.add(newSubList1)
            list2.add(newSubList2)
            previousInDegree = currentInDegree
            previousOutDegree = currentOutDegree
        } else {
            list1[list1.lastIndex].add(currentNode)
            list2[list2.lastIndex].add(nodeList2[i])
        }
    }
    val certainMap = HashMap<Graph.Vertex<V, E>, Graph.Vertex<V, E>>()
    for (i in list1.indices) {
        val currentSubList = list1[i]
        if (currentSubList.size == 1) {
            certainMap[currentSubList[0]] = list2[i][0]
        }
    }
    val groupList1 = ArrayList<List<Graph.Vertex<V, E>>>()
    val groupList2 = ArrayList<MutableList<Graph.Vertex<V, E>>>()
    for (i in list1.indices) {
        if (list1[i].size > 1) {
            groupList1.add(ArrayList(list1[i]))
            groupList2.add(ArrayList(list2[i]))
        }
    }
    return if (groupList1.isEmpty()) {
        if (isIsomorphism(certainMap, vertexEquator, edgeEquator)) certainMap else null
    } else {
        findIsomorphismPermutation(groupList1, groupList2, HashMap(certainMap), vertexEquator, edgeEquator)
    }
}

private fun <V, E> findIsomorphismPermutation(
    groupList1: List<List<Graph.Vertex<V, E>>>,
    groupList2: List<MutableList<Graph.Vertex<V, E>>>,
    certainMap: Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>,
    vertexEquator: Equator<V>,
    edgeEquator: Equator<E>
): Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>? {
    val permutationEnumeratorList = ArrayList<PermutationEnumerator>(groupList1.size)
    for (group in groupList1) {
        permutationEnumeratorList.add(PermutationEnumerator(group.size))
    }
    do {
        val candidate = generateIsomorphismCandidate(groupList1, groupList2, permutationEnumeratorList)
        candidate.putAll(certainMap)
        if (isIsomorphism(candidate, vertexEquator, edgeEquator)) {
            return candidate
        }
    } while (incrementPermutationEnumeratorList(permutationEnumeratorList))
    return null
}

private fun <V, E> generateIsomorphismCandidate(
    groupList1: List<List<Graph.Vertex<V, E>>>,
    groupList2: List<MutableList<Graph.Vertex<V, E>>>,
    permutationEnumeratorList: List<PermutationEnumerator>
): MutableMap<Graph.Vertex<V, E>, Graph.Vertex<V, E>> {
    for (groupIndex in groupList2.indices) {
        permute(groupList2[groupIndex], permutationEnumeratorList[groupIndex])
    }
    val isomorphismCandidate = HashMap<Graph.Vertex<V, E>, Graph.Vertex<V, E>>()
    for (groupIndex in groupList1.indices) {
        for (nodeIndex in groupList1[groupIndex].indices) {
            isomorphismCandidate[groupList1[groupIndex][nodeIndex]] = groupList2[groupIndex][nodeIndex]
        }
    }
    return isomorphismCandidate
}

private fun <V, E> permute(
    groupList: MutableList<Graph.Vertex<V, E>>,
    permutationEnumeratorList: PermutationEnumerator
) {
    val indices = permutationEnumeratorList.indices
    val tmp = ArrayList(groupList)
    for (i in groupList.indices) {
        groupList[indices[i]] = tmp[i]
    }
}

private fun incrementPermutationEnumeratorList(list: List<PermutationEnumerator>): Boolean {
    for (i in list.indices) {
        if (list[i].next() == null) {
            list[i].reset()
        } else {
            return true
        }
    }
    return false
}

internal fun <V, E> isIsomorphism(
    candidate: Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>,
    vertexEquator: Equator<V>,
    edgeEquator: Equator<E>
): Boolean {
    for ((key, value) in candidate) {
        if (key.getChildrenSize() != value.getChildrenSize()) {
            return false
        }
        if (key.getParentsSize() != value.getParentsSize()) {
            return false
        }
        if (!vertexEquator.equals(key.value, value.value)) {
            return false
        }
        for (childEdge in key.edges) {
            val child = childEdge.target
            if (child === key) {
                continue
            }
            val vertex = candidate[key] ?: return false
            val found = (candidate[child] ?: return false).edges.any {
                it.source === vertex && edgeEquator.equals(it.value, childEdge.value)
            }
            if (!found) {
                return false
            }
        }
    }
    return true
}
