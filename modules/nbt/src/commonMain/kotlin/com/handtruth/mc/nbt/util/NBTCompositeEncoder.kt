package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.*
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

internal abstract class NBTCompositeEncoder(
    final override val serializersModule: SerializersModule
) : CompositeEncoder {
    protected abstract fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, tag: Tag<T>)

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        placeTag(descriptor, index, ByteTag(if (value) 1 else 0))
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        placeTag(descriptor, index, ByteTag(value))
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        placeTag(descriptor, index, ShortTag(value.toShort()))
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        placeTag(descriptor, index, DoubleTag(value))
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        placeTag(descriptor, index, FloatTag(value))
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        placeTag(descriptor, index, IntTag(value))
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        placeTag(descriptor, index, LongTag(value))
    }

    fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray) {
        placeTag(descriptor, index, ByteArrayTag(value))
    }

    fun encodeIntArrayElement(descriptor: SerialDescriptor, index: Int, value: IntArray) {
        placeTag(descriptor, index, IntArrayTag(value))
    }

    fun encodeLongArrayElement(descriptor: SerialDescriptor, index: Int, value: LongArray) {
        placeTag(descriptor, index, LongArrayTag(value))
    }

    fun encodeBooleanArrayElement(descriptor: SerialDescriptor, index: Int, value: BooleanArray) {
        val bytes = ByteArray(value.size) { if (value[it]) 1 else 0 }
        placeTag(descriptor, index, ByteArrayTag(bytes))
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        if (value != null) {
            encodeSerializableElement(descriptor, index, serializer, value)
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        when (value) {
            is Boolean -> encodeBooleanElement(descriptor, index, value)
            is Byte -> encodeByteElement(descriptor, index, value)
            is Short -> encodeShortElement(descriptor, index, value)
            is Char -> encodeCharElement(descriptor, index, value)
            is Long -> encodeLongElement(descriptor, index, value)
            is Float -> encodeFloatElement(descriptor, index, value)
            is Double -> encodeDoubleElement(descriptor, index, value)
            is BooleanArray -> encodeBooleanArrayElement(descriptor, index, value)
            is ByteArray -> encodeByteArrayElement(descriptor, index, value)
            is String -> encodeStringElement(descriptor, index, value)
            is IntArray -> encodeIntArrayElement(descriptor, index, value)
            is LongArray -> encodeLongArrayElement(descriptor, index, value)
            else -> {
                val encoder = NBTEncoder(serializersModule)
                serializer.serialize(encoder, value)
                placeTag(descriptor, index, encoder.tag)
            }
        }
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        placeTag(descriptor, index, ShortTag(value))
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        placeTag(descriptor, index, StringTag(value))
    }
}
