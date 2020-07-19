package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.smartJoin
import com.handtruth.mc.nbt.util.validate
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readInt
import kotlinx.io.writeInt

class IntArrayTag(override var value: IntArray) : MutableTag<IntArray>(
    TagID.IntArray
) {
    override fun write(output: Output) {
        output.writeInt(value.size)
        value.forEach { output.writeInt(it) }
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
        override fun read(input: Input): IntArrayTag {
            val size = input.readInt()
            validate(size >= 0) { "byte array size is negative: $size" }
            return IntArrayTag(IntArray(size) { input.readInt() })
        }
        override val id get() = TagID.IntArray
        override fun wrap(value: IntArray) = IntArrayTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        smartJoin(value.iterator(), builder, "[I;", postfix = "]")
    }
}
