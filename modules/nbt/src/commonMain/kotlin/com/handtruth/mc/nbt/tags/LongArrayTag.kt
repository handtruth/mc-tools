package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output

object LongArrayTag : Tag<LongArray> {
    override val type = LongArray::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): LongArray {
        val size = readSize(input, conf.binaryConfig)
        validate(size >= 0) { "byte array size is negative: $size" }
        return LongArray(size) { readInt64(input, conf.binaryConfig) }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): LongArray {
        val list = readArray(input, 'L', 'l') { it.toLong() }
        return list.toLongArray()
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: LongArray) {
        writeSize(output, conf.binaryConfig, value.size)
        value.forEach { writeInt64(output, conf.binaryConfig, it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: LongArray, level: Int) {
        joinArray(value.iterator(), output, conf.stringConfig, "L", "l")
    }

    override fun toString() = "TAG_LongArray"
}
