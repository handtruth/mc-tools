package com.handtruth.mc.collections

import kotlin.contracts.contract
import kotlin.jvm.JvmField

public class DirectedGraph<V, E> : MutableGraph<V, E> {

    private val vertexMap = Multimap<V, DirectVertex>()
    private val edgeMap = Multimap<E, DirectEdge>()

    override val vertices: MutableSet<MutableGraph.MutableVertex<V, E>> = AllVertices()
    override val edges: MutableSet<MutableGraph.MutableEdge<V, E>> = AllEdges()

    override val vertexValues: MutableSet<V> = VertexValues()

    override val edgeValues: MutableSet<E> = EdgesValues()

    override fun getVertices(value: V): Collection<MutableGraph.MutableVertex<V, E>> = vertexMap[value]

    override fun getEdges(value: E): Collection<MutableGraph.MutableEdge<V, E>> = edgeMap[value]

    private fun createVertex(value: V): DirectVertex {
        val vertex = DirectVertex(value)
        vertexMap.put(value, vertex)
        return vertex
    }

    private fun createEdge(source: DirectVertex, target: DirectVertex, value: E): DirectEdge {
        val edge = DirectEdge(source, target, value)
        edgeMap.put(value, edge)
        source.connected += edge
        target.connected += edge
        return edge
    }

    private fun deleteEdge(edge: DirectEdge): Boolean {
        val a = edge.directSource.connected.remove(edge)
        val b = edge.directTarget.connected.remove(edge)
        assert(a && b)
        val result = edgeMap.remove(edge.value, edge)
        edge.exists = false
        return result
    }

    private fun deleteVertex(vertex: DirectVertex): Boolean {
        for (edge in vertex.connected.toTypedArray()) {
            val removed = deleteEdge(edge)
            assert(removed)
        }
        val result = vertexMap.remove(vertex.value, vertex)
        vertex.exists = false
        return result
    }

    override fun addVertex(value: V): MutableGraph.MutableVertex<V, E> {
        return createVertex(value)
    }

    private fun removeVertices(value: V): Boolean {
        val list = vertexMap.remove(value)
        for (vertex in list) {
            val removed = deleteVertex(vertex)
            assert(!removed)
        }
        return list.isNotEmpty()
    }

    private fun removeEdges(value: E): Boolean {
        val list = edgeMap.remove(value)
        for (vertex in list) {
            val removed = deleteEdge(vertex)
            assert(!removed)
        }
        return list.isNotEmpty()
    }

    override fun clear() {
        val iterator = vertexMap.valuesIterator()
        while (iterator.hasNext()) {
            val vertex = iterator.next()
            iterator.remove()
            val removed = deleteVertex(vertex)
            assert(!removed)
        }
    }

    private fun has(vertex: MutableGraph.MutableVertex<V, E>): Boolean {
        contract {
            returns(true) implies (vertex is DirectVertex)
        }
        return vertex.exists && vertex.graph === this
    }

    private fun has(edge: MutableGraph.MutableEdge<V, E>): Boolean {
        contract {
            returns(true) implies (edge is DirectEdge)
        }
        return edge.exists && edge.graph === this
    }

