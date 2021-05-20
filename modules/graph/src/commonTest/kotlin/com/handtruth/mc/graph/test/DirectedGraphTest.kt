package com.handtruth.mc.graph.test

import com.handtruth.mc.graph.*
import kotlin.test.*

class DirectedGraphTest {

    @Test
    fun graphTest() {
        val graph: MutableGraph<String, Unit> = DirectedGraph()
        assertTrue { graph.vertices.isEmpty() }
        assertEquals(0, graph.vertices.size)
        assertFalse { "A" in graph }
        graph += "A"
        assertTrue { "A" in graph }
        graph += "B"
        assertTrue { "B" in graph }
        assertEquals(0, graph.edges.size)
        assertTrue { graph.edges.isEmpty() }
        assertEquals(2, graph.vertices.size)
        assertFalse { graph.vertices.isEmpty() }
        graph["B", "C"] = Unit
        assertEquals(1, graph.edges.size)
        assertFalse { graph.edges.isEmpty() }
        assertFalse { "D" in graph }
        assertNotNull(graph["B", "C"])
        assertNotNull(graph.set("B", "C", Unit))
        assertNull(graph["C", "B"])
        val c = graph["C"]
        assertNotNull(c)
        val edge = graph.getEdge("B", "C")
        assertTrue { edge in c.edges }
        assertNull(graph["A", "D"])
        assertNull(graph["D", "A"])
        assertSame(c, graph.getOrSet("C"))
        assertTrue(graph.set("I", "J"))
        assertTrue(graph.set("C", "B"))
        assertFalse(graph.set("C", "B"))
        assertEquals(3, graph.edges.size)
        assertFalse { graph.edges.isEmpty() }
        assertTrue(graph.set("K", "J"))
        assertFalse(graph.set("K", "J"))
        assertEquals(4, graph.edges.size)
        assertNull(graph["H"])
        graph.addEdge("H", "M")
        val h = graph["H"]
        assertNotNull(h)
        val hm = graph.getEdge("H", "M")
        assertNotNull(hm)
        assertEquals(setOf(hm), h.edges.toSet())
        graph.addEdge("H", "L")
        val hl = graph.getEdge("H", "L")
        assertNotNull(hl)
        assertEquals(setOf(hm, hl), h.edges.toSet())
        graph.addEdge("N", "M")
        assertEquals(7, graph.edges.size)
        val m = graph["M"]
        assertNotNull(m)
        assertEquals(2, m.edges.size)
        val kj = graph.getEdge("K", "J")
        assertNotNull(kj)
        assertTrue { kj.exists }
        kj.target = m
        assertEquals(3, m.edges.size)
        assertTrue { m.exists }
        graph.vertices.remove(m)
        assertFalse { graph.vertexValues.remove(m.value) }
        assertFalse { m.exists }
        assertFalse { m in graph.vertices }
        assertFalse { kj.exists }
        assertFalse { kj in graph.edges }
        assertEquals(4, graph.edges.size)
        assertTrue { graph.vertexValues.remove("A") }
        assertEquals(4, graph.edges.size)
        assertNull(graph.remove("A", "B"))
        assertNull(graph.remove("B", "A"))
        assertTrue { graph.set("H", "B") }
        val hb = graph.getEdge("H", "B")
        assertNotNull(hb)
        assertFalse { hm.exists }
        assertEquals(setOf(hb, hl), h.edges.toSet())
        assertTrue { hl.exists }
        assertNotNull(graph.remove("H", "L"))
        assertFalse { hl.exists }
        assertTrue { hb.exists }
        assertNull(graph.remove("H", "L"))
        assertEquals(setOf(hb), h.edges.toSet())
        val o = graph.getOrSet("O")
        assertTrue { o.exists }
        assertTrue { graph.vertices.remove(o) }
        assertFalse { o.exists }
        assertFalse { graph.vertices.remove(o) }
        assertFalse { "O" in graph }
        assertFailsWith<IllegalStateException> {
            o.value = "0"
        }
        assertEquals(setOf("B", "C", "H", "I", "J", "K", "L", "N"), graph.vertices.map { it.value }.toSet())
        assertEquals(4, graph.edges.toList().size)
        val cb = graph.getEdge("C", "B")
        val bc = graph.getEdge("B", "C")
        assertNotNull(cb)
        assertNotNull(bc)
        assertEquals(setOf(cb, bc), c.edges.toSet())
        val b = graph["B"]
        assertNotNull(b)
        assertEquals(setOf(cb, bc, hb), b.edges.toSet())
        assertTrue { cb.exists }
        assertTrue { graph.edges.removeAll { it === cb } }
        assertFalse { cb.exists }
        assertTrue { bc.exists }
        assertTrue { graph.vertices.removeAll { it.value == "C" } }
        assertFalse { bc.exists }
        assertEquals(setOf(hb), b.edges.toSet())
        assertTrue { graph.set("N", "B") }
        val nb = graph.getEdge("N", "B")
        assertNotNull(nb)
        run {
            val other: MutableGraph<String, Unit> = DirectedGraph()
            other.set("N", "B")
            val onb = other.getEdge("N", "B")
            assertFalse { onb in graph.edges }
            assertTrue { nb in graph.edges }
            val on = other["N"]
            assertNotNull(on)
            assertFalse { on in graph.vertices }
            assertFailsWith<IllegalArgumentException> {
                b.connect(on, Unit)
            }
        }
        assertTrue { graph.edges.remove(nb) }
        assertFalse { nb.exists }
        assertFalse { graph.edges.remove(nb) }
        assertFalse { nb in graph.edges }
        assertEquals("Vertex(B)", b.toString())
        assertEquals("(N)-[kotlin.Unit]->(B)", nb.toString())
        val z = graph.getOrSet("Z")
        val n = graph["N"]
        assertNotNull(n)
        assertTrue { z.exists }
        n.value = "Z"
        z.remove()
        assertFalse { z.exists }
        assertSame(n, graph["Z"])
        assertNull(graph["N"])
        n.value = "N"
        assertSame(n, graph["N"])
        assertNull(graph["Z"])

        println(graph)

        assertSame(graph, n.graph)
        assertSame(graph, kj.graph)
        graph.edges.clear()
        assertEquals(emptySet(), graph.edges.toSet())
        graph.clear()
        assertEquals(emptySet(), graph.vertices.toSet())
        assertEquals(0, graph.vertices.size)
        assertEquals(0, graph.edges.size)

        assertFailsWith<UnsupportedOperationException> {
            graph.vertices.add(n)
        }
        assertFailsWith<UnsupportedOperationException> {
            graph.edges.add(kj)
        }
    }

