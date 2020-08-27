package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import kotlinx.io.*

interface NBTBinaryCodec {
    val binaryConfig: NBTBinaryConfig
    fun read(input: Input): CompoundTag
    fun write(output: Output, tag: CompoundTag)
}

fun NBTBinaryCodec.read(bytes: ByteArray, start: Int = 0, end: Int = bytes.size): CompoundTag {
    ByteArrayInput(bytes, start, end).use {
        return read(it)
    }
}

fun NBTBinaryCodec.write(tag: CompoundTag): ByteArray {
    ByteArrayOutput().use {
        write(it, tag)
        return it.toByteArray()
    }
}

internal class NBTBinaryCodecImpl(override val binaryConfig: NBTBinaryConfig) : NBTBinaryCodec {
    override fun read(input: Input): CompoundTag = CompoundTag.readBinary(input, binaryConfig)
    override fun write(output: Output, tag: CompoundTag) = tag.writeBinary(output, binaryConfig)
}

fun NBTBinaryCodec(binaryConfig: NBTBinaryConfig = NBTBinaryConfig.Default): NBTBinaryCodec =
    NBTBinaryCodecImpl(binaryConfig)
