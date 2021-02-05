package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readUInt32
import com.handtruth.mc.nbt.util.writeUInt32
import kotlinx.io.Input
import kotlinx.io.Output

object UIntTag : Tag<UInt> {
    override val type = UInt::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readUInt32(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) =
        readAnyInt(input, null, true) { it.toUInt() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: UInt) {
        writeUInt32(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: UInt, level: Int) {
        output.append(value.toString()).append('u')
    }

    override fun toString() = "TAG_UInt"
}
