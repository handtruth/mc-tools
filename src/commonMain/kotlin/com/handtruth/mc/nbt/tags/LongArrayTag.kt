package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.smartJoin
import com.handtruth.mc.nbt.util.validate
import kotlinx.io.*

class LongArrayTag(override var value: LongArray) : MutableTag<LongArray>(
    TagID.LongArray
) {
    override fun write(output: Output) {
        output.writeInt(value.size)
        value.forEach { output.writeLong(it) }
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
        override fun read(input: Input): LongArrayTag {
            val size = input.readInt()
            validate(size >= 0) { "byte array size is negative: $size" }
            return LongArrayTag(LongArray(size) { input.readLong() })
        }
        override val id get() = TagID.LongArray
        override fun wrap(value: LongArray) = LongArrayTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        smartJoin(value.iterator(), builder, prefix = "[L;", suffix = "l", postfix = "]")
    }
}
