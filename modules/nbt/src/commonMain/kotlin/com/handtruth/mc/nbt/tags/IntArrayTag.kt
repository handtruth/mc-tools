package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.*
import com.handtruth.mc.nbt.util.readSize
import com.handtruth.mc.nbt.util.smartJoin
import com.handtruth.mc.nbt.util.writeInt32
import com.handtruth.mc.nbt.util.writeSize
import kotlinx.io.Input
import kotlinx.io.Output

class IntArrayTag(override var value: IntArray) : MutableTag<IntArray>(
    TagID.IntArray
) {
    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeSize(output, conf, value.size)
        value.forEach { writeInt32(output, conf, it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        joinArray(value.iterator(), output, conf, 'I', "")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IntArrayTag
        if (!value.contentEquals(other.value)) return false
        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }

    companion object : TagResolver<IntArray> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig): IntArrayTag {
            val size = readSize(input, conf)
            validate(size >= 0) { "byte array size is negative: $size" }
            return IntArrayTag(IntArray(size) { readInt32(input, conf) })
        }

        override fun readText(input: Reader, conf: NBTStringConfig): IntArrayTag {
            val list = readArray(input, 'I', null) { it.toInt() }
            return IntArrayTag(list.toIntArray())
        }

        override val id get() = TagID.IntArray
        override fun wrap(value: IntArray) = IntArrayTag(value)
    }
}
