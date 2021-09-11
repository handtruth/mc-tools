package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.util.readString
import com.handtruth.mc.nbt.util.writeString
import io.ktor.utils.io.core.*

interface NBTBinaryCodec : TagsContainer {
    val binaryConfig: NBTBinaryConfig
    fun readNamedBinary(input: Input): Pair<String, Any>
    fun readBinary(input: Input): Any
    fun writeNamedBinary(output: Output, key: String, value: Any)
    fun write(output: Output, value: Any)
}

fun NBTBinaryCodec.readNamedBinary(bytes: ByteArray, start: Int = 0, end: Int = bytes.size): Pair<String, Any> {
    ByteReadPacket(bytes, start, end).use {
        return readNamedBinary(it)
    }
}

fun NBTBinaryCodec.readBinary(bytes: ByteArray, start: Int = 0, end: Int = bytes.size): Any {
    ByteReadPacket(bytes, start, end).use {
        return readBinary(it)
    }
}

fun NBTBinaryCodec.writeNamedBinary(key: String, value: Any): ByteArray {
    buildPacket {
        writeNamedBinary(this, key, value)
    }.use {
        return it.readBytes()
    }
}

fun NBTBinaryCodec.writeBinary(value: Any): ByteArray {
    buildPacket {
        write(this, value)
    }.use {
        return it.readBytes()
    }
}

internal class NBTBinaryCodecImpl(
    override val tagsModule: TagsModule,
    override val binaryConfig: NBTBinaryConfig
) : NBTBinaryCodec {
    override fun readNamedBinary(input: Input): Pair<String, Any> {
        val id = input.readByte()
        val key = readString(input, binaryConfig)
        val tag = tagsModule.tagById(id)
        return key to tag.readBinary(input, this)
    }

    override fun readBinary(input: Input): Any {
        val id = input.readByte()
        val tag = tagsModule.tagById(id)
        return tag.readBinary(input, this)
    }

    override fun writeNamedBinary(output: Output, key: String, value: Any) {
        val tag = tagsModule.tagOf(value)
        val id = tagsModule.tagIdOf(tag)
        output.writeByte(id)
        writeString(output, binaryConfig, key)
        tag.writeBinary(output, this, value)
    }

    override fun write(output: Output, value: Any) {
        val tag = tagsModule.tagOf(value)
        val id = tagsModule.tagIdOf(tag)
        output.writeByte(id)
        tag.writeBinary(output, this, value)
    }
}

@Suppress("FunctionName")
fun NBTBinaryCodec(
    tagsModule: TagsModule = TagsModule.Default,
    binaryConfig: NBTBinaryConfig = NBTBinaryConfig.Default
): NBTBinaryCodec = NBTBinaryCodecImpl(tagsModule, binaryConfig)
