package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import kotlinx.io.Input
import kotlinx.io.Output

class EndTag internal constructor() : Tag<Nothing>(TagID.End) {
    override val value get() = throw UnsupportedOperationException()
    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        output.writeByte(0)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {}

    companion object : TagResolver<Nothing> {
        private inline val throwIt: Nothing get() = throw NotImplementedError("end tag is not for read")
        override fun readBinary(input: Input, conf: NBTBinaryConfig): Nothing = throwIt
        override fun readText(input: Reader, conf: NBTStringConfig): Nothing = throwIt
        override val id get() = TagID.End
        override fun wrap(value: Nothing) = throw NotImplementedError()
    }
}
