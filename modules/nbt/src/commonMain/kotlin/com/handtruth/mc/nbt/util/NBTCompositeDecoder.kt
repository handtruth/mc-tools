package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal abstract class NBTCompositeDecoder(
    conf: NBTSerialFormat,
    serializersModule: SerializersModule
) : NBTIndexedDecoder(conf, serializersModule) {
    protected abstract fun retrieveTag(descriptor: SerialDescriptor, index: Int): Any?

    private fun ensureRetrieveTag(descriptor: SerialDescriptor, index: Int): Any {
        return retrieveTag(descriptor, index) ?: throw NBTException("no such element")
    }

    private inline fun <reified T : Any> retrieveAs(descriptor: SerialDescriptor, index: Int): T {
        val value = ensureRetrieveTag(descriptor, index)
        validate(value is T, T::class, value)
        return value
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        return when (val value = ensureRetrieveTag(descriptor, index)) {
            is Boolean -> value
            is Byte -> value != 0.toByte()
            else -> notValid(Boolean::class, value)
        }
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<Byte>(descriptor, index)

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        return when (val value = ensureRetrieveTag(descriptor, index)) {
            is Short -> value.toInt().toChar()
            is Char -> value
            else -> notValid(Char::class, value)
        }
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<Double>(descriptor, index)

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<Float>(descriptor, index)

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<Int>(descriptor, index)

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<Long>(descriptor, index)

    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? {
        if (this is NBTStructDecoder) {
            println(descriptor.getElementName(index))
        }
        if (retrieveTag(descriptor, index) == null) {
            return null
        }
        return decodeSerializableElement(descriptor, index, deserializer, previousValue)
    }

    override fun <T> decodeNonPrimitiveElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
    ): T {
        val tag = retrieveTag(descriptor, index) ?: NBTException("not found")
        val decoder = NBTDecoder(tag, conf, serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<Short>(descriptor, index)

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int) = retrieveAs<String>(descriptor, index)
}
