package com.handtruth.mc.util

internal class ReferenceSet<T> : AbstractMutableSet<T>() {
    private val innerSet = mutableListOf<T>()

    override fun add(element: T): Boolean {
        if (element in this) {
            return false
        }
        innerSet.add(element)
        return true
    }

    override fun clear() = innerSet.clear()

    override fun iterator(): MutableIterator<T> = innerSet.iterator()

    override fun remove(element: T): Boolean {
        val iter = innerSet.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item === element) {
                iter.remove()
                return true
            }
        }
        return false
    }

    override val size by innerSet::size

    override fun contains(element: T): Boolean {
        for (item in innerSet.iterator()) {
            if (item === element) {
                return true
            }
        }
        return false
    }

    override fun isEmpty() = innerSet.isEmpty()
}
