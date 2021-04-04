package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.Codecs
import com.handtruth.mc.paket.get
import io.ktor.utils.io.core.*
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

internal class PaketEncoder(
    private val codecs: Codecs,
    private val output: Output,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    private var useCodec: Codec<*>? = null

    private inline fun <reified T : Any> encode(value: T) {
        useCodec?.let {
            @Suppress("UNCHECKED_CAST")
            (it as Codec<T>).write(output, value)
            useCodec = null
        } ?: codecs.get<T>().write(output, value)
    }

    override fun encodeBoolean(value: Boolean) = encode(value)

    override fun encodeChar(value: Char) = encode(value)

    override fun encodeByte(value: Byte) = encode(value)

    override fun encodeShort(value: Short) = encode(value)

    override fun encodeInt(value: Int) = encode(value)

    override fun encodeLong(value: Long) = encode(value)

    override fun encodeFloat(value: Float) = encode(value)

    override fun encodeDouble(value: Double) = encode(value)

    override fun encodeString(value: String) = encode(value)

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = encode(index)

    override fun encodeNull() {
        output.writeByte(0)
    }

    override fun encodeNotNullMark() {
        output.writeByte(1)
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        encode(collectionSize.toUInt())
        return super.beginCollection(descriptor, collectionSize)
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        @Suppress("UNCHECKED_CAST")
        (codecs.getOrNull(serializer.descriptor.serialName) as Codec<T>?)?.write(output, value)
            ?: serializer.serialize(this, value)
    }
}
