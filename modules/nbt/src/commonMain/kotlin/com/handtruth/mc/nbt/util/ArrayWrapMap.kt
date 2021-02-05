package com.handtruth.mc.nbt.util

internal class ArrayWrapMap<V>(private val array: Array<V>) : Map<Int, V> {
    override val entries: Set<Map.Entry<Int, V>> get() = EntriesSet(array)
    override val size get() = array.size
    override val keys: Set<Int> get() = IndexSet(size)
    override val values: Collection<V> get() = array.asList()

    override fun containsKey(key: Int) = key in array.indices

    override fun containsValue(value: V) = value in array

    override fun get(key: Int): V? = if (key in array.indices) array[key] else null

    override fun isEmpty() = array.isEmpty()

    private class IndexSet(override val size: Int) : Set<Int> {
        override fun contains(element: Int) = element in 0 until size
        override fun containsAll(elements: Collection<Int>) = elements.all { it in this }
        override fun isEmpty() = size == 0
        override fun iterator() = (0 until size).iterator()
    }

    private class EntriesSet<V>(val array: Array<V>) : Set<Map.Entry<Int, V>> {
        override val size get() = array.size

        override fun contains(element: Map.Entry<Int, V>) =
            element.key in array.indices && array[element.key] == element.value

        override fun containsAll(elements: Collection<Map.Entry<Int, V>>) = elements.all { it in this }

        override fun isEmpty() = array.isEmpty()

        override fun iterator(): Iterator<Map.Entry<Int, V>> = EntryIterator(array)

        private data class Entry<out V>(override val key: Int, override val value: V) : Map.Entry<Int, V>

        private class EntryIterator<V>(val array: Array<V>) : Iterator<Map.Entry<Int, V>> {
            val iterator = array.indices.iterator()

            override fun hasNext() = iterator.hasNext()

            override fun next(): Map.Entry<Int, V> {
                val index = iterator.next()
                val value = array[index]
                return Entry(index, value)
            }
        }
    }
}

internal fun <V> Array<out V>.asMap(): Map<Int, V> = ArrayWrapMap(this)
