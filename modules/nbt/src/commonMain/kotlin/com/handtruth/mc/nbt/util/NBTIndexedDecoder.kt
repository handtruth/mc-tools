package com.handtruth.mc.nbt.util

import kotlinx.serialization.*
import kotlinx.serialization.modules.SerialModule

internal abstract class NBTIndexedDecoder(
    override val context: SerialModule,
    override val updateMode: UpdateMode
) : CompositeDecoder {
    final override fun endStructure(descriptor: SerialDescriptor) {}

    protected abstract val size: Int

    final override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return size
    }

    protected var index = 0

    final override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (index >= size)
            return CompositeDecoder.READ_DONE
        return index++
    }

    final override fun <T : Any> updateNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        old: T?
    ): T? {
        return decodeNullableSerializableElement(descriptor, index, deserializer)
    }

    final override fun <T> updateSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        old: T
    ): T {
        return decodeSerializableElement(descriptor, index, deserializer)
    }

    abstract fun <T> decodeNonPrimitiveElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>): T

    final override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
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
}
