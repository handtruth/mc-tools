package com.handtruth.mc.collections

public interface Graph<out V, out E> {
    public val vertices: Set<Vertex<V, E>>
    public val edges: Set<Edge<V, E>>

    public val vertexValues: Set<V>
    public val edgeValues: Set<E>

    public fun getVertices(value: @UnsafeVariance V): Collection<Vertex<V, E>>
    public fun getEdges(value: @UnsafeVariance E): Collection<Edge<V, E>>

    public interface Vertex<out V, out E> {
        public val graph: Graph<V, E>
        public val value: V
        public val edges: Set<Edge<V, E>>
        public val exists: Boolean
    }

    public interface Edge<out V, out E> {
        public val graph: Graph<V, E>
        public val value: E
        public val source: Vertex<V, E>
        public val target: Vertex<V, E>
        public val exists: Boolean
    }
}

public interface MutableGraph<V, E> : Graph<V, E> {
    public override val vertices: MutableSet<MutableVertex<V, E>>
    public override val edges: MutableSet<MutableEdge<V, E>>

    public override val vertexValues: MutableSet<V>
    public override val edgeValues: MutableSet<E>

    public override fun getVertices(value: V): Collection<MutableVertex<V, E>>
    public override fun getEdges(value: E): Collection<MutableEdge<V, E>>

    public fun addVertex(value: V): MutableVertex<V, E>

    public fun clear()

    public interface MutableVertex<V, E> : Graph.Vertex<V, E> {
        override val graph: MutableGraph<V, E>
        public override var value: V
        public override val edges: MutableSet<MutableEdge<V, E>>

        public fun connect(other: MutableVertex<V, E>, value: E): MutableEdge<V, E>
    }

    public interface MutableEdge<V, E> : Graph.Edge<V, E> {
        override val graph: MutableGraph<V, E>
        public override var value: E
        public override var source: MutableVertex<V, E>
        public override var target: MutableVertex<V, E>
    }
}

public operator fun <V, E> Graph<V, E>.get(vertex: @UnsafeVariance V): Graph.Vertex<V, E>? =
    getVertices(vertex).firstOrNull()

public operator fun <V, E> MutableGraph<V, E>.get(vertex: V): MutableGraph.MutableVertex<V, E>? =
    getVertices(vertex).firstOrNull()

public fun <V, E> MutableGraph<V, E>.getOrSet(vertex: V): MutableGraph.MutableVertex<V, E> {
    val vertices = getVertices(vertex)
    return if (vertices.isEmpty()) {
        addVertex(vertex)
    } else {
        vertices.first()
    }
}

public fun <V, E> Graph<V, E>.getEdge(source: @UnsafeVariance V, target: @UnsafeVariance V): Graph.Edge<V, E>? {
    val fromVertex = this[source] ?: return null
    val toVertex = this[target] ?: return null
    return fromVertex.edges.find { it.source === fromVertex && it.target === toVertex }
}

public fun <V, E> MutableGraph<V, E>.getEdge(
    source: V,
    target: V
): MutableGraph.MutableEdge<V, E>? {
    val fromVertex = this[source] ?: return null
    val toVertex = this[target] ?: return null
    return fromVertex.edges.find { it.source === fromVertex && it.target === toVertex }
}

public operator fun <V, E> Graph<V, E>.get(source: @UnsafeVariance V, target: @UnsafeVariance V): E? {
    return getEdge(source, target)?.value
}

public operator fun <V, E> MutableGraph<V, E>.set(source: V, target: V, value: E): E? {
    val fromVertex = getOrSet(source)
    val toVertex = getOrSet(target)
    val edge = fromVertex.edges.find { it.source === fromVertex && it.target === toVertex }
    return if (edge === null) {
        fromVertex.connect(toVertex, value)
        null
    } else {
        val result = edge.value
        edge.value = value
        result
    }
}

public operator fun <V> Graph<V, *>.contains(vertex: @UnsafeVariance V): Boolean = vertexValues.contains(vertex)

public fun MutableGraph.MutableVertex<*, *>.remove() {
    graph.vertices.remove(this)
}

public fun MutableGraph.MutableEdge<*, *>.remove() {
    graph.edges.remove(this)
}

public operator fun <V> MutableGraph<V, *>.plusAssign(vertex: V) {
    addVertex(vertex)
}

public fun <V> MutableGraph<V, Unit>.set(source: V, target: V): Boolean {
    return set(source, target, Unit) == null
}

public fun <V, E> MutableGraph<V, E>.addEdge(source: V, target: V, value: E): MutableGraph.MutableEdge<V, E> {
    val sourceVertex = getOrSet(source)
    val targetVertex = getOrSet(target)
    return sourceVertex.connect(targetVertex, value)
}

public fun <V> MutableGraph<V, Unit>.addEdge(source: V, target: V): MutableGraph.MutableEdge<V, Unit> {
    val sourceVertex = getOrSet(source)
    val targetVertex = getOrSet(target)
    return sourceVertex.connect(targetVertex, Unit)
}

public fun <V, E> MutableGraph<V, E>.remove(source: V, target: V): E? {
    val edge = getEdge(source, target) ?: return null
    edge.remove()
    return edge.value
}

private fun <V, E> topologicalSorting(
    vertex: Graph.Vertex<V, E>,
    result: MutableList<Graph.Vertex<V, E>>,
    stack: MutableSet<Graph.Vertex<V, E>>
) {
    if (vertex in result) {
        return
    }
    check(vertex !in stack) { "cycle in graph" }
    stack += vertex
    for (edge in vertex.edges) {
        if (edge.target !== vertex) {
            topologicalSorting(edge.target, result, stack)
        }
    }
    stack -= vertex
    result += vertex
}

public fun <V, E> Graph<V, E>.topologicalSorted(): List<Graph.Vertex<V, E>> {
    val result = ArrayList<Graph.Vertex<V, E>>(vertices.size)
    val stack = hashSetOf<Graph.Vertex<V, E>>()
    for (vertex in vertices) {
        topologicalSorting(vertex, result, stack)
    }
    return result.asReversed()
}

public fun <V, E> Graph<V, E>.copy(reverse: Boolean = false): MutableGraph<V, E> {
    val associateVertices = hashMapOf<Graph.Vertex<V, E>, MutableGraph.MutableVertex<V, E>>()
    val newGraph = DirectedGraph<V, E>()
    for (vertex in vertices) {
        val newVertex = newGraph.addVertex(vertex.value)
        associateVertices[vertex] = newVertex
    }
    if (reverse) {
        for (edge in edges) {
            val target = associateVertices[edge.source]!!
            val source = associateVertices[edge.target]!!
            source.connect(target, edge.value)
        }
    } else {
        for (edge in edges) {
            val source = associateVertices[edge.source]!!
            val target = associateVertices[edge.target]!!
            source.connect(target, edge.value)
        }
    }
    return newGraph
}

public fun <V, E> Graph<V, E>.toMutableGraph(): Graph<V, E> = copy()

public fun <V, E> MutableGraph(): MutableGraph<V, E> = DirectedGraph()
