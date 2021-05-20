package com.handtruth.mc.collections

import com.handtruth.mc.internals.end
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

public open class CopyOnWriteMap<K, V>(initial: Map<K, V> = emptyMap()) : AbstractMutableMap<K, V>() {
    private val inner: AtomicRef<Map<K, V>> = atomic(initial)

    private inline var map: Map<K, V>
        get() = inner.value
        set(value) {
            inner.value = value
        }

    protected open fun copyMap(oldMap: Map<K, V>): MutableMap<K, V> {
        return oldMap.toMutableMap()
    }

    private inline fun <R> mutate(block: (MutableMap<K, V>) -> R): R {
        val newMap = copyMap(map)
        val result = block(newMap)
        map = newMap
        return result
    }

    override val size: Int get() = map.size

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = map.containsValue(value)

    override fun get(key: K): V? = map[key]

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = EntriesSet()
    override val keys: MutableSet<K> = KeysSet()
    override val values: MutableCollection<V> = ValuesCollection()

    override fun clear(): Unit = mutate { it.clear() }

    override fun put(key: K, value: V): V? = mutate { it.put(key, value) }

    override fun putAll(from: Map<out K, V>): Unit = mutate {
        it.putAll(from)
    }

    override fun remove(key: K): V? = mutate { it.remove(key) }

    private inner class EntriesSet : MutableSet<MutableMap.MutableEntry<K, V>> {
        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean = mutate {
            it.entries.add(element)
        }

        override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = mutate {
            it.entries.addAll(elements)
        }

        override fun clear() {
            mutate { it.entries.clear() }
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
            return EntriesIterator()
        }

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean = mutate {
            it.entries.remove(element)
        }

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = mutate {
            it.entries.removeAll(elements)
        }

        override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = mutate {
            it.entries.retainAll(elements)
        }

        override val size: Int get() = map.entries.size

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean =
            map.entries.contains(element)

        override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            map.entries.containsAll(elements)

        override fun isEmpty(): Boolean = map.entries.isEmpty()
    }

    private inner class Entry(actual: Map.Entry<K, V>) : MutableMap.MutableEntry<K, V> {
        override val key: K = actual.key
        override var value: V = actual.value
        override fun setValue(newValue: V): V {
            mutate { it.put(key, value) }
            val previous = value
            value = newValue
            return previous
        }
    }

    private inner class EntriesIterator : MutableIterator<MutableMap.MutableEntry<K, V>> {
        private val inner = map.entries.iterator()

        private var current: Map.Entry<K, V>? = null

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): MutableMap.MutableEntry<K, V> {
            val entry = inner.next()
            current = entry
            return Entry(entry)
        }

        override fun remove() {
            mutate { remove(current!!.key) }
        }
    }

    private inner class KeysSet : MutableSet<K> {
        override fun add(element: K): Boolean = mutate { it.keys.add(element) }

        override fun addAll(elements: Collection<K>): Boolean = mutate { it.keys.addAll(elements) }

        override fun clear() {
            mutate { it.keys.clear() }
        }

        override fun iterator(): MutableIterator<K> = KeysIterator()

        override fun remove(element: K): Boolean = mutate { it.keys.remove(element) }

        override fun removeAll(elements: Collection<K>): Boolean = mutate { it.keys.removeAll(elements) }

        override fun retainAll(elements: Collection<K>): Boolean = mutate { it.keys.retainAll(elements) }

        override val size: Int get() = map.keys.size

        override fun contains(element: K): Boolean = map.keys.contains(element)

        override fun containsAll(elements: Collection<K>): Boolean = map.keys.containsAll(elements)

        override fun isEmpty(): Boolean = map.keys.isEmpty()
    }

    private inner class KeysIterator : MutableIterator<K> {
        private val inner = map.keys.iterator()

        private var current: K = end()

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): K {
            val key = inner.next()
            current = key
            return key
        }

        override fun remove() {
            mutate { it.keys.remove(current) }
        }
    }

    private inner class ValuesCollection : MutableCollection<V> {
        override val size: Int get() = map.values.size

        override fun contains(element: V): Boolean = map.values.contains(element)

        override fun containsAll(elements: Collection<V>): Boolean = map.values.containsAll(elements)

        override fun isEmpty(): Boolean = map.values.isEmpty()

        override fun add(element: V): Boolean = mutate { it.values.add(element) }

        override fun addAll(elements: Collection<V>): Boolean = mutate { it.values.addAll(elements) }

        override fun clear() {
            mutate { it.values.clear() }
        }

        override fun iterator(): MutableIterator<V> = ValuesIterator()

        override fun remove(element: V): Boolean = mutate { it.values.remove(element) }

        override fun removeAll(elements: Collection<V>): Boolean = mutate { it.values.removeAll(elements) }

        override fun retainAll(elements: Collection<V>): Boolean = mutate { it.values.retainAll(elements) }
    }

    private inner class ValuesIterator : MutableIterator<V> {
        private val inner = map.values.iterator()

        private var current: V = end()

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): V {
            val value = inner.next()
            current = value
            return value
        }

        override fun remove() {
            mutate { it.values.remove(current) }
        }
    }
}