    @Test
    fun multimapTest() {
        val map = Multimap<String, Int>()
        map.put("A", 1)
        map.put("M", 1)
        map.put("M", 4)
        assertEquals(3, map.valuesCount)
        val m = map.remove("M")
        assertEquals(1, map.valuesCount)
        map.put("A", m)
        assertEquals(3, map.valuesCount)
        assertEquals(3, map["A"].size)
        assertTrue { map.remove("A", 1) }
        assertEquals(2, map.valuesCount)
        assertEquals(2, map["A"].size)
        println(map.valuesIterator().asSequence().toList())
    }

    @Test
    fun verticesAndEdgesTest() {
        val graph: MutableGraph<String, Unit> = DirectedGraph()
        val a = graph.getOrSet("A")
        val b = graph.getOrSet("B")
        val c = graph.getOrSet("C")
        val d = graph.getOrSet("D")
        assertTrue { graph.vertices.remove(d) }
        assertTrue { graph.set("A", "B") }
        val ab = graph.getEdge("A", "B")
        assertNotNull(ab)
        assertFailsWith<IllegalArgumentException> {
            ab.source = d
        }
        assertTrue { c.edges.isEmpty() }
        ab.source = c
        assertFalse { c.edges.isEmpty() }
        assertEquals(emptySet(), a.edges.toSet())
        assertEquals(setOf(ab), b.edges.toSet())
        assertEquals(setOf(ab), c.edges.toSet())
        assertTrue { b.edges.remove(ab) }
        assertEquals(emptySet(), a.edges.toSet())
        assertEquals(emptySet(), b.edges.toSet())
        assertEquals(emptySet(), c.edges.toSet())
        assertFalse { ab.exists }
        assertFailsWith<IllegalStateException> {
            ab.source = b
        }
        assertFailsWith<IllegalStateException> {
            ab.target = c
        }

        graph.set("A", "B")
        graph.set("B", "C")
        graph.set("C", "A")

        val nab = graph.getEdge("A", "B")
        checkNotNull(nab)
        val bc = graph.getEdge("B", "C")
        checkNotNull(bc)
        val ca = graph.getEdge("C", "A")
        checkNotNull(ca)

        graph.set("B", "A")
        graph.set("C", "B")
        graph.set("A", "C")

        val ba = graph.getEdge("B", "A")
        checkNotNull(ba)
        val cb = graph.getEdge("C", "B")
        checkNotNull(cb)
        val ac = graph.getEdge("A", "C")
        checkNotNull(ac)

        assertTrue { nab.exists }
        assertTrue { b.edges.removeAll { it === nab } }
        assertFalse { nab.exists }
        assertEquals(setOf(ca, ac, ba), a.edges.toSet())
        assertEquals(setOf(bc, cb, ba), b.edges.toSet())
        assertEquals(setOf(bc, ca, cb, ac), c.edges.toSet())

        assertTrue { b.edges.removeAll { it === ba } }
        assertEquals(setOf(ca, ac), a.edges.toSet())
        assertEquals(setOf(bc, cb), b.edges.toSet())
        assertEquals(setOf(bc, ca, cb, ac), c.edges.toSet())

        assertFailsWith<UnsupportedOperationException> {
            a.edges.add(ab)
        }

        assertFalse { a.edges.remove(ab) }

        assertTrue { c.edges.containsAll(setOf(bc, ca, cb, ac)) }
        assertFalse { c.edges.containsAll(setOf(bc, ab, cb, ac)) }

        assertEquals(c.edges.size, graph.edges.size)

        c.edges.clear()

        assertEquals(0, graph.edges.size)
        assertEquals(0, c.edges.size)

        graph.clear()

        assertEquals("DirectedGraph()", graph.toString())
    }

