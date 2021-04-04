package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt64
import com.handtruth.mc.nbt.util.writeInt64
import io.ktor.utils.io.core.*

object LongTag : Tag<Long> {
    override val type = Long::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = readInt64(input, conf.binaryConfig)

    override fun readText(input: Reader, conf: NBTStringCodec) = readAnyInt(input, 'l') { it.toLong() }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Long) {
        writeInt64(output, conf.binaryConfig, value)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Long, level: Int) {
        output.append(value.toString())
        output.append('l')
    }

    override fun toString() = "TAG_Long"
}
