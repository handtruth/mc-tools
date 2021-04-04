package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import io.ktor.utils.io.core.*

object ShortArrayTag : Tag<ShortArray> {
    override val type = ShortArray::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): ShortArray {
        val size = readSize(input, conf.binaryConfig)
        validate(size >= 0) { "byte array size is negative: $size" }
        return ShortArray(size) { readInt16(input, conf.binaryConfig) }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): ShortArray {
        val list = readArray(input, 'S', 's') { it.toShort() }
        return list.toShortArray()
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: ShortArray) {
        writeSize(output, conf.binaryConfig, value.size)
        value.forEach { writeInt16(output, conf.binaryConfig, it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: ShortArray, level: Int) {
        joinArray(value.iterator(), output, conf.stringConfig, "S", "s")
    }

    override fun toString() = "TAG_ShortArray"
}
