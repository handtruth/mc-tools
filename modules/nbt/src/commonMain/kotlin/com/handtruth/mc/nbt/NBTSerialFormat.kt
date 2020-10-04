package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.util.NBTDecoder
import com.handtruth.mc.nbt.util.NBTEncoder
import com.handtruth.mc.nbt.util.Reader
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface NBTSerialFormat : SerialFormat {
    val serialConfig: NBTSerialConfig
    fun <T> decodeFromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>): T
    fun <T> encodeToNBT(serializationStrategy: SerializationStrategy<T>, value: T): Tag<*>
}

interface NBTBinaryFormat : BinaryFormat, NBTBinaryCodec, NBTSerialFormat {
    fun <T> decodeFromInput(deserializer: DeserializationStrategy<T>, input: Input): T {
        return decodeFromNBT(deserializer, read(input))
    }

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        return decodeFromNBT(deserializer, read(bytes))
    }

    fun <T> decodeFromByteArray(serializer: SerializationStrategy<T>, output: Output, value: T) {
        write(output, encodeToNBT(serializer, value) as CompoundTag)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        return write(encodeToNBT(serializer, value) as CompoundTag)
    }
}

interface NBTStringFormat : StringFormat, NBTStringCodec, NBTSerialFormat {
    fun <T> decodeFromReader(deserializer: DeserializationStrategy<T>, reader: Reader): T {
        return decodeFromNBT(deserializer, read(reader))
    }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return decodeFromNBT(deserializer, read(string))
    }

    fun <T> encodeToString(serializer: SerializationStrategy<T>, appendable: Appendable, value: T) {
        write(appendable, encodeToNBT(serializer, value))
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return write(encodeToNBT(serializer, value))
    }
}

private class NBTSerialFormatImpl(
    override val serialConfig: NBTSerialConfig,
    override val serializersModule: SerializersModule
) : NBTSerialFormat {
    override fun <T> decodeFromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>): T {
        val decoder = NBTDecoder(tag, serializersModule)
        return deserializationStrategy.deserialize(decoder)
    }

    override fun <T> encodeToNBT(serializationStrategy: SerializationStrategy<T>, value: T): Tag<*> {
        val encoder = NBTEncoder(serializersModule)
        serializationStrategy.serialize(encoder, value)
        return encoder.tag
    }
}

@Suppress("FunctionName")
fun NBTSerialFormat(
    serialConfig: NBTSerialConfig = NBTSerialConfig.Default,
    serializersModule: SerializersModule = EmptySerializersModule
): NBTSerialFormat = NBTSerialFormatImpl(serialConfig, serializersModule)
