package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.util.NBTDecoder
import com.handtruth.mc.nbt.util.NBTEncoder
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

class NBTConfiguration {
    companion object {
        val Default = NBTConfiguration()
    }
}

class NBT(
    val config: NBTConfiguration = NBTConfiguration.Default,
    override val context: SerialModule = EmptyModule
) : BinaryFormat {

    companion object {
        val Default = NBT()
    }

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val output = ByteArrayOutput()
        serialize(serializer, output, value)
        return output.toByteArray()
    }

    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val input = ByteArrayInput(bytes)
        return deserialize(deserializer, input)
    }

    fun read(input: Input) = CompoundTag.read(input)

    fun write(output: Output, root: CompoundTag) = root.write(output)

    fun <T> serialize(serializer: SerializationStrategy<T>, value: T): Tag<*> {
        val encoder = NBTEncoder(context)
        serializer.serialize(encoder, value)
        return encoder.tag
    }

    fun <T> deserialize(deserializer: DeserializationStrategy<T>, tag: Tag<*>): T {
        val decoder = NBTDecoder(tag, context, UpdateMode.OVERWRITE)
        return deserializer.deserialize(decoder)
    }

    fun <T> deserialize(deserializer: DeserializationStrategy<T>, input: Input): T {
        return deserialize(deserializer, read(input))
    }

    fun <T> serialize(serializer: SerializationStrategy<T>, output: Output, value: T) {
        write(output, serialize(serializer, value) as CompoundTag)
    }
}