    private inner class DirectVertex(value: V) : MutableGraph.MutableVertex<V, E> {
        val connected = arrayListOf<DirectEdge>()

        private fun checkExists() = check(exists) { "this vertex was removed" }

        override val graph get() = this@DirectedGraph
        override var value: V = value
            set(value) {
                checkExists()
                val list = vertexMap.remove(field)
                vertexMap.put(value, list)
                field = value
            }

        override val edges = VertexEdges()

        override var exists = true

        override fun connect(other: MutableGraph.MutableVertex<V, E>, value: E): DirectEdge {
            require(has(other)) { "vertex $value is not contained in the graph" }
            return createEdge(this, other, value)
        }

        private fun hasEdge(edge: MutableGraph.MutableEdge<V, E>): Boolean {
            contract {
                returns(true) implies (edge is DirectEdge)
            }
            return edge in connected
        }

        private inner class VertexEdges : AbstractMutableSet<MutableGraph.MutableEdge<V, E>>() {

            override fun add(element: MutableGraph.MutableEdge<V, E>) = throw UnsupportedOperationException()

            override fun clear() {
                for (edge in connected.toTypedArray()) {
                    deleteEdge(edge)
                }
            }

            override fun iterator() = VertexEdgesIterator()

            override fun remove(element: MutableGraph.MutableEdge<V, E>): Boolean {
                return if (hasEdge(element)) {
                    val removed = deleteEdge(element)
                    assert(removed)
                    true
                } else {
                    false
                }
            }

            override val size get() = connected.size

            override fun contains(element: MutableGraph.MutableEdge<V, E>) = hasEdge(element)

            override fun isEmpty() = connected.isEmpty()
        }

        private inner class VertexEdgesIterator : MutableIterator<DirectEdge> {
            private var index = 0

            override fun hasNext() = index < connected.size

            override fun next() = connected[index++]

            override fun remove() {
                val edge = connected[--index]
                deleteEdge(edge)
            }
        }

        override fun toString() = "Vertex($value)"
    }

    private inner class DirectEdge(
        @JvmField
        var directSource: DirectVertex,
        @JvmField
        var directTarget: DirectVertex,
        value: E
    ) : MutableGraph.MutableEdge<V, E> {
        override val graph get() = this@DirectedGraph

        private fun checkExists() = check(exists) { "this edge was removed" }

        override var value: E = value
            set(value) {
                checkExists()
                val list = edgeMap.remove(field)
                edgeMap.put(value, list)
                field = value
            }

        private fun replace(from: DirectVertex, to: MutableGraph.MutableVertex<V, E>): DirectVertex {
            checkExists()
            require(has(to)) { "vertex $value is not contained in the graph" }
            from.connected.remove(this)
            to.connected.add(this)
            return to
        }

        override var source: MutableGraph.MutableVertex<V, E>
            get() = directSource
            set(value) {
                directSource = replace(directSource, value)
            }

        override var target: MutableGraph.MutableVertex<V, E>
            get() = directTarget
            set(value) {
                directTarget = replace(directTarget, value)
            }

        override var exists = true

        override fun toString() = "(${source.value})-[$value]->(${target.value})"
    }

    private inner class AllVertices : AbstractMutableSet<MutableGraph.MutableVertex<V, E>>() {
        override fun add(element: MutableGraph.MutableVertex<V, E>) = throw UnsupportedOperationException()

        override fun clear() {
            this@DirectedGraph.clear()
        }

        override fun iterator() = AllVerticesIterator()

        override fun remove(element: MutableGraph.MutableVertex<V, E>): Boolean {
            return if (has(element)) {
                val removed = deleteVertex(element)
                assert(removed)
                true
            } else {
                false
            }
        }

        override val size get() = vertexMap.valuesCount

        override fun contains(element: MutableGraph.MutableVertex<V, E>) = has(element)
    }

    private inner class AllVerticesIterator : MutableIterator<DirectVertex> {
        private val iterator = vertexMap.valuesIterator()
        private var current: DirectVertex? = null

        override fun hasNext() = iterator.hasNext()

        override fun next(): DirectVertex {
            val current = iterator.next()
            this.current = current
            return current
        }

        override fun remove() {
            iterator.remove()
            val removed = deleteVertex(current!!)
            assert(!removed)
        }
    }

    private inner class AllEdges : AbstractMutableSet<MutableGraph.MutableEdge<V, E>>() {
        override fun add(element: MutableGraph.MutableEdge<V, E>) = throw UnsupportedOperationException()

        override fun clear() {
            val iterator = edgeMap.valuesIterator()
            while (iterator.hasNext()) {
                val edge = iterator.next()
                iterator.remove()
                val removed = deleteEdge(edge)
                assert(!removed)
            }
        }

        override fun iterator() = AllEdgesIterator()

        override fun remove(element: MutableGraph.MutableEdge<V, E>): Boolean {
            return if (has(element)) {
                val removed = deleteEdge(element)
                assert(removed)
                true
            } else {
                false
            }
        }

        override val size get() = edgeMap.valuesCount

        override fun contains(element: MutableGraph.MutableEdge<V, E>) = has(element)
    }

