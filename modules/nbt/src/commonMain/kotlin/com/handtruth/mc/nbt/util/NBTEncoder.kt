package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTListType
import com.handtruth.mc.nbt.tags.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

internal class NBTEncoder(
    override val serializersModule: SerializersModule
) : Encoder {
    var tag: Tag<*> = Tag.empty
        internal set

    override fun beginStructure(
        descriptor: SerialDescriptor
    ): CompositeEncoder {
        return NBTStructEncoder(serializersModule, this)
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val tagId = (descriptor.annotations.find { it is NBTListType } as? NBTListType)?.id ?: EndTag.id
                NBTListEncoder(serializersModule, this, tagId.resolver)
            }
            StructureKind.MAP -> NBTMapEncoder(serializersModule, this)
            else -> throw NotImplementedError()
        }
    }

    override fun encodeBoolean(value: Boolean) {
        tag = ByteTag(if (value) 1 else 0)
    }

    override fun encodeByte(value: Byte) {
        tag = ByteTag(value)
    }

    override fun encodeChar(value: Char) {
        tag = ShortTag(value.toShort())
    }

    override fun encodeDouble(value: Double) {
        tag = DoubleTag(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        tag = StringTag(enumDescriptor.getElementName(index))
    }

    override fun encodeFloat(value: Float) {
        tag = FloatTag(value)
    }

    override fun encodeInt(value: Int) {
        tag = IntTag(value)
    }

    override fun encodeLong(value: Long) {
        tag = LongTag(value)
    }

    override fun encodeNull() {
        tag = Tag.empty
    }

    override fun encodeShort(value: Short) {
        tag = ShortTag(value)
    }

    override fun encodeString(value: String) {
        tag = StringTag(value)
    }
}
