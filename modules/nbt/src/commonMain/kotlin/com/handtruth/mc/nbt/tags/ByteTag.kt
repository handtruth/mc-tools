package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import io.ktor.utils.io.core.*

object ByteTag : Tag<Byte> {
    override val type = Byte::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = input.readByte()

    override fun readText(input: Reader, conf: NBTStringCodec) = readAnyInt(input, 'b') { it.toByte() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Byte) = output.writeByte(value)

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Byte, level: Int) {
        output.append(value.toString()).append('b')
    }

    override fun toString() = "TAG_ByteTag"
}
