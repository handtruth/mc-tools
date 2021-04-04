package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyFloating
import com.handtruth.mc.nbt.util.readDouble
import com.handtruth.mc.nbt.util.writeDouble
import io.ktor.utils.io.core.*

object DoubleTag : Tag<Double> {
    override val type = Double::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readDouble(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) = readAnyFloating(input, 'd') { it.toDouble() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Double) {
        writeDouble(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Double, level: Int) {
        output.append(value.toString())
        output.append('d')
    }

    override fun toString() = "TAG_Double"
}
