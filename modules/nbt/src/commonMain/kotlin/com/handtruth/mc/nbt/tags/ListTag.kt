package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.TagsContainer
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte

object ListTag : Tag<List<*>> {
    override val type = List::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): List<*> {
        val tag = conf.tagsModule.tagById(input.readByte())
        val size = readSize(input, conf.binaryConfig)
        validate(size >= 0) { "list size is negative" }
        return if (tag == BooleanTag && conf.binaryConfig.compressBooleans) {
            val result = ArrayList<Boolean>(size)
            compressBooleansRead(input, size) { _, value -> result.add(value) }
            result
        } else {
            List(size) { tag.readBinary(input, conf) }
        }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): List<*> {
        input.skipSpace()
        check(input.read() == '[') { "not a list" }
        val tag = deduceTag(input, conf)
        if (tag == EndTag) {
            input.skipSpace()
            check(input.read() == ']') { "unexpected token" }
            return emptyList<Any>()
        }
        return buildList {
            while (true) {
                input.skipSpace()
                when (input.read()) {
                    ']' -> break
                    else -> {
                        input.back()
                        add(tag.readText(input, conf))
                        input.skipSpace()
                        when (val a = input.read()) {
                            ',' -> {
                            }
                            ']' -> break
                            else -> error("unexpected token: $a")
                        }
                    }
                }
            }
        }
    }

    private fun TagsContainer.getTag(value: List<*>): Pair<Byte, Tag<Any>> {
        val first = value.first()
        validate(first != null) { "nulls prohibited in TAG_List" }
        val tag = tagsModule.tagOf(first)
        val id = tagsModule.tagIdOf(tag)
        return id to tag
    }

    private fun TagsContainer.checkItem(tag: Tag<Any>, value: Any?): Any {
        validate(value != null) { "nulls prohibited in TAG_List" }
        val otherTag = tagsModule.tagOf(value)
        validate(tag == otherTag) {
            "list element tag ID is different ($tag expected, got $otherTag)"
        }
        return value
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: List<*>) {
        if (value.isEmpty()) {
            output.writeByte(0)
            writeSize(output, conf.binaryConfig, 0)
        } else {
            val (id, tag) = conf.getTag(value)
            output.writeByte(id)
            writeSize(output, conf.binaryConfig, value.size)
            if ((tag as Tag<*>) == BooleanTag && conf.binaryConfig.compressBooleans) {
                compressBooleansWrite(output, value.size) { value[it] as Boolean }
            } else {
                value.forEach {
                    val item = conf.checkItem(tag, it)
                    tag.writeBinary(output, conf, item)
                }
            }
        }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: List<*>, level: Int) {
        if (value.isEmpty()) {
            output.append("[]")
        } else {
            val (_, tag) = conf.getTag(value)
            smartJoin(
                value.iterator(),
                output,
                prefix = "[",
                postfix = "]",
                pretty = conf.stringConfig.pretty,
                level = level,
                identString = conf.stringConfig.identString
            ) {
                val item = conf.checkItem(tag, it)
                tag.writeText(this, conf, item, level + 1)
            }
        }
    }

    override fun toString() = "TAG_List"
}
