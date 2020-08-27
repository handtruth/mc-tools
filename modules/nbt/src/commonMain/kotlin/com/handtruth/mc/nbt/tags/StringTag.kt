package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output

class StringTag(override var value: String = "") : MutableTag<String>(TagID.String) {
    override fun writeBinary(output: Output, conf: NBTBinaryConfig) = writeString(output, conf, value)

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        writeString(output, conf.quoteValues, value)
    }

    companion object : TagResolver<String> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = StringTag(readString(input, conf))
        override fun readText(input: Reader, conf: NBTStringConfig) = StringTag(readString(input))

        override val id get() = TagID.String
        override fun wrap(value: String) = StringTag(value)
    }
}
