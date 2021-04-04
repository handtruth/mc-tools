package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.PaketDecoder
import com.handtruth.mc.paket.util.PaketEncoder
import io.ktor.utils.io.core.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

public class PaketFormat(
    public val configuration: Configuration = Configuration.Default,
    public override val serializersModule: SerializersModule = EmptySerializersModule
) : BinaryFormat {
    public class Configuration {
        public companion object {
            public val Default: Configuration = Configuration()
        }
    }

    public fun <T> decodeFromInput(deserializer: DeserializationStrategy<T>, input: Input): T {
        val decoder = PaketDecoder(configuration, serializersModule)
        decoder.input = input
        return deserializer.deserialize(decoder)
    }

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        return ByteReadPacket(bytes).use { input ->
            decodeFromInput(deserializer, input)
        }
    }

    public fun <T> encodeToOutput(serializer: SerializationStrategy<T>, output: Output, value: T) {
        val encoder = PaketEncoder(configuration, serializersModule)
        encoder.output = output
        return serializer.serialize(encoder, value)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        return buildPacket {
            encodeToOutput(serializer, this, value)
        }.use {
            it.readBytes()
        }
    }
}
