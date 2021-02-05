package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readString
import com.handtruth.mc.nbt.util.writeString
import kotlinx.io.Input
import kotlinx.io.Output

object StringTag : Tag<String> {
    override val type = String::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readString(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) = readString(input)

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: String) {
        writeString(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: String, level: Int) {
        writeString(output, conf.stringConfig.quoteValues, value, conf)
    }

    override fun toString() = "TAG_String"
}
