package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.plus

operator fun NBTBinaryCodec.plus(serial: NBTSerialFormat): NBTBinaryFormat =
    object : NBTBinaryFormat, NBTBinaryCodec by this, NBTSerialFormat by serial {}

operator fun NBTSerialFormat.plus(codec: NBTBinaryCodec) = codec + this

operator fun NBTStringCodec.plus(serial: NBTSerialFormat): NBTStringFormat =
    object : NBTStringFormat, NBTStringCodec by this, NBTSerialFormat by serial {}

operator fun NBTSerialFormat.plus(codec: NBTStringCodec) = codec + this

operator fun NBTBinaryCodec.plus(codec: NBTStringCodec): NBTCodec =
    object : NBTCodec, NBTBinaryCodec by this, NBTStringCodec by codec {}

operator fun NBTStringCodec.plus(codec: NBTBinaryCodec): NBTCodec = codec + this

operator fun NBTCodec.plus(serial: NBTSerialFormat): NBT =
    object : NBT, NBTCodec by this, NBTSerialFormat by serial {}

operator fun NBTSerialFormat.plus(codec: NBTCodec): NBT = codec + this

operator fun NBTBinaryFormat.plus(format: NBTStringFormat): NBT =
    object : NBT, NBTBinaryFormat by this, NBTStringFormat by format {
        override val context = this@plus.context + format.context
        override val serialConfig = format.serialConfig
        override fun <T> fromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>) =
            format.fromNBT(deserializationStrategy, tag)

        override fun <T> toNBT(serializationStrategy: SerializationStrategy<T>, value: T) =
            format.toNBT(serializationStrategy, value)
    }

operator fun NBTStringFormat.plus(format: NBTBinaryFormat): NBT =
    object : NBT, NBTStringFormat by this, NBTBinaryFormat by format {
        override val context = this@plus.context + format.context
        override val serialConfig = format.serialConfig
        override fun <T> fromNBT(deserializationStrategy: DeserializationStrategy<T>, tag: Tag<*>) =
            format.fromNBT(deserializationStrategy, tag)

        override fun <T> toNBT(serializationStrategy: SerializationStrategy<T>, value: T) =
            format.toNBT(serializationStrategy, value)
    }
