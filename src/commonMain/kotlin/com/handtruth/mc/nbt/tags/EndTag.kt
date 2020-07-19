package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output

class EndTag internal constructor(): Tag<Nothing>(TagID.End) {
    override val value get() = throw UnsupportedOperationException()
    override fun write(output: Output) {}

    companion object : TagResolver<Nothing> {
        override fun read(input: Input) = throw NotImplementedError("end tag is special")
        override val id get() = TagID.End
        override fun wrap(value: Nothing) = throw NotImplementedError()
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {}
}
