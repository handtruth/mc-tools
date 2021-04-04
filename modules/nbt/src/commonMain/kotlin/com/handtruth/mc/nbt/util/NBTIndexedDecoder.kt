package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

internal abstract class NBTIndexedDecoder(
    val conf: NBTSerialFormat,
    override val serializersModule: SerializersModule
) : CompositeDecoder {
    final override fun endStructure(descriptor: SerialDescriptor) {}

    protected var index = 0

    final override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (index >= decodeCollectionSize(descriptor)) {
            return CompositeDecoder.DECODE_DONE
        }
        return index++
    }

    abstract fun <T> decodeNonPrimitiveElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
    ): T

    final override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        @Suppress("UNCHECKED_CAST")
        return when (deserializer.descriptor.kind) {
            PrimitiveKind.BOOLEAN -> decodeBooleanElement(descriptor, index) as T
            PrimitiveKind.BYTE -> decodeByteElement(descriptor, index) as T
            PrimitiveKind.SHORT -> decodeShortElement(descriptor, index) as T
            PrimitiveKind.INT -> decodeIntElement(descriptor, index) as T
            PrimitiveKind.LONG -> decodeLongElement(descriptor, index) as T
            PrimitiveKind.FLOAT -> decodeFloatElement(descriptor, index) as T
            PrimitiveKind.DOUBLE -> decodeDoubleElement(descriptor, index) as T
            PrimitiveKind.CHAR -> decodeCharElement(descriptor, index) as T
            PrimitiveKind.STRING -> decodeStringElement(descriptor, index) as T
            else -> decodeNonPrimitiveElement(descriptor, index, deserializer)
        }
    }

    final override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder {
        throw UnsupportedOperationException()
    }
}
