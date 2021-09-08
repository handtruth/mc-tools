package com.handtruth.mc.graph

private fun <E> swap(list: MutableList<E>, i: Int, j: Int) {
    val a = list[i]
    val b = list[j]
    list[i] = b
    list[j] = a
}

private suspend fun <E> SequenceScope<List<E>>.heapPermutation(
    list: MutableList<E>,
    size: Int
) {
    if (size == 1) {
        yield(list)
    } else {
        for (i in 0 until size) {
            heapPermutation(list, size - 1)
            if (size and 1 == 1) {
                swap(list, 0, size - 1)
            } else {
                swap(list, i, size - 1)
            }
        }
    }
}

internal val <E> List<E>.permutations: Iterable<List<E>>
    get() = sequence {
        val size = size
        val list = this@permutations.toMutableList()
        heapPermutation(list, size)
    }.asIterable()

private fun <V, E> Graph<V, E>.groups(equation: Boolean) = if (equation) {
    vertices.groupBy { it.edges.size to it.value }
} else {
    vertices.groupBy { it.edges.size }
}

private suspend fun <E> SequenceScope<List<E>>.nextGroup(
    result: MutableList<E>,
    groups: List<List<E>>,
    depth: Int
) {
    if (depth == groups.size) {
        yield(result)
    } else {
        val group = groups[depth]
        group.permutations.forEach { perm ->
            result += perm
            nextGroup(result, groups, depth + 1)
            repeat(perm.size) {
                result.removeLast()
            }
        }
    }
}

internal val <E> List<List<E>>.groupPermutations: Iterable<List<E>>
    get() = sequence {
        val result = ArrayList<E>(sumOf { it.size })
        nextGroup(result, this@groupPermutations, 0)
    }.asIterable()

internal fun <V, E> findIsomorphism(
    a: Graph<V, E>,
    b: Graph<V, E>,
    equation: Boolean
): Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>? {
    if (a.vertices.size != b.vertices.size) {
        return null
    }
    val groupsA = a.groups(equation)
    val groupsB = b.groups(equation)
    if (groupsA.keys != groupsB.keys) {
        return null
    }
    val sortedA = ArrayList<List<Graph.Vertex<V, E>>>(groupsA.size)
    val sortedB = ArrayList<List<Graph.Vertex<V, E>>>(groupsB.size)
    for ((key, value) in groupsA.entries) {
        sortedA += value
        sortedB += groupsB[key]!!
    }
    sortedA.groupPermutations.forEach { permA ->
        sortedB.groupPermutations.forEach { permB ->
            val isomorphism = HashMap<Graph.Vertex<V, E>, Graph.Vertex<V, E>>()
            permA.zip(permB) { a, b -> isomorphism[a] = b }
            if (isIsomorphism(isomorphism, equation)) {
                return isomorphism
            }
        }
    }
    return null
}

internal fun <V, E> isIsomorphism(
    candidate: Map<Graph.Vertex<V, E>, Graph.Vertex<V, E>>,
    equation: Boolean
): Boolean {
    for ((key, value) in candidate) {
        if (key.edges.size != value.edges.size) {
            return false
        }
        if (equation && key.value != value.value) {
            return false
        }
        for (childEdge in key.edges) {
            val child = childEdge.target
            if (child === key) {
                continue
            }
            val vertex = candidate[key] ?: return false
            val found = (candidate[child] ?: return false).edges.any {
                it.source === vertex && (!equation || it.value == childEdge.value)
            }
            if (!found) {
                return false
            }
        }
    }
    return true
}
