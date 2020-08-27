package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTDsl
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte

class ListTag<T : Any>(
    override var value: MutableList<Tag<T>>,
    val resolver: TagResolver<T>
) : Tag<List<Tag<T>>>(TagID.List) {
    inline val tagId get() = resolver.id

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        output.writeByte(tagId.ordinal.toByte())
        val value = value
        writeSize(output, conf, value.size)
        value.forEach {
            validate(it.id == tagId) { "list element tag ID is different ($tagId expected, got ${it.id})" }
            it.writeBinary(output, conf)
        }
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        smartJoin(value.iterator(), output, prefix = "[", postfix = "]", pretty = conf.pretty, level = level) {
            it.writeText(this, conf, level + 1)
        }
    }

    companion object : TagResolver<List<Tag<Any>>> {
        private fun readID(input: Input): TagID {
            val type = input.readByte().toInt()
            val values = TagID.values()
            validate(type in values.indices) { "unknown tag ID: $type" }
            return values[type]
        }

        private fun read(input: Input, conf: NBTBinaryConfig, tagId: TagID): MutableList<Tag<Any>> {
            val size = readSize(input, conf)
            validate(size >= 0) { "list size is negative" }
            val resolver = tagId.resolver
            return MutableList(size) { resolver.readBinary(input, conf) }
        }

        override fun readBinary(input: Input, conf: NBTBinaryConfig): Tag<List<Tag<Any>>> {
            val tagId = readID(input)
            val list = read(input, conf, tagId)
            @Suppress("UNCHECKED_CAST")
            return ListTag(list, tagId.resolver as TagResolver<Any>)
        }

        override fun readText(input: Reader, conf: NBTStringConfig): Tag<List<Tag<Any>>> {
            input.skipSpace()
            check(input.read() == '[') { "not a list" }
            val id = deduceTag(input)
            if (id == TagID.End) {
                input.skipSpace()
                check(input.read() == ']') { "unexpected token" }
                return ListTag(mutableListOf(), TagID.End.resolver)
            }
            val result = mutableListOf<Tag<Any>>()
            cycle@while (true) {
                input.skipSpace()
                when (input.read()) {
                    ']' -> break@cycle
                    else -> {
                        input.back()
                        result += id.resolver.readText(input, conf)
                        input.skipSpace()
                        when (val a = input.read()) {
                            ',' -> {}
                            ']' -> break@cycle
                            else -> error("unexpected token: $a")
                        }
                    }
                }
            }
            @Suppress("UNCHECKED_CAST")
            return ListTag(result, id.resolver as TagResolver<Any>)
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

    private class InterfaceIterator<T : Any>(
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

    private class InterfaceList<T : Any>(val resolver: TagResolver<T>, val value: MutableList<Tag<T>>) :
        MutableList<T> {
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

    // builder

    @NBTDsl
    operator fun Tag<T>.unaryPlus(): Tag<T> {
        val tagId = resolver.id
        validate(id == tagId) { "failed to add $id tag to list of $tagId tags" }
        this@ListTag.value.add(this)
        return this
    }

    @NBTDsl
    operator fun T.unaryPlus(): Tag<T> = +resolver.wrap(this)

    @NBTDsl
    fun add(vararg tags: Tag<T>) = tags.forEach { +it }

    @NBTDsl
    fun add(vararg values: T) = values.forEach { +it }
}
