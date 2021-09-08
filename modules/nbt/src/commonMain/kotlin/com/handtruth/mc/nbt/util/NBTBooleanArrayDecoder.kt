package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTBooleanArrayDecoder(
    private val value: BooleanArray,
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
        throw NBTException("failed to treat IntArray element as ${deserializer.descriptor.kind} type")
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        return value[index]
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
        return if (value[index]) 1 else 0
    }

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        return decodeByteElement(descriptor, index).toInt().toChar()
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        return decodeByteElement(descriptor, index).toDouble()
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        return decodeByteElement(descriptor, index).toFloat()
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        return decodeByteElement(descriptor, index).toInt()
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        return decodeByteElement(descriptor, index).toLong()
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
        return decodeByteElement(descriptor, index).toShort()
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        throw NBTException("failed to cast int to string")
    }
}
