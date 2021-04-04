package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt16
import com.handtruth.mc.nbt.util.writeInt16
import io.ktor.utils.io.core.*

object ShortTag : Tag<Short> {
    override val type = Short::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readInt16(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) = readAnyInt(input, 's') { it.toShort() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Short) {
        writeInt16(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Short, level: Int) {
        output.append(value.toString())
        output.append('s')
    }

    override fun toString() = "TAG_Short"
}