    @Test
    fun newGraphTest() {
        val graph: MutableGraph<String, Int> = DirectedGraph()

        val w1 = graph.addVertex("W")
        val e1 = graph.addVertex("E")
        val l1 = graph.addVertex("L")
        val c1 = graph.addVertex("C")
        val o1 = graph.addVertex("O")
        val m1 = graph.addVertex("M")
        val e2 = graph.addVertex("E")

        val t1 = graph.addVertex("T")
        val o2 = graph.addVertex("O")

        val b1 = graph.addVertex("B")
        val l2 = graph.addVertex("L")
        val a1 = graph.addVertex("A")
        val c2 = graph.addVertex("C")
        val k1 = graph.addVertex("K")

        val s1 = graph.addVertex("S")
        val p1 = graph.addVertex("P")
        val a2 = graph.addVertex("A")
        val c3 = graph.addVertex("C")
        val e3 = graph.addVertex("E")

        w1.connect(e1, 1)
        e1.connect(l1, 1)
        l1.connect(c1, 1)
        c1.connect(o1, 1)
        o1.connect(m1, 1)
        m1.connect(e2, 1)

        t1.connect(o2, 2)

        b1.connect(l2, 3)
        l2.connect(a1, 3)
        a1.connect(c2, 3)
        c2.connect(k1, 3)

        s1.connect(p1, 4)
        p1.connect(a2, 4)
        a2.connect(c3, 4)
        c3.connect(e3, 4)

        assertEquals(19, graph.vertices.size)
        assertEquals(15, graph.edges.size)

        val letters = graph.vertexValues.toSet()
        val expectLetters = setOf(
            "W", "E", "L", "C", "O", "M", "T", "B", "A", "K", "S", "P"
        )
        assertEquals(expectLetters, letters)

        val wordsOrder = graph.edgeValues.toSet()
        assertEquals(setOf(1, 2, 3, 4), wordsOrder)

        fun lettersSet(word: Collection<Graph.Edge<String, Int>>): Set<Char> {
            return word.flatMap { listOf(it.source.value[0], it.target.value[0]) }.toSet()
        }

        val word1 = graph.getEdges(1)
        assertEquals("WELCOME".toSet(), lettersSet(word1))
        val word2 = graph.getEdges(2)
        assertEquals("TO".toSet(), lettersSet(word2))
        val word3 = graph.getEdges(3)
        assertEquals("BLACK".toSet(), lettersSet(word3))
        val word4 = graph.getEdges(4)
        assertEquals("SPACE".toSet(), lettersSet(word4))

        assertTrue { 1 in graph.edgeValues }
        assertTrue { 2 in graph.edgeValues }
        assertTrue { 3 in graph.edgeValues }
        assertTrue { 4 in graph.edgeValues }
        assertFalse { -1 in graph.edgeValues }
        assertFalse { -2 in graph.edgeValues }
        assertFalse { 0 in graph.edgeValues }
        assertFalse { 5 in graph.edgeValues }

        assertFalse { graph.edgeValues.remove(5) }
        assertTrue { graph.edgeValues.remove(3) }
        assertTrue { graph.getEdges(3).isEmpty() }

        assertEquals(19, graph.vertices.size)
        assertEquals(15 - 4, graph.edges.size)

        assertTrue { graph.vertexValues.add("H") }
        assertFalse { graph.vertexValues.add("H") }
        val h = graph["H"]
        assertNotNull(h)
        val i = graph.getOrSet("I")

        graph.addEdge("W", "H", 3)
        h.connect(i, 3)
        i.connect(t1, 3)
        t1.connect(e2, 3)

        val white = graph.getEdges(3)
        assertEquals("WHITE".toSet(), lettersSet(white))

        assertFalse { graph.vertexValues.remove("Z") }
        assertEquals(21, graph.vertices.size)
        assertEquals(15, graph.edges.size)

        println(graph)

        assertTrue { graph.vertexValues.removeAll { it == "W" } }
        assertFalse { graph.vertexValues.removeAll { it == "W" } }
        assertEquals(20, graph.vertices.size)
        assertEquals(13, graph.edges.size)

        assertFailsWith<UnsupportedOperationException> {
            graph.edgeValues.add(5)
        }
    }

