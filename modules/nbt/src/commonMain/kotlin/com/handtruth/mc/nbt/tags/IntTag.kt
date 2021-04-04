package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt32
import com.handtruth.mc.nbt.util.writeInt32
import io.ktor.utils.io.core.*

object IntTag : Tag<Int> {
    override val type = Int::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readInt32(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) = readAnyInt(input, null) { it.toInt() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Int) {
        writeInt32(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Int, level: Int) {
        output.append(value.toString())
    }

    override fun toString() = "TAG_Int"
}
