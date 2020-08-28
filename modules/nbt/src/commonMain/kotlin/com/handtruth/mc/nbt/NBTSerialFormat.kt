package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.util.NBTDecoder
import com.handtruth.mc.nbt.util.NBTEncoder
import com.handtruth.mc.nbt.util.Reader
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

interface NBTSerialFormat : SerialFormat {
    val serialConfig: NBTSerialConfig
    fun <T> fromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>): T
    fun <T> toNBT(serializationStrategy: SerializationStrategy<T>, value: T): Tag<*>
}

interface NBTBinaryFormat : BinaryFormat, NBTBinaryCodec, NBTSerialFormat {
    fun <T> load(deserializer: DeserializationStrategy<T>, input: Input): T {
        return fromNBT(deserializer, read(input))
    }

    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        return fromNBT(deserializer, read(bytes))
    }

    fun <T> dump(serializer: SerializationStrategy<T>, output: Output, value: T) {
        write(output, toNBT(serializer, value) as CompoundTag)
    }

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray {
        return write(toNBT(serializer, value) as CompoundTag)
    }
}

interface NBTStringFormat : StringFormat, NBTStringCodec, NBTSerialFormat {
    fun <T> parse(deserializer: DeserializationStrategy<T>, reader: Reader): T {
        return fromNBT(deserializer, read(reader))
    }

    override fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T {
        return fromNBT(deserializer, read(string))
    }

    fun <T> stringify(serializer: SerializationStrategy<T>, appendable: Appendable, value: T) {
        write(appendable, toNBT(serializer, value))
    }

    override fun <T> stringify(serializer: SerializationStrategy<T>, value: T): String {
        return write(toNBT(serializer, value))
    }
}

private class NBTSerialFormatImpl(
    override val serialConfig: NBTSerialConfig,
    override val context: SerialModule
) : NBTSerialFormat {
    override fun <T> fromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>): T {
        val decoder = NBTDecoder(tag, context, UpdateMode.OVERWRITE)
        return deserializationStrategy.deserialize(decoder)
    }

    override fun <T> toNBT(serializationStrategy: SerializationStrategy<T>, value: T): Tag<*> {
        val encoder = NBTEncoder(context)
        serializationStrategy.serialize(encoder, value)
        return encoder.tag
    }
}

@Suppress("FunctionName")
fun NBTSerialFormat(
    serialConfig: NBTSerialConfig = NBTSerialConfig.Default,
    context: SerialModule = EmptyModule
): NBTSerialFormat = NBTSerialFormatImpl(serialConfig, context)
