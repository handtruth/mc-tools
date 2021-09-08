package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialConfig
import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.nbt.contains
import com.handtruth.mc.nbt.tags.BooleanTag
import com.handtruth.mc.nbt.tags.CharTag
import com.handtruth.mc.nbt.tags.EndTag
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

internal class NBTEncoder(
    private val conf: NBTSerialFormat,
    override val serializersModule: SerializersModule
) : Encoder {
    var tag: Any = EndTag

    override fun beginStructure(
        descriptor: SerialDescriptor
    ): CompositeEncoder {
        return NBTStructEncoder(conf, serializersModule, this)
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> NBTListEncoder(conf, serializersModule, this)
            StructureKind.MAP -> NBTMapEncoder(conf, serializersModule, this)
            else -> throw NotImplementedError()
        }
    }

    override fun encodeBoolean(value: Boolean) {
        tag = if (BooleanTag in conf.tagsModule) {
            value
        } else {
            if (value) 1 else 0
        }
    }

    override fun encodeByte(value: Byte) {
        tag = value
    }

    override fun encodeChar(value: Char) {
        tag = if (CharTag in conf.tagsModule) {
            value
        } else {
            value.code.toShort()
        }
    }

    override fun encodeDouble(value: Double) {
        tag = value
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        tag = when (conf.serialConfig.enumTag) {
            NBTSerialConfig.EnumTag.Int -> index
            NBTSerialConfig.EnumTag.String -> enumDescriptor.getElementName(index)
        }
    }

    override fun encodeFloat(value: Float) {
        tag = value
    }

    override fun encodeInt(value: Int) {
        tag = value
    }

    override fun encodeLong(value: Long) {
        tag = value
    }

    override fun encodeNull() {
        throw NBTException("there are no null tag")
    }

    override fun encodeShort(value: Short) {
        tag = value
    }

    override fun encodeString(value: String) {
        tag = value
    }

    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
        throw UnsupportedOperationException()
    }
}
