package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.*
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readSize
import com.handtruth.mc.nbt.util.writeSize
import kotlinx.io.*

class ByteArrayTag(override var value: ByteArray) : MutableTag<ByteArray>(
    TagID.ByteArray
) {
    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeSize(output, conf, value.size)
        // TODO: Improve when fixed
        value.forEach { output.writeByte(it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        joinArray(value.iterator(), output, conf, 'B', "b")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ByteArrayTag
        if (!value.contentEquals(other.value)) return false
        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }

    companion object : TagResolver<ByteArray> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig): ByteArrayTag {
            val size = readSize(input, conf)
            validate(size >= 0) { "byte array size is negative: $size" }
            return ByteArrayTag(ByteArray(size) { input.readByte() })
        }

        override fun readText(input: Reader, conf: NBTStringConfig): ByteArrayTag {
            val list = readArray(input, 'B', 'b') { it.toByte() }
            return ByteArrayTag(list.toByteArray())
        }

        override val id get() = TagID.ByteArray
        override fun wrap(value: ByteArray) = ByteArrayTag(value)
    }
}
