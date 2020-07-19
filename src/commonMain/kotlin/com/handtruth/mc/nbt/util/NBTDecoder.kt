package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.*
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerialModule

internal class NBTDecoder(
    val tag: Tag<*>,
    override val context: SerialModule,
    override val updateMode: UpdateMode
) : Decoder {
    override fun beginStructure(descriptor: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
        return when(descriptor.kind) {
            StructureKind.LIST -> {
                when (tag) {
                    is ListTag<*> -> NBTListDecoder(tag, context, updateMode)
                    is ByteArrayTag -> NBTByteArrayDecoder(tag, context, updateMode)
                    is IntArrayTag -> NBTIntArrayDecoder(tag, context, updateMode)
                    is LongArrayTag -> NBTLongArrayDecoder(tag, context, updateMode)
                    else -> throw NBTException("tag ${tag.id} can't be treated as list")
                }
            }
            StructureKind.MAP -> {
                validate(tag is CompoundTag, Map::class, tag.id)
                NBTMapDecoder(tag, context, updateMode)
            }
            StructureKind.CLASS -> {
                validate(tag is CompoundTag) { "structures may only be associated with CompountTag" }
                NBTStructDecoder(tag, context, updateMode)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    override fun decodeBoolean(): Boolean {
        validate(tag is ByteTag, Boolean::class, tag.id)
        return tag.byte.toInt() != 0
    }

    override fun decodeByte(): Byte {
        validate(tag is ByteTag, Byte::class, tag.id)
        return tag.byte
    }

    override fun decodeChar(): Char {
        validate(tag is ShortTag, Char::class, tag.id)
        return tag.short.toChar()
    }

    override fun decodeDouble(): Double {
        validate(tag is DoubleTag, Double::class, tag.id)
        return tag.number
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        validate(tag is StringTag, Int::class, tag.id)
        return enumDescriptor.getElementIndex(tag.value)
    }

    override fun decodeFloat(): Float {
        validate(tag is FloatTag, Float::class, tag.id)
        return tag.number
    }

    override fun decodeInt(): Int {
        validate(tag is IntTag, Int::class, tag.id)
        return tag.integer
    }

    override fun decodeLong(): Long {
        validate(tag is LongTag, Long::class, tag.id)
        return tag.integer
    }

    override fun decodeNotNullMark() = tag is EndTag

    override fun decodeNull() = null

    override fun decodeShort(): Short {
        validate(tag is ShortTag, Short::class, tag.id)
        return tag.short
    }

    override fun decodeString(): String {
        validate(tag is StringTag, String::class, tag.id)
        return tag.value
    }

    override fun decodeUnit() = Unit
}