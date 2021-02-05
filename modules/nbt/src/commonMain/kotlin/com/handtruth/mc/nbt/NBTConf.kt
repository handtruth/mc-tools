package com.handtruth.mc.nbt

import kotlinx.datetime.TimeZone

data class NBTBinaryConfig internal constructor(
    val endian: ByteOrders,
    val format: Formats,
    val compressBooleans: Boolean
) {
    enum class ByteOrders {
        Big, Little
    }

    enum class Formats {
        Flat, ZigZag, ZInt
    }

    companion object {
        val Java = NBTBinaryConfig(endian = ByteOrders.Big, format = Formats.Flat, false)
        inline val Default get() = Java
        val BedrockDisk = NBTBinaryConfig(endian = ByteOrders.Little, format = Formats.Flat, false)
        inline val Bedrock get() = BedrockDisk
        val BedrockNet = NBTBinaryConfig(endian = ByteOrders.Little, format = Formats.ZigZag, false)
        val KBT = NBTBinaryConfig(endian = ByteOrders.Big, format = Formats.ZInt, true)
    }
}

data class NBTStringConfig internal constructor(
    val pretty: Boolean,
    val quoteKeys: Boolean,
    val quoteValues: Boolean,
    val timeZone: TimeZone
) {
    companion object {
        val Mojang = NBTStringConfig(
            pretty = false,
            quoteKeys = false,
            quoteValues = true,
            timeZone = TimeZone.currentSystemDefault()
        )
        inline val Default get() = Mojang
        val Handtruth = NBTStringConfig(
            pretty = false,
            quoteKeys = false,
            quoteValues = true,
            timeZone = TimeZone.currentSystemDefault()
        )
    }
}

data class NBTSerialConfig(
    val enumTag: EnumTag = EnumTag.Int
) {
    enum class EnumTag {
        Int, String
    }

    companion object {
        val Default = NBTSerialConfig()
    }
}
