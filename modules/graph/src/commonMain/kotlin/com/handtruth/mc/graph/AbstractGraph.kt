package com.handtruth.mc.graph

public abstract class AbstractGraph<V, E> : Graph<V, E> {
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
        append("Graph(")
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

    override fun hashCode(): Int {
        val vertexSum = vertices.sumBy { it.value.hashCode() }
        val edgeSum = edges.sumBy {
            it.value.hashCode() - (it.source.value.hashCode() xor it.target.value.hashCode())
        }
        return vertexSum xor edgeSum
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Graph<*, *>) {
            return false
        }
        return findIsomorphism(this, other, valueEquator, valueEquator) != null
    }
}
