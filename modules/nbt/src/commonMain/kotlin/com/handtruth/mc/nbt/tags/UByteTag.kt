package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readUByte
import kotlinx.io.writeUByte

object UByteTag : Tag<UByte> {
    override val type = UByte::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = input.readUByte()

    override fun readText(input: Reader, conf: NBTStringCodec) =
        readAnyInt(input, 'b', true) { it.toUByte() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: UByte) = output.writeUByte(value)

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: UByte, level: Int) {
        output.append(value.toString()).append("ub")
    }

    override fun toString() = "TAG_UByte"
}
