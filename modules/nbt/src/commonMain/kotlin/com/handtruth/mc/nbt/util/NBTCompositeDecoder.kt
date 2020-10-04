package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal abstract class NBTCompositeDecoder(
    serializersModule: SerializersModule
) : NBTIndexedDecoder(serializersModule) {
    protected abstract fun retrieveTag(descriptor: SerialDescriptor, index: Int): Tag<*>

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        val tag = retrieveTag(descriptor, index)
        validate(tag is ByteTag, Boolean::class, tag.id)
        return tag.byte.toInt() != 0
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
        val tag = retrieveTag(descriptor, index)
        validate(tag is ByteTag, Byte::class, tag.id)
        return tag.byte
    }

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        val tag = retrieveTag(descriptor, index)
        validate(tag is ShortTag, Char::class, tag.id)
        return tag.short.toChar()
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        val tag = retrieveTag(descriptor, index)
        validate(tag is DoubleTag, Double::class, tag.id)
        return tag.number
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        val tag = retrieveTag(descriptor, index)
        validate(tag is FloatTag, Float::class, tag.id)
        return tag.number
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        val tag = retrieveTag(descriptor, index)
        validate(tag is IntTag, Int::class, tag.id)
        return tag.integer
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        val tag = retrieveTag(descriptor, index)
        validate(tag is LongTag, Long::class, tag.id)
        return tag.long
    }

    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? {
        if (this is NBTStructDecoder) {
            println(descriptor.getElementName(index))
        }
        if (retrieveTag(descriptor, index) is EndTag) {
            return null
        }
        return decodeSerializableElement(descriptor, index, deserializer, previousValue)
    }

    override fun <T> decodeNonPrimitiveElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
    ): T {
        val tag = retrieveTag(descriptor, index)
        val decoder = NBTDecoder(tag, serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short {
        val tag = retrieveTag(descriptor, index)
        validate(tag is ShortTag, Short::class, tag.id)
        return tag.short
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        val tag = retrieveTag(descriptor, index)
        validate(tag is StringTag, String::class, tag.id)
        return tag.value
    }
}
