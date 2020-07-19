package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.buildCompoundTag
import com.handtruth.mc.nbt.buildListOfCompoundTags
import com.handtruth.mc.nbt.buildListTag
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte
import kotlin.jvm.JvmName

class CompoundTag(
        override var value: MutableMap<String, Tag<*>>,
        var isRoot: Boolean = false
) : Tag<Map<String, Tag<*>>>(TagID.Compound) {

    override fun write(output: Output) {
        for ((key, value) in value) {
            output.writeByte(value.id.ordinal.toByte())
            writeString(output, key)
            value.write(output)
        }
        if (!isRoot)
            output.writeByte(0)
    }
    companion object : TagResolver<Map<String, Tag<*>>> {
        override fun read(input: Input): CompoundTag {
            val tags = TagID.values()
            val result: MutableMap<String, Tag<*>> = hashMapOf()
            while (true) {
                if (input.exhausted())
                    return CompoundTag(result, true)
                val tagId = input.readByte().toInt()
                validate(tagId in tags.indices) { "unknown tag ID in compound tag: $tagId" }
                val tag = tags[tagId]
                if (tag == TagID.End)
                    break
                val key = readString(input)
                val value = tag.resolver.read(input)
                val previous = result.put(key, value)
                validate(previous == null) { "value with that name was already specified" }
            }
            return CompoundTag(result)
        }
        override val id get() = TagID.Compound
        override fun wrap(value: Map<String, Tag<*>>) = CompoundTag(value.toMutableMap())
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        smartJoin(value.iterator(), builder, prefix = "{", postfix = "}", pretty = pretty, level = level) {
            (key, value) -> append('"').append(quoteString(key)).append(if (pretty) "\": " else "\":")
            value.toMojangson(this, pretty, level + 1)
        }
    }

    // builders
    operator fun String.invoke(): EndTag {
        value.remove(this)
        return empty
    }
    operator fun String.invoke(@Suppress("UNUSED_PARAMETER") value: Nothing?) = invoke()
    infix fun <T: Any> String.tag(@Suppress("UNUSED_PARAMETER") value: Nothing?) = invoke()

    operator fun <T: Any> String.invoke(tag: Tag<T>): Tag<T> {
        value[this] = tag
        return tag
    }
    @JvmName("invokeEnd")
    operator fun String.invoke(@Suppress("UNUSED_PARAMETER") tag: Tag<Nothing>) = invoke()
    infix fun <T: Any> String.tag(value: Tag<T>) = invoke(value)
    @JvmName("tagEnd")
    infix fun String.tag(@Suppress("UNUSED_PARAMETER") value: Tag<Nothing>) = invoke()
    operator fun String.invoke(value: Byte): ByteTag {
        val tag = ByteTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.byte(value: Byte) = invoke(value)
    operator fun String.invoke(value: Short): ShortTag {
        val tag = ShortTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.short(value: Short) = invoke(value)
    operator fun String.invoke(value: Int): IntTag {
        val tag = IntTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.int(value: Int) = invoke(value)
    operator fun String.invoke(value: Long): LongTag {
        val tag = LongTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.long(value: Long) = invoke(value)
    operator fun String.invoke(value: Float): FloatTag {
        val tag = FloatTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.float(value: Float) = invoke(value)
    operator fun String.invoke(value: Double): DoubleTag {
        val tag = DoubleTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.double(value: Double) = invoke(value)
    operator fun String.invoke(value: ByteArray): ByteArrayTag {
        val tag = ByteArrayTag(value)
        invoke(tag)
        return tag
    }
    fun String.array(vararg value: Byte) = invoke(value)
    fun String.array(vararg value: Int) = invoke(value)
    fun String.array(vararg value: Long) = invoke(value)
    fun String.byteArray(vararg value: Byte) = invoke(value)
    fun String.intArray(vararg value: Int) = invoke(value)
    fun String.longArray(vararg value: Long) = invoke(value)
    operator fun String.invoke(value: String): StringTag {
        val tag = StringTag(value)
        invoke(tag)
        return tag
    }
    infix fun String.string(value: String) = invoke(value)
    fun <T: Any> String.listWith(resolver: TagResolver<T>, vararg tags: Tag<T>): ListTag<T> {
        val tag = ListTag(mutableListOf(*tags), resolver)
        invoke(tag)
        return tag
    }
    fun <T: Any> String.listWith(resolver: TagResolver<T>, vararg values: T): ListTag<T> {
        val list = mutableListOf<Tag<T>>()
        values.mapTo(list) { resolver.wrap(it) }
        val tag = ListTag(list, resolver)
        invoke(tag)
        return tag
    }
    fun <T: Any> String.listWith(resolver: TagResolver<T>, values: List<T>): ListTag<T> {
        val list = mutableListOf<Tag<T>>()
        values.mapTo(list) { resolver.wrap(it) }
        val tag = ListTag(list, resolver)
        invoke(tag)
        return tag
    }
    fun String.listOf(vararg values: Byte) = listWith(ByteTag, values.toList())
    fun String.listOf(vararg values: Short) = listWith(ShortTag, values.toList())
    fun String.listOf(vararg values: Int) = listWith(IntTag, values.toList())
    fun String.listOf(vararg values: Long) = listWith(LongTag, values.toList())
    fun String.listOf(vararg values: Float) = listWith(FloatTag, values.toList())
    fun String.listOf(vararg values: Double) = listWith(DoubleTag, values.toList())
    fun String.listOf(vararg values: ByteArray) = listWith(ByteArrayTag, *values)
    fun String.listOf(vararg values: String) = listWith(StringTag, *values)
    fun String.listOf(vararg values: IntArray) = listWith(IntArrayTag, *values)
    fun String.listOf(vararg values: LongArray) = listWith(LongArrayTag, *values)
    operator fun String.invoke(value: IntArray): IntArrayTag {
        val tag = IntArrayTag(value)
        invoke(tag)
        return tag
    }
    operator fun String.invoke(value: LongArray): LongArrayTag {
        val tag = LongArrayTag(value)
        invoke(tag)
        invoke(tag)
        return tag
    }

    inline fun <T: Any> String.list(resolver: TagResolver<T>, block: ListTag<T>.() -> Unit): ListTag<T> {
        val tag = buildListTag(resolver, block)
        invoke(tag)
        return tag
    }
    inline infix fun String.compounds(block: ListTag<Map<String, Tag<*>>>.() -> Unit): ListTag<Map<String, Tag<*>>> {
        val tag = buildListOfCompoundTags(block)
        invoke(tag)
        return tag
    }
    inline operator fun String.invoke(block: CompoundTag.() -> Unit): CompoundTag {
        val tag = buildCompoundTag(false, block)
        invoke(tag)
        return tag
    }
}
