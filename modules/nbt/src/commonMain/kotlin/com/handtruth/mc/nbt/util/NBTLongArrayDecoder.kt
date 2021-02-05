package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTLongArrayDecoder(
    private val value: LongArray,
    conf: NBTSerialFormat,
    serializersModule: SerializersModule
) : NBTIndexedDecoder(conf, serializersModule) {

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return value.size
    }

    override fun <T> decodeNonPrimitiveElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
    ): T {
        throw NBTException("failed to treat LongArray element as ${deserializer.descriptor.kind} type")
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        return value[index] != 0L
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
        return value[index].toByte()
    }

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        return value[index].toChar()
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        return value[index].toDouble()
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        return value[index].toFloat()
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        return value[index].toInt()
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        return value[index]
    }

    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? {
        return if (index >= decodeCollectionSize(descriptor)) {
            null
        } else {
            decodeSerializableElement(descriptor, index, deserializer, previousValue)
        }
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short {
        return value[index].toShort()
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        throw NBTException("failed to cast long to string")
    }
}
