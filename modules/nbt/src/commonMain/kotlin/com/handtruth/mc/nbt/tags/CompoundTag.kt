package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.*
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte
import kotlin.jvm.JvmName

class CompoundTag(
    override var value: MutableMap<String, Tag<*>>,
    var isRoot: Boolean = false
) : Tag<Map<String, Tag<*>>>(TagID.Compound) {

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        for ((key, value) in value) {
            output.writeByte(value.id.ordinal.toByte())
            writeString(output, conf, key)
            value.writeBinary(output, conf)
        }
        if (!isRoot) {
            output.writeByte(0)
        }
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        smartJoin(
            value.iterator(),
            output,
            prefix = "{",
            postfix = "}",
            pretty = conf.pretty,
            level = level
        ) { (key, value) ->
            writeString(this, conf.quoteKeys, key)
            append(if (conf.pretty) ": " else ":")
            value.writeText(this, conf, level + 1)
        }
    }

    companion object : TagResolver<Map<String, Tag<*>>> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig): CompoundTag {
            val tags = TagID.values()
            val result: MutableMap<String, Tag<*>> = hashMapOf()
            while (true) {
                if (input.exhausted()) {
                    return CompoundTag(result, true)
                }
                val tagId = input.readByte().toInt()
                validate(tagId in tags.indices) { "unknown tag ID in compound tag: $tagId" }
                val tag = tags[tagId]
                if (tag == TagID.End) {
                    break
                }
                val key = readString(input, conf)
                val value = tag.resolver.readBinary(input, conf)
                val previous = result.put(key, value)
                validate(previous == null) { "value with that name was already specified" }
            }
            return CompoundTag(result)
        }

        override fun readText(input: Reader, conf: NBTStringConfig): CompoundTag {
            input.skipSpace()
            if (input.read() != '{') {
                error("not an compound tag")
            }
            val result: MutableMap<String, Tag<*>> = hashMapOf()
            cycle@while (true) {
                when (deduceTag(input)) {
                    TagID.End -> {
                        input.skipSpace()
                        check(input.read() == '}') { "unexpected token" }
                        break@cycle
                    }
                    TagID.String -> {}
                    else -> error("string expected as key")
                }
                val key = readString(input)
                input.skipSpace()
                check(input.read() == ':') { "key-value delimiter expected" }
                val valueTag = deduceTag(input)
                check(valueTag != TagID.End) { "value expected" }
                val value = valueTag.resolver.readText(input, conf)
                result[key] = value
                input.skipSpace()
                when (input.read()) {
                    ',' -> {}
                    '}' -> break@cycle
                    else -> error("unexpected token")
                }
            }
            return CompoundTag(result)
        }

        override val id get() = TagID.Compound
        override fun wrap(value: Map<String, Tag<*>>) = CompoundTag(value.toMutableMap())
    }

    // builders

    @NBTDsl
    operator fun String.invoke(): EndTag {
        value.remove(this)
        return empty
    }

    @NBTDsl
    operator fun String.invoke(@Suppress("UNUSED_PARAMETER") value: Nothing?) = invoke()

    @NBTDsl
    infix fun String.tag(@Suppress("UNUSED_PARAMETER") value: Nothing?) = invoke()

    @NBTDsl
    operator fun <T : Any> String.invoke(tag: Tag<T>): Tag<T> {
        value[this] = tag
        return tag
    }

    @NBTDsl
    @JvmName("invokeEnd")
    operator fun String.invoke(@Suppress("UNUSED_PARAMETER") tag: Tag<Nothing>) = invoke()

    @NBTDsl
    infix fun <T : Any> String.tag(value: Tag<T>) = invoke(value)

    @NBTDsl
    @JvmName("tagEnd")
    infix fun String.tag(@Suppress("UNUSED_PARAMETER") value: Tag<Nothing>) = invoke()

    @NBTDsl
    operator fun String.invoke(value: Byte): ByteTag {
        val tag = ByteTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.byte(value: Byte) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: Short): ShortTag {
        val tag = ShortTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.short(value: Short) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: Int): IntTag {
        val tag = IntTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.int(value: Int) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: Long): LongTag {
        val tag = LongTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.long(value: Long) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: Float): FloatTag {
        val tag = FloatTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.float(value: Float) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: Double): DoubleTag {
        val tag = DoubleTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.double(value: Double) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: ByteArray): ByteArrayTag {
        val tag = ByteArrayTag(value)
        invoke(tag)
        return tag
    }

    fun String.array(vararg value: Byte) = invoke(value)

    @NBTDsl
    fun String.array(vararg value: Int) = invoke(value)

    @NBTDsl
    fun String.array(vararg value: Long) = invoke(value)

    @NBTDsl
    fun String.byteArray(vararg value: Byte) = invoke(value)

    @NBTDsl
    fun String.intArray(vararg value: Int) = invoke(value)

    @NBTDsl
    fun String.longArray(vararg value: Long) = invoke(value)

    @NBTDsl
    operator fun String.invoke(value: String): StringTag {
        val tag = StringTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    infix fun String.string(value: String) = invoke(value)

    fun <T : Any> String.listWith(resolver: TagResolver<T>, vararg tags: Tag<T>): ListTag<T> {
        val tag = ListTag(mutableListOf(*tags), resolver)
        invoke(tag)
        return tag
    }

    fun <T : Any> String.listWith(resolver: TagResolver<T>, vararg values: T): ListTag<T> {
        val list = mutableListOf<Tag<T>>()
        values.mapTo(list) { resolver.wrap(it) }
        val tag = ListTag(list, resolver)
        invoke(tag)
        return tag
    }

    fun <T : Any> String.listWith(resolver: TagResolver<T>, values: List<T>): ListTag<T> {
        val list = mutableListOf<Tag<T>>()
        values.mapTo(list) { resolver.wrap(it) }
        val tag = ListTag(list, resolver)
        invoke(tag)
        return tag
    }

    @NBTDsl
    fun String.listOf(vararg values: Byte) = listWith(ByteTag, values.toList())

    @NBTDsl
    fun String.listOf(vararg values: Short) = listWith(ShortTag, values.toList())

    @NBTDsl
    fun String.listOf(vararg values: Int) = listWith(IntTag, values.toList())

    @NBTDsl
    fun String.listOf(vararg values: Long) = listWith(LongTag, values.toList())

    @NBTDsl
    fun String.listOf(vararg values: Float) = listWith(FloatTag, values.toList())

    @NBTDsl
    fun String.listOf(vararg values: Double) = listWith(DoubleTag, values.toList())

    @NBTDsl
    fun String.listOf(vararg values: ByteArray) = listWith(ByteArrayTag, *values)

    @NBTDsl
    fun String.listOf(vararg values: String) = listWith(StringTag, *values)

    @NBTDsl
    fun String.listOf(vararg values: IntArray) = listWith(IntArrayTag, *values)

    @NBTDsl
    fun String.listOf(vararg values: LongArray) = listWith(LongArrayTag, *values)

    @NBTDsl
    operator fun String.invoke(value: IntArray): IntArrayTag {
        val tag = IntArrayTag(value)
        invoke(tag)
        return tag
    }

    @NBTDsl
    operator fun String.invoke(value: LongArray): LongArrayTag {
        val tag = LongArrayTag(value)
        invoke(tag)
        invoke(tag)
        return tag
    }

    @NBTDsl
    inline fun <T : Any> String.list(resolver: TagResolver<T>, block: ListTag<T>.() -> Unit): ListTag<T> {
        val tag = buildListTag(resolver, block)
        invoke(tag)
        return tag
    }

    @NBTDsl
    inline infix fun String.compounds(block: ListTag<Map<String, Tag<*>>>.() -> Unit): ListTag<Map<String, Tag<*>>> {
        val tag = buildListOfCompoundTags(block)
        invoke(tag)
        return tag
    }

    @NBTDsl
    inline operator fun String.invoke(block: CompoundTag.() -> Unit): CompoundTag {
        val tag = buildCompoundTag(false, block)
        invoke(tag)
        return tag
    }
}