    @Test
    fun topologicalSortTest() {
        val graph = DirectedGraph<Char, Unit>()
        graph.set('A', 'B')
        graph.set('A', 'C')
        graph.set('A', 'D')
        graph.set('A', 'E')
        graph.set('B', 'D')
        graph.set('C', 'D')
        graph.set('C', 'E')
        graph.set('D', 'E')

        val result = graph.topologicalSorted().map { it.value }.toCharArray().concatToString()

        assertTrue {
            result in listOf("ABCDE", "ACBDE")
        }

        graph.set('E', 'A')

        assertFailsWith<IllegalStateException> {
            graph.topologicalSorted()
        }
    }

    private fun createGraph(): MutableGraph<Unit, Unit> {
        val graph = MutableGraph<Unit, Unit>()
        val a = graph.addVertex()
        val b = graph.addVertex()
        val c = graph.addVertex()
        val d = graph.addVertex()
        val e = graph.addVertex()
        val f = graph.addVertex()
        val g = graph.addVertex()
        val h = graph.addVertex()
        a connect b
        b connect c
        b connect d
        c connect e
        d connect f
        d connect e
        e connect g
        f connect g
        g connect h
        return graph
    }

    @Test
    fun isomorphismTest() {
        val a = createGraph()
        val b = createGraph()
        assertTrue { a isomorphicTo b }
        val vertex = b.vertices.first()
        vertex connect vertex
        assertFalse { a isomorphicTo b }
    }

    private fun <T> cast(value: Any): T {
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    @Test
    fun equationTest1() {
        val a = createGraph()
        val b = createGraph()
        assertEquals(a, b)
        val vertex = a.vertices.last()
        vertex.value = cast(Any())
        assertNotEquals(a, b)
        assertTrue { a isomorphicTo b }
    }

    @Test
    fun equationTest2() {
        val a = createGraph()
        val b = createGraph()
        assertEquals(a, b)
        val edge = a.edges.last()
        edge.value = cast(Any())
        assertNotEquals(a, b)
        assertTrue { a isomorphicTo b }
    }
}
