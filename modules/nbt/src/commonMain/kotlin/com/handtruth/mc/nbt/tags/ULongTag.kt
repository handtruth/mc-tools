package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readUInt64
import com.handtruth.mc.nbt.util.writeUInt64
import io.ktor.utils.io.core.*

object ULongTag : Tag<ULong> {
    override val type = ULong::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readUInt64(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) =
        readAnyInt(input, 'l', true) { it.toULong() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: ULong) {
        writeUInt64(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: ULong, level: Int) {
        output.append(value.toString())
        output.append("ul")
    }

    override fun toString() = "TAG_ULong"
}
