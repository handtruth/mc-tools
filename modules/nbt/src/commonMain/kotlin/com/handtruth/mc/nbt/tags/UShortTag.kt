package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt16
import com.handtruth.mc.nbt.util.writeInt16
import kotlinx.io.Input
import kotlinx.io.Output

object UShortTag : Tag<UShort> {
    override val type = UShort::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readInt16(input, conf.binaryConfig).toUShort()

    override fun readText(input: Reader, conf: NBTStringCodec) =
        readAnyInt(input, 's', true) { it.toUShort() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: UShort) {
        writeInt16(output, conf.binaryConfig, value.toShort())
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: UShort, level: Int) {
        output.append(value.toString())
        output.append("us")
    }

    override fun toString() = "TAG_UShort"
}
