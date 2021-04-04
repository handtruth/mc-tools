package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.PaketDecoder
import com.handtruth.mc.paket.util.PaketEncoder
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class PaketFormat(
    val codecs: Codecs = Codecs.Default,
    override val serializersModule: SerializersModule = EmptySerializersModule
) : BinaryFormat {
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val decoder = PaketDecoder(codecs, ByteArrayInput(bytes), serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val output = ByteArrayOutput()
        val encoder = PaketEncoder(codecs, output, serializersModule)
        serializer.serialize(encoder, value)
        return output.toByteArray()
    }
}
