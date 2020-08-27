package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.util.Position
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.StringReader
import com.handtruth.mc.nbt.util.deduceTag
import kotlinx.io.*

interface NBTStringCodec {
    val stringConfig: NBTStringConfig
    fun read(reader: Reader): Tag<*>
    fun write(appendable: Appendable, tag: Tag<*>)
}

fun NBTStringCodec.read(string: String, start: Int = 0, end: Int = string.length): Tag<*> {
    return StringReader(string, start, end).use { read(it) }
}

fun NBTStringCodec.write(tag: Tag<*>): String {
    return buildString { write(this, tag) }
}

class NBTParseException(val position: Position, inner: Exception) :
    RuntimeException("${inner.message} (line: ${position.line}, column: ${position.column})", inner)

internal class NBTStringCodecImpl(override val stringConfig: NBTStringConfig) : NBTStringCodec {
    override fun read(reader: Reader): Tag<*> {
        try {
            val id = deduceTag(reader)
            val result = id.resolver.readText(reader, stringConfig)
            if (result is CompoundTag)
                result.isRoot = true
            return result
        } catch (e: Exception) {
            throw NBTParseException(reader.position(), e)
        }
    }

    override fun write(appendable: Appendable, tag: Tag<*>) {
        tag.writeText(appendable, stringConfig, 0)
    }
}

fun NBTStringCodec(stringConfig: NBTStringConfig = NBTStringConfig.Default): NBTStringCodec =
    NBTStringCodecImpl(stringConfig)
