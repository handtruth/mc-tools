package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.util.readString
import com.handtruth.mc.nbt.util.writeString
import io.ktor.utils.io.core.*

interface NBTBinaryCodec : TagsContainer {
    val binaryConfig: NBTBinaryConfig
    fun read(input: Input): Pair<String, Any>
    fun write(output: Output, key: String, value: Any)
}

fun NBTBinaryCodec.read(bytes: ByteArray, start: Int = 0, end: Int = bytes.size): Pair<String, Any> {
    ByteReadPacket(bytes, start, end).use {
        return read(it)
    }
}

fun NBTBinaryCodec.write(key: String, value: Any): ByteArray {
    buildPacket {
        write(this, key, value)
    }.use {
        return it.readBytes()
    }
}

internal class NBTBinaryCodecImpl(
    override val tagsModule: TagsModule,
    override val binaryConfig: NBTBinaryConfig
) : NBTBinaryCodec {
    override fun read(input: Input): Pair<String, Any> {
        val id = input.readByte()
        val key = readString(input, binaryConfig)
        val tag = tagsModule.tagById(id)
        return key to tag.readBinary(input, this)
    }

    override fun write(output: Output, key: String, value: Any) {
        val tag = tagsModule.tagOf(value)
        val id = tagsModule.tagIdOf(tag)
        output.writeByte(id)
        writeString(output, binaryConfig, key)
        tag.writeBinary(output, this, value)
    }
}

@Suppress("FunctionName")
fun NBTBinaryCodec(
    tagsModule: TagsModule = TagsModule.Default,
    binaryConfig: NBTBinaryConfig = NBTBinaryConfig.Default
): NBTBinaryCodec = NBTBinaryCodecImpl(tagsModule, binaryConfig)
