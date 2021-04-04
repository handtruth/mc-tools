package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.nbt.contains
import com.handtruth.mc.nbt.tags.BooleanArrayTag
import com.handtruth.mc.nbt.tags.BooleanTag
import com.handtruth.mc.nbt.tags.CharTag
import com.handtruth.mc.nbt.tags.ShortArrayTag
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

internal abstract class NBTCompositeEncoder(
    val conf: NBTSerialFormat,
    final override val serializersModule: SerializersModule
) : CompositeEncoder {
    protected abstract fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, value: T)

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        if (BooleanTag in conf.tagsModule) {
            placeTag(descriptor, index, value)
        } else {
            placeTag(descriptor, index, if (value) 1.toByte() else 0.toByte())
        }
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        placeTag(descriptor, index, value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        if (CharTag in conf.tagsModule) {
            placeTag(descriptor, index, value)
        } else {
            placeTag(descriptor, index, value.toShort())
        }
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        placeTag(descriptor, index, value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        placeTag(descriptor, index, value)
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        placeTag(descriptor, index, value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        placeTag(descriptor, index, value)
    }

    private fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray) {
        placeTag(descriptor, index, value)
    }

    private fun encodeShortArrayElement(descriptor: SerialDescriptor, index: Int, value: ShortArray) {
        if (ShortArrayTag in conf.tagsModule) {
            placeTag(descriptor, index, value)
        } else {
            placeTag(descriptor, index, IntArray(value.size) { value[it].toInt() })
        }
    }

    private fun encodeIntArrayElement(descriptor: SerialDescriptor, index: Int, value: IntArray) {
        placeTag(descriptor, index, value)
    }

    private fun encodeLongArrayElement(descriptor: SerialDescriptor, index: Int, value: LongArray) {
        placeTag(descriptor, index, value)
    }

    private fun encodeBooleanArrayElement(descriptor: SerialDescriptor, index: Int, value: BooleanArray) {
        if (BooleanArrayTag in conf.tagsModule) {
            placeTag(descriptor, index, value)
        } else {
            val bytes = ByteArray(value.size) { if (value[it]) 1 else 0 }
            placeTag(descriptor, index, bytes)
        }
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
            is Int -> encodeIntElement(descriptor, index, value)
            is Long -> encodeLongElement(descriptor, index, value)
            is Float -> encodeFloatElement(descriptor, index, value)
            is Double -> encodeDoubleElement(descriptor, index, value)
            is BooleanArray -> encodeBooleanArrayElement(descriptor, index, value)
            is ByteArray -> encodeByteArrayElement(descriptor, index, value)
            is ShortArray -> encodeShortArrayElement(descriptor, index, value)
            is IntArray -> encodeIntArrayElement(descriptor, index, value)
            is LongArray -> encodeLongArrayElement(descriptor, index, value)
            is Char -> encodeCharElement(descriptor, index, value)
            is String -> encodeStringElement(descriptor, index, value)
            else -> {
                val encoder = NBTEncoder(conf, serializersModule)
                serializer.serialize(encoder, value)
                placeTag(descriptor, index, encoder.tag)
            }
        }
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        placeTag(descriptor, index, value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        placeTag(descriptor, index, value)
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        throw UnsupportedOperationException()
    }
}