    private inner class AllEdgesIterator : MutableIterator<DirectEdge> {
        private val iterator = edgeMap.valuesIterator()

        private var current: DirectEdge? = null

        override fun hasNext() = iterator.hasNext()

        override fun next(): DirectEdge {
            val current = iterator.next()
            this.current = current
            return current
        }

        override fun remove() {
            iterator.remove()
            val removed = deleteEdge(current!!)
            assert(!removed)
        }
    }

    private inner class VertexValues : AbstractMutableSet<V>() {
        override fun add(element: V): Boolean {
            return if (element in vertexMap) {
                false
            } else {
                addVertex(element)
                true
            }
        }

        override fun clear() {
            this@DirectedGraph.clear()
        }

        override fun iterator() = VertexValuesIterator()

        override fun remove(element: V): Boolean {
            return removeVertices(element)
        }

        override val size get() = vertexMap.keys.size

        override fun contains(element: V) = element in vertexMap
    }

    private inner class VertexValuesIterator : MutableIterator<V> {
        private val iterator = vertexMap.keys.toList().iterator()

        private var current: V = end()

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): V {
            current = iterator.next()
            return current
        }

        override fun remove() {
            removeVertices(current)
        }
    }

    private inner class EdgesValues : AbstractMutableSet<E>() {
        override fun add(element: E) = throw UnsupportedOperationException()

        override fun clear() {
            edges.clear()
        }

        override fun iterator() = EdgesValuesIterator()

        override fun remove(element: E) = removeEdges(element)

        override val size get() = edgeMap.keys.size

        override fun contains(element: E) = element in edgeMap
    }

    private inner class EdgesValuesIterator : MutableIterator<E> {
        private val iterator = edgeMap.keys.toList().iterator()

        private var current: E = end()

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): E {
            current = iterator.next()
            return current
        }

        override fun remove() {
            removeEdges(current)
        }
    }

    private fun Appendable.appendEdge(cache: Map<Graph.Vertex<*, *>, String>, edge: Graph.Edge<*, *>) {
        append(cache[edge.source])
        append("-[")
        append(edge.value.toString())
        append("]->")
        append(cache[edge.target])
    }

    private fun Appendable.appendVertex(cache: Map<Graph.Vertex<*, *>, String>, vertex: Graph.Vertex<*, *>) {
        val iterator = vertex.edges.iterator()
        val first: Graph.Edge<*, *>
        while (true) {
            if (iterator.hasNext()) {
                val edge = iterator.next()
                if (edge.source === vertex) {
                    first = edge
                    break
                }
            } else {
                append(cache[vertex])
                return
            }
        }
        appendEdge(cache, first)
        for (edge in iterator) {
            append(", ")
            appendEdge(cache, edge)
        }
    }

    override fun toString(): String = buildString {
        append("DirectedGraph(")
        val cache = buildMap<Graph.Vertex<*, *>, String> {
            for (vertex in vertices) {
                put(vertex, vertex.value.toString())
            }
        }
        val iterator = vertices.iterator()
        if (iterator.hasNext()) {
            val first = iterator.next()
            appendVertex(cache, first)
            for (vertex in iterator) {
                append("; ")
                appendVertex(cache, vertex)
            }
        }
        append(")")
    }

    /*
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Graph<*, *>) {
            return false
        }
        if (vertices.size != other.vertices.size || edges.size != other.edges.size) {
            return false
        }
        val bV = other.vertices.toList()
        main@ for (a in vertices) {
            for (b in bV) {
                if (a.value == b.value && a.edges.size == b.edges.size) {
                    for (ea in a.edges) {

                    }
                }
            }
            return false
        }
    }
    */
}
