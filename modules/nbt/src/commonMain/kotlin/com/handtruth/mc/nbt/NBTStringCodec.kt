package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.util.Position
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.StringReader
import com.handtruth.mc.nbt.util.deduceTag
import kotlinx.io.use

interface NBTStringCodec : TagsContainer {
    val stringConfig: NBTStringConfig
    fun read(reader: Reader): Any
    fun write(appendable: Appendable, value: Any)
}

fun NBTStringCodec.read(string: String, start: Int = 0, end: Int = string.length): Any {
    return StringReader(string, start, end).use { read(it) }
}

fun NBTStringCodec.write(value: Any): String {
    return buildString { write(this, value) }
}

class NBTParseException(val position: Position, inner: Exception) :
    RuntimeException("${inner.message} (line: ${position.line}, column: ${position.column})", inner)

internal class NBTStringCodecImpl(
    override val tagsModule: TagsModule,
    override val stringConfig: NBTStringConfig
) : NBTStringCodec {
    override fun read(reader: Reader): Any {
        try {
            val tag = deduceTag(reader, this)
            return tag.readText(reader, this)
        } catch (e: Exception) {
            throw NBTParseException(reader.position(), e)
        }
    }

    override fun write(appendable: Appendable, value: Any) {
        tagsModule.tagOf(value).writeText(appendable, this, value)
    }
}

@Suppress("FunctionName")
fun NBTStringCodec(
    tagsModule: TagsModule = TagsModule.Default,
    stringConfig: NBTStringConfig = NBTStringConfig.Default
): NBTStringCodec = NBTStringCodecImpl(tagsModule, stringConfig)
