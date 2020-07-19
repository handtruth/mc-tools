package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.quoteString
import com.handtruth.mc.nbt.util.readString
import com.handtruth.mc.nbt.util.writeString
import kotlinx.io.Input
import kotlinx.io.Output

data class StringTag(override var value: String = "") : MutableTag<String>(TagID.String) {
    override fun write(output: Output) = writeString(output, value)
    companion object : TagResolver<String> {
        override fun read(input: Input) = StringTag(readString(input))
        override val id get() = TagID.String
        override fun wrap(value: String) = StringTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append('"')
        builder.append(quoteString(value))
        builder.append('"')
    }
}
