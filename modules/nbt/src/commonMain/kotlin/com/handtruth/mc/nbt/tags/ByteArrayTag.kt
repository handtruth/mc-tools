package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import io.ktor.utils.io.core.*

object ByteArrayTag : Tag<ByteArray> {
    override val type = ByteArray::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): ByteArray {
        val size = readSize(input, conf.binaryConfig)
        validate(size >= 0) { "byte array size is negative: $size" }
        return ByteArray(size) { input.readByte() }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): ByteArray {
        val list = readArray(input, 'B', 'b') { it.toByte() }
        return list.toByteArray()
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: ByteArray) {
        writeSize(output, conf.binaryConfig, value.size)
        // TODO: Improve when fixed
        value.forEach { output.writeByte(it) }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: ByteArray, level: Int) {
        joinArray(value.iterator(), output, conf.stringConfig, "B", "b")
    }

    override fun toString() = "TAG_ByteArray"
}
