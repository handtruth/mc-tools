package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.util.NBTDecoder
import com.handtruth.mc.nbt.util.NBTEncoder
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

interface NBTSerialFormat : SerialFormat {
    val serialConfig: NBTSerialConfig
    fun <T> fromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>): T
    fun <T> toNBT(serializationStrategy: SerializationStrategy<T>, value: T): Tag<*>
}

interface NBTBinaryFormat : BinaryFormat, NBTBinaryCodec, NBTSerialFormat {
    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        return fromNBT(deserializer, read(bytes))
    }

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray {
        return write(toNBT(serializer, value) as CompoundTag)
    }
}

interface NBTStringFormat : StringFormat, NBTStringCodec, NBTSerialFormat {
    override fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T {
        return fromNBT(deserializer, read(string))
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

fun NBTSerialFormat(
    serialConfig: NBTSerialConfig = NBTSerialConfig.Default,
    context: SerialModule = EmptyModule
): NBTSerialFormat = NBTSerialFormatImpl(serialConfig, context)
