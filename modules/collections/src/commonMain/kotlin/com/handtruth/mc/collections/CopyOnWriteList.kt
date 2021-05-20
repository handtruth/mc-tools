package com.handtruth.mc.collections

import com.handtruth.mc.internals.assert
import com.handtruth.mc.internals.end
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

public open class CopyOnWriteList<E>(initial: List<E> = emptyList()) : AbstractMutableList<E>() {
    private val inner: AtomicRef<List<E>> = atomic(initial)

    private inline var list: List<E>
        get() = inner.value
        set(value) {
            inner.value = value
        }

    protected open fun copyList(oldList: List<E>): MutableList<E> {
        return oldList.toMutableList()
    }

    private inline fun <R> mutate(block: (MutableList<E>) -> R): R {
        val newList = copyList(list)
        val result = block(newList)
        list = newList
        return result
    }

    override val size: Int get() = list.size

    override fun contains(element: E): Boolean = list.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = list.containsAll(elements)

    override fun get(index: Int): E = list.get(index)

    override fun indexOf(element: E): Int = list.indexOf(element)

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): MutableIterator<E> = listIterator()

    override fun lastIndexOf(element: E): Int = list.lastIndexOf(element)

    override fun add(element: E): Boolean = mutate { it.add(element) }

    override fun add(index: Int, element: E): Unit = mutate { it.add(index, element) }

    override fun addAll(index: Int, elements: Collection<E>): Boolean = mutate { it.addAll(index, elements) }

    override fun addAll(elements: Collection<E>): Boolean = mutate { it.addAll(elements) }

    override fun clear(): Unit = mutate { it.clear() }

    override fun listIterator(): MutableListIterator<E> = ElementsIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E> = ElementsIterator(index)

    override fun remove(element: E): Boolean = mutate { it.remove(element) }

    override fun removeAll(elements: Collection<E>): Boolean = mutate { it.removeAll(elements) }

    override fun removeAt(index: Int): E = mutate { it.removeAt(index) }

    override fun retainAll(elements: Collection<E>): Boolean = mutate { it.retainAll(elements) }

    override fun set(index: Int, element: E): E = mutate { it.set(index, element) }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        CopyOnWriteList(list.subList(fromIndex, toIndex))

    private inner class ElementsIterator(private var index: Int) : MutableListIterator<E> {
        private var iterator = list.listIterator(index)
        private var current: E = end()

        override fun hasPrevious(): Boolean = iterator.hasPrevious()

        override fun nextIndex(): Int = iterator.nextIndex()

        override fun previous(): E {
            current = iterator.previous()
            --index
            return current
        }

        override fun previousIndex(): Int = iterator.previousIndex()

        override fun add(element: E) {
            mutate { it.add(index, element) }
            iterator = list.listIterator(index)
            current = iterator.next()
            assert(current === element)
        }

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): E {
            current = iterator.next()
            ++index
            return current
        }

        override fun remove() {
            mutate { it.removeAt(index) }
            iterator = list.listIterator(--index)
            current = iterator.next()
        }

        override fun set(element: E) {
            mutate { it.set(index, element) }
            iterator = list.listIterator(index)
            current = iterator.next()
            assert(current === element)
        }
    }
}
