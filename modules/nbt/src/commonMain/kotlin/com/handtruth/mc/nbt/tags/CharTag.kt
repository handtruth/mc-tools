package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readChar
import com.handtruth.mc.nbt.util.readInt16
import com.handtruth.mc.nbt.util.writeInt16
import io.ktor.utils.io.core.*

object CharTag : Tag<Char> {
    override val type = Char::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readInt16(input, conf.binaryConfig).toInt().toChar()

    override fun readText(input: Reader, conf: NBTStringCodec) = readChar(input)

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Char) {
        writeInt16(output, conf.binaryConfig, value.code.toShort())
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Char, level: Int) {
        output.append('\'')
        output.append(value)
        output.append('\'')
    }

    override fun toString() = "TAG_Char"
}
