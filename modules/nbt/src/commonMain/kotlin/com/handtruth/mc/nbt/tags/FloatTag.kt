package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyFloating
import com.handtruth.mc.nbt.util.readFloat
import com.handtruth.mc.nbt.util.writeFloat
import io.ktor.utils.io.core.*

object FloatTag : Tag<Float> {
    override val type = Float::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readFloat(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) = readAnyFloating(input, 'f') { it.toFloat() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Float) {
        writeFloat(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Float, level: Int) {
        output.append(value.toString())
        output.append('f')
    }

    override fun toString() = "TAG_Float"
}
