package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output

object IntArrayTag : Tag<IntArray> {
    override val type = IntArray::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): IntArray {
        val size = readSize(input, conf.binaryConfig)
        validate(size >= 0) { "byte array size is negative: $size" }
        return IntArray(size) { readInt32(input, conf.binaryConfig) }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): IntArray {
        val list = readArray(input, 'I', null) { it.toInt() }
        return list.toIntArray()
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: IntArray) {
        writeSize(output, conf.binaryConfig, value.size)
        value.forEach { writeInt32(output, conf.binaryConfig, it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: IntArray, level: Int) {
        joinArray(value.iterator(), output, conf.stringConfig, "I", "")
    }

    override fun toString() = "TAG_IntArray"
}
