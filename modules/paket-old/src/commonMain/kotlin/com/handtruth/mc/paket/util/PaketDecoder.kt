package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

internal class PaketDecoder(
    private val codecs: Codecs,
    private val input: Input,
    override val serializersModule: SerializersModule
) : AbstractDecoder() {

    private var index = 0

    private var useCodec: Codec<*>? = null

    private inline fun <reified T : Any> decode(): T {
        return useCodec?.let {
            val result = it.read(input) as T
            useCodec = null
            result
        } ?: codecs.get<T>().read(input)
    }

    override fun decodeSequentially() = true

    override fun decodeBoolean(): Boolean = decode()

    override fun decodeChar(): Char = decode()

    override fun decodeByte(): Byte = decode()

    override fun decodeShort(): Short = decode()

    override fun decodeInt(): Int = decode()

    override fun decodeLong(): Long = decode()

    override fun decodeFloat(): Float = decode()

    override fun decodeDouble(): Double = decode()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = decode()

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return decode<UInt>().toInt()
    }

    override fun decodeString(): String = decode()

    override fun decodeNotNullMark(): Boolean = input.readByte() != 0.toByte()

    override fun decodeNull() = null

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val i = index++
        val annotation = descriptor.getElementAnnotations(i).find { it is WithCodec }
        if (annotation != null) {
            annotation as WithCodec
            @Suppress("UNCHECKED_CAST")
            useCodec = codecs.codec(annotation.codec as KClass<Codec<Any>>)
        }
        return i
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        index = 0
        return this
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        @Suppress("UNCHECKED_CAST")
        return codecs.getOrNull(deserializer.descriptor.serialName)?.read(input) as T?
            ?: deserializer.deserialize(this)
    }
}
