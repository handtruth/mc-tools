package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte

object BooleanArrayTag : Tag<BooleanArray> {
    override val type = BooleanArray::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): BooleanArray {
        val size = readSize(input, conf.binaryConfig)
        return if (conf.binaryConfig.compressBooleans) {
            val result = BooleanArray(size)
            compressBooleansRead(input, size) { i, value -> result[i] = value }
            result
        } else {
            BooleanArray(size) { input.readByte() != 0.toByte() }
        }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): BooleanArray {
        input.skipSpace()
        check(input.read() == '[' && input.read() == 'b' && input.read() == ';') {
            "boolean array begin expected"
        }
        input.skipSpace()
        val result = mutableListOf<Boolean>()
        while (true) {
            input.skipSpace()
            return when (input.read()) {
                ']' -> result
                else -> {
                    input.back()
                    result += BooleanTag.readText(input, conf)
                    input.skipSpace()
                    when (input.read()) {
                        ',' -> continue
                        ']' -> result
                        else -> error("unexpected token")
                    }
                }
            }.toBooleanArray()
        }
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: BooleanArray) {
        val size = value.size
        writeSize(output, conf.binaryConfig, size)
        if (conf.binaryConfig.compressBooleans) {
            compressBooleansWrite(output, size) { value[it] }
        } else {
            for (item in value) {
                if (item) {
                    output.writeByte(1)
                } else {
                    output.writeByte(0)
                }
            }
        }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: BooleanArray, level: Int) {
        joinArray(value.iterator(), output, conf.stringConfig, "b", "")
    }

    override fun toString() = "TAG_BooleanArray"
}
