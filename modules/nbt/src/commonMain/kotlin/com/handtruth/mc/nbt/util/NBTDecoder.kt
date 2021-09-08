package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.types.Dynamic
import kotlinx.serialization.ExperimentalSerializationApi
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

    override fun decodeByte() = decodeAs<Byte>()

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

    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        throw UnsupportedOperationException()
    }

    override fun decodeInt() = decodeAs<Int>()

    override fun decodeLong() = decodeAs<Long>()

    override fun decodeNotNullMark() = true

    override fun decodeNull() = null

    override fun decodeShort() = decodeAs<Short>()

    override fun decodeString() = decodeAs<String>()
}
