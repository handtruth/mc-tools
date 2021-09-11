package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.nbt.contains
import com.handtruth.mc.types.Dynamic
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

internal class NBTDecoder(
    val value: Any,
    val conf: NBTSerialFormat,
    override val serializersModule: SerializersModule
) : Decoder {
    private var isUnsigned = false

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                when (value) {
                    is List<*> -> NBTListDecoder(value, conf, serializersModule)
                    is BooleanArray -> NBTBooleanArrayDecoder(value, conf, serializersModule)
                    is ByteArray -> NBTByteArrayDecoder(value, conf, serializersModule)
                    is ShortArray -> NBTShortArrayDecoder(value, conf, serializersModule)
                    is IntArray -> NBTIntArrayDecoder(value, conf, serializersModule)
                    is LongArray -> NBTLongArrayDecoder(value, conf, serializersModule)
                    else -> throw NBTException("tag $value can't be treated as list")
                }
            }
            StructureKind.MAP -> {
                validate(value is Dynamic, Dynamic::class, value)
                NBTMapDecoder(value, conf, serializersModule)
            }
            StructureKind.CLASS -> {
                validate(value is Dynamic) { "structures may only be associated with CompountTag, got ${value::class}" }
                NBTStructDecoder(value, conf, serializersModule)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    private inline fun <reified T : Any> decodeAs(): T {
        validate(value is T, T::class, value)
        return value
    }

    override fun decodeBoolean(): Boolean {
        return when (value) {
            is Boolean -> value
            is Byte -> value != 0
            else -> notValid(Boolean::class, value)
        }
    }

    override fun decodeByte() = if (isUnsigned) decodeAs<UByte>().toByte() else decodeAs<Byte>()

    override fun decodeChar(): Char {
        return when (value) {
            is Short -> value.toInt().toChar()
            is Char -> value
            else -> notValid(Char::class, value)
        }
    }

    override fun decodeDouble() = decodeAs<Double>()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return when (value) {
            is Int -> value
            is String -> enumDescriptor.getElementIndex(value)
            else -> notValid(Int::class, value)
        }
    }

    override fun decodeFloat() = decodeAs<Float>()

    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        val tag = unsignedSerialDescriptors[inlineDescriptor]
        if (tag != null && tag in conf.tagsModule) {
            isUnsigned = true
        }
        return this
    }

    override fun decodeInt() = if (isUnsigned) decodeAs<UInt>().toInt() else decodeAs<Int>()

    override fun decodeLong() = if (isUnsigned) decodeAs<ULong>().toLong() else decodeAs<Long>()

    override fun decodeNotNullMark() = true

    override fun decodeNull() = null

    override fun decodeShort() = if (isUnsigned) decodeAs<UShort>().toShort() else decodeAs<Short>()

    override fun decodeString() = decodeAs<String>()
}
