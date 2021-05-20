package com.handtruth.mc.graph

import com.handtruth.mc.internals.assert
import com.handtruth.mc.internals.end

internal class Multimap<K, V> {
    private val container = hashMapOf<K, MutableList<V>>()

    var valuesCount: Int = 0
        private set

    val keys: Set<K> get() = container.keys

    fun put(key: K, value: V) {
        val list = container.getOrPut(key) { arrayListOf() }
        list += value
        ++valuesCount
    }

    fun put(key: K, values: List<V>) {
        val list = container.getOrPut(key) { arrayListOf() }
        list += values
        valuesCount += values.size
    }

    operator fun get(key: K): List<V> = container[key] ?: emptyList()

    operator fun contains(key: K) = key in container

    fun remove(key: K, value: V): Boolean {
        val list = container[key] ?: return false
        list.remove(value) || return false
        if (list.isEmpty()) {
            container.remove(key)
        }
        --valuesCount
        return true
    }

    fun remove(key: K): List<V> {
        val list = container.remove(key)
        return if (list != null) {
            valuesCount -= list.size
            list
        } else {
            emptyList()
        }
    }

    fun valuesIterator(): MutableIterator<V> = ValuesIterator()

    private inner class ValuesIterator : MutableIterator<V> {
        var mainIterator = container.entries.toList().iterator()
        var currentEntry: MutableMap.MutableEntry<K, MutableList<V>>? = null
        var index = 0
        var nextEntry: MutableMap.MutableEntry<K, MutableList<V>>? = null
        var currentItem: V = end()
        var nextItem = tryGetNext()

        private fun tryGetNext(): V {
            while (true) {
                val nullableNextEntry = nextEntry
                val nextEntry: MutableMap.MutableEntry<K, MutableList<V>>
                if (nullableNextEntry == null) {
                    if (mainIterator.hasNext()) {
                        nextEntry = mainIterator.next()
                        this.nextEntry = nextEntry
                        index = 0
                    } else {
                        return end()
                    }
                } else {
                    nextEntry = nullableNextEntry
                }
                if (index < nextEntry.value.size) {
                    return nextEntry.value[index++]
                } else {
                    this.nextEntry = null
                }
            }
        }

        override fun hasNext() = nextItem !== end<V>()

        override fun next(): V {
            check(hasNext()) { "no more values" }
            currentEntry = nextEntry
            currentItem = nextItem
            nextItem = tryGetNext()
            return currentItem
        }

        override fun remove() {
            check(currentItem !== end<V>()) { "not called next() yet" }
            val currentEntry = currentEntry!!
            if (index == 1) {
                val removed = remove(currentEntry.key, currentItem)
                assert(removed)
            } else {
                --index
                val removed = currentEntry.value.removeAt(index - 1)
                assert(removed === currentItem)
                assert(currentEntry.value.isNotEmpty())
                --valuesCount
            }
        }
    }
}
