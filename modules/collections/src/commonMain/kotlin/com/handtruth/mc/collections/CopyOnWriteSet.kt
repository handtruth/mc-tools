package com.handtruth.mc.collections

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

public open class CopyOnWriteSet<E>(initial: Set<E> = emptySet()) : AbstractMutableSet<E>() {
    private val inner: AtomicRef<Set<E>> = atomic(initial)

    private inline var set: Set<E>
        get() = inner.value
        set(value) {
            inner.value = value
        }

    protected open fun copySet(oldSet: Set<E>): MutableSet<E> {
        return oldSet.toMutableSet()
    }

    private inline fun <R> mutate(block: (MutableSet<E>) -> R): R {
        val newSet = copySet(set)
        val result = block(newSet)
        set = newSet
        return result
    }

    override fun add(element: E): Boolean = mutate { it.add(element) }

    override fun addAll(elements: Collection<E>): Boolean = mutate { it.addAll(elements) }

    override fun clear(): Unit = mutate { it.clear() }

    override fun iterator(): MutableIterator<E> = ElementsIterator()

    override fun remove(element: E): Boolean = mutate { it.remove(element) }

    override fun removeAll(elements: Collection<E>): Boolean = mutate { it.removeAll(elements) }

    override fun retainAll(elements: Collection<E>): Boolean = mutate { it.retainAll(elements) }

    override val size: Int get() = set.size

    override fun contains(element: E): Boolean = set.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = set.containsAll(elements)

    override fun isEmpty(): Boolean = set.isEmpty()

    private inner class ElementsIterator : MutableIterator<E> {
        private val iterator = set.iterator()
        private var current: E = end()

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): E {
            current = iterator.next()
            return current
        }

        override fun remove() {
            mutate { it.remove(current) }
        }
    }
}
