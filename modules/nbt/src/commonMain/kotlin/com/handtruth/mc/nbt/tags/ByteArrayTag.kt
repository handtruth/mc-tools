package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.smartJoin
import com.handtruth.mc.nbt.util.validate
import kotlinx.io.*

class ByteArrayTag(override var value: ByteArray) : MutableTag<ByteArray>(
    TagID.ByteArray
) {
    override fun write(output: Output) {
        output.writeInt(value.size)
        // TODO: Improve when fixed
        value.forEach { output.writeByte(it) }
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
        override fun read(input: Input): ByteArrayTag {
            val size = input.readInt()
            validate(size >= 0) { "byte array size is negative: $size" }
            return ByteArrayTag(ByteArray(size) { input.readByte() })
        }
        override val id get() = TagID.ByteArray
        override fun wrap(value: ByteArray) = ByteArrayTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        smartJoin(value.iterator(), builder, prefix = "[B;", suffix = "b", postfix = "]")
    }
}
