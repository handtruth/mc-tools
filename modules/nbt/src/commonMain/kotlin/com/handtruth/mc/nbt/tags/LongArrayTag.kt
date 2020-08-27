package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output

class LongArrayTag(override var value: LongArray) : MutableTag<LongArray>(
    TagID.LongArray
) {
    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeSize(output, conf, value.size)
        value.forEach { writeInt64(output, conf, it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        joinArray(value.iterator(), output, conf, 'L', "l")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as LongArrayTag
        if (!value.contentEquals(other.value)) return false
        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }

    companion object : TagResolver<LongArray> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig): LongArrayTag {
            val size = readSize(input, conf)
            validate(size >= 0) { "byte array size is negative: $size" }
            return LongArrayTag(LongArray(size) { readInt64(input, conf) })
        }

        override fun readText(input: Reader, conf: NBTStringConfig): LongArrayTag {
            val list = readArray(input, 'L', 'l') { it.toLong() }
            return LongArrayTag(list.toLongArray())
        }

        override val id get() = TagID.LongArray
        override fun wrap(value: LongArray) = LongArrayTag(value)
    }
}
