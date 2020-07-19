package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.smartJoin
import com.handtruth.mc.nbt.util.validate
import kotlinx.io.*

class ListTag<T: Any>(
        override var value: MutableList<Tag<T>>,
        val resolver: TagResolver<T>
) : Tag<List<Tag<T>>>(TagID.List) {
    inline val tagId get() = resolver.id

    override fun write(output: Output) {
        output.writeByte(tagId.ordinal.toByte())
        val value = value
        output.writeInt(value.size)
        value.forEach {
            validate(it.id == tagId) { "list element tag ID is different ($tagId expected, got ${it.id})" }
            it.write(output)
        }
    }
    companion object : TagResolver<List<Tag<Any>>> {
        private fun readID(input: Input): TagID {
            val type = input.readByte().toInt()
            val values = TagID.values()
            validate(type in values.indices) { "unknown tag ID: $type" }
            return values[type]
        }
        private fun read(input: Input, tagId: TagID): MutableList<Tag<Any>> {
            val size = input.readInt()
            validate(size >= 0) { "list size is negative" }
            val resolver = tagId.resolver
            return MutableList(size) { resolver.read(input) }
        }
        override fun read(input: Input): Tag<List<Tag<Any>>> {
            val tagId = readID(input)
            val list = read(input, tagId)
            @Suppress("UNCHECKED_CAST")
            return ListTag(list, tagId.resolver as TagResolver<Any>)
        }
        override val id get() = TagID.List
        override fun wrap(value: List<Tag<Any>>): Tag<List<Tag<Any>>> {
            if (value.isEmpty())
                return ListTag(mutableListOf(), EndTag)
            val resolver = value.first().id.resolver
            @Suppress("UNCHECKED_CAST")
            return ListTag(value.toMutableList(), resolver as TagResolver<Any>)
        }
    }

    private class InterfaceIterator<T: Any>(
            val resolver: TagResolver<T>,
            val iter: MutableListIterator<Tag<T>>
    ) : MutableListIterator<T> {
        override fun hasNext() = iter.hasNext()
        override fun next() = iter.next().value
        override fun hasPrevious() = iter.hasPrevious()
        override fun nextIndex() = iter.nextIndex()
        override fun previous() = iter.previous().value
        override fun previousIndex() = iter.previousIndex()
        override fun add(element: T) = iter.add(resolver.wrap(element))
        override fun remove() = iter.remove()
        override fun set(element: T) = iter.set(resolver.wrap(element))
    }

    private class InterfaceList<T: Any>(val resolver: TagResolver<T>, val value: MutableList<Tag<T>>) : MutableList<T> {
        override val size = value.size
        override fun contains(element: T) = value.any { it.value == element }
        override fun containsAll(elements: Collection<T>) = elements.all { contains(it) }
        override fun get(index: Int) = value[index].value
        override fun indexOf(element: T): Int {
            for ((i, each) in value.withIndex())
                if (element == each)
                    return i
            return -1
        }
        override fun isEmpty() = value.isEmpty()
        override fun iterator() = listIterator()
        override fun lastIndexOf(element: T): Int {
            for (i in value.indices.reversed()) {
                if (value[i].value == element)
                    return i
            }
            return -1
        }
        override fun listIterator() =
            InterfaceIterator(resolver, value.listIterator())
        override fun listIterator(index: Int) =
            InterfaceIterator(resolver, value.listIterator(index))
        override fun subList(fromIndex: Int, toIndex: Int) =
            InterfaceList(resolver, value.subList(fromIndex, toIndex))
        override fun add(element: T) = value.add(resolver.wrap(element))
        override fun add(index: Int, element: T) = value.add(index, resolver.wrap(element))
        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            val tags = elements.map { resolver.wrap(it) }
            return value.addAll(index, tags)
        }
        override fun addAll(elements: Collection<T>): Boolean {
            var result = true
            for (element in elements)
                result = result && add(element)
            return result
        }
        override fun clear() = value.clear()
        override fun remove(element: T): Boolean {
            val iter = value.iterator()
            while (iter.hasNext()) {
                if (iter.next().value == element) {
                    iter.remove()
                    return true
                }
            }
            return false
        }
        override fun removeAll(elements: Collection<T>): Boolean {
            var result = false
            elements.forEach { result = result || remove(it) }
            return result
        }
        override fun removeAt(index: Int) = value.removeAt(index).value
        override fun retainAll(elements: Collection<T>): Boolean {
            val iter = value.iterator()
            var result = false
            while (iter.hasNext()) {
                if (iter.next().value !in elements) {
                    iter.remove()
                    result = true
                }
            }
            return result
        }
        override fun set(index: Int, element: T) = value.set(index, resolver.wrap(element)).value
    }

    val values: MutableList<T> = InterfaceList(resolver, value)

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        smartJoin(value.iterator(), builder, prefix = "[", postfix = "]", pretty = pretty, level = level) {
            it.toMojangson(this, pretty, level + 1)
        }
    }

    // builder
    operator fun Tag<T>.unaryPlus(): Tag<T> {
        val tagId = resolver.id
        validate(id == tagId) { "failed to add $id tag to list of $tagId tags" }
        this@ListTag.value.add(this)
        return this
    }
    operator fun T.unaryPlus(): Tag<T> = +resolver.wrap(this)
    fun add(vararg tags: Tag<T>) = tags.forEach { +it }
    fun add(vararg values: T) = values.forEach { +it }
}
