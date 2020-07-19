package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTListType
import com.handtruth.mc.nbt.tags.*
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerialModule

internal class NBTEncoder(override val context: SerialModule) : Encoder {
    var tag: Tag<*> = Tag.empty
        internal set

    override fun beginStructure(
        descriptor: SerialDescriptor,
        vararg typeSerializers: KSerializer<*>
    ): CompositeEncoder {
        return NBTStructEncoder(context, this)
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
        vararg typeSerializers: KSerializer<*>
    ): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val tagId = (descriptor.annotations.find { it is NBTListType } as? NBTListType)?.id ?: EndTag.id
                NBTListEncoder(context, this, tagId.resolver)
            }
            StructureKind.MAP -> NBTMapEncoder(context, this)
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

    override fun encodeUnit() {
        tag = Tag.empty
    }
}
