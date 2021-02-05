package com.handtruth.mc.nbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.plus

operator fun NBTBinaryCodec.plus(serial: NBTSerialFormat): NBTBinaryFormat =
    object : NBTBinaryFormat, NBTBinaryCodec by this, NBTSerialFormat by serial {
        override val tagsModule = serial.tagsModule
    }

operator fun NBTSerialFormat.plus(codec: NBTBinaryCodec): NBTBinaryFormat =
    object : NBTBinaryFormat, NBTSerialFormat by this, NBTBinaryCodec by codec {
        override val tagsModule = codec.tagsModule
    }

operator fun NBTStringCodec.plus(serial: NBTSerialFormat): NBTStringFormat =
    object : NBTStringFormat, NBTStringCodec by this, NBTSerialFormat by serial {
        override val tagsModule = serial.tagsModule
    }

operator fun NBTSerialFormat.plus(codec: NBTStringCodec): NBTStringFormat =
    object : NBTStringFormat, NBTSerialFormat by this, NBTStringCodec by codec {
        override val tagsModule = codec.tagsModule
    }

operator fun NBTBinaryCodec.plus(codec: NBTStringCodec): NBTCodec =
    object : NBTCodec, NBTBinaryCodec by this, NBTStringCodec by codec {
        override val tagsModule = codec.tagsModule
    }

operator fun NBTStringCodec.plus(codec: NBTBinaryCodec): NBTCodec =
    object : NBTCodec, NBTStringCodec by this, NBTBinaryCodec by codec {
        override val tagsModule = codec.tagsModule
    }

operator fun NBTCodec.plus(serial: NBTSerialFormat): NBT =
    object : NBT, NBTCodec by this, NBTSerialFormat by serial {
        override val tagsModule = serial.tagsModule
    }

operator fun NBTSerialFormat.plus(codec: NBTCodec): NBT =
    object : NBT, NBTSerialFormat by this, NBTCodec by codec {
        override val tagsModule = codec.tagsModule
    }

operator fun NBTBinaryFormat.plus(format: NBTStringFormat): NBT =
    object : NBT, NBTBinaryFormat by this, NBTStringFormat by format {
        override val tagsModule = format.tagsModule
        override val serializersModule = this@plus.serializersModule + format.serializersModule
        override val serialConfig = format.serialConfig
        override fun <T> decodeFromNBT(deserializationStrategy: DeserializationStrategy<T>, value: Any) =
            format.decodeFromNBT(deserializationStrategy, value)

        override fun <T> encodeToNBT(serializationStrategy: SerializationStrategy<T>, value: T) =
            format.encodeToNBT(serializationStrategy, value)
    }

operator fun NBTStringFormat.plus(format: NBTBinaryFormat): NBT =
    object : NBT, NBTStringFormat by this, NBTBinaryFormat by format {
        override val tagsModule = format.tagsModule
        override val serializersModule = this@plus.serializersModule + format.serializersModule
        override val serialConfig = format.serialConfig
        override fun <T> decodeFromNBT(deserializationStrategy: DeserializationStrategy<T>, value: Any) =
            format.decodeFromNBT(deserializationStrategy, value)

        override fun <T> encodeToNBT(serializationStrategy: SerializationStrategy<T>, value: T) =
            format.encodeToNBT(serializationStrategy, value)
    }
