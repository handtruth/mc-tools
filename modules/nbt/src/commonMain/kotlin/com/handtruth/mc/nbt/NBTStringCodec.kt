package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.util.Position
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.StringReader
import com.handtruth.mc.nbt.util.deduceTag
import io.ktor.utils.io.core.*

interface NBTStringCodec : TagsContainer {
    val stringConfig: NBTStringConfig
    fun readText(reader: Reader): Any
    fun writeText(appendable: Appendable, value: Any)
}

fun NBTStringCodec.readText(string: String, start: Int = 0, end: Int = string.length): Any {
    return StringReader(string, start, end).use { readText(it) }
}

fun NBTStringCodec.writeText(value: Any): String {
    return buildString { writeText(this, value) }
}

class NBTParseException(val position: Position, inner: Exception) :
    RuntimeException("${inner.message} (line: ${position.line}, column: ${position.column})", inner)

internal class NBTStringCodecImpl(
    override val tagsModule: TagsModule,
    override val stringConfig: NBTStringConfig
) : NBTStringCodec {
    override fun readText(reader: Reader): Any {
        try {
            val tag = deduceTag(reader, this)
            return tag.readText(reader, this)
        } catch (e: Exception) {
            throw NBTParseException(reader.position(), e)
        }
    }

    override fun writeText(appendable: Appendable, value: Any) {
        tagsModule.tagOf(value).writeText(appendable, this, value)
    }
}

@Suppress("FunctionName")
fun NBTStringCodec(
    tagsModule: TagsModule = TagsModule.Default,
    stringConfig: NBTStringConfig = NBTStringConfig.Default
): NBTStringCodec = NBTStringCodecImpl(tagsModule, stringConfig)
