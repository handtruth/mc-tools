package com.handtruth.mc.nbt

import kotlinx.datetime.TimeZone

data class NBTBinaryConfig internal constructor(
    val endian: ByteOrders,
    val format: Formats,
    val compressBooleans: Boolean,
    val moveBytes: Boolean
) {
    enum class ByteOrders {
        Big, Little
    }

    enum class Formats {
        Flat, ZigZag, ZInt
    }

    companion object {
        val Java = NBTBinaryConfig(
            endian = ByteOrders.Big,
            format = Formats.Flat,
            compressBooleans = false,
            moveBytes = false
        )
        inline val Default get() = Java
        val BedrockDisk = NBTBinaryConfig(
            endian = ByteOrders.Little,
            format = Formats.Flat,
            compressBooleans = false,
            moveBytes = false
        )
        inline val Bedrock get() = BedrockDisk
        val BedrockNet = NBTBinaryConfig(
            endian = ByteOrders.Little,
            format = Formats.ZigZag,
            compressBooleans = false,
            moveBytes = false
        )
        val KBT = NBTBinaryConfig(
            endian = ByteOrders.Big,
            format = Formats.ZInt,
            compressBooleans = true,
            moveBytes = false
        )
    }
}

data class NBTStringConfig internal constructor(
    val pretty: Boolean,
    val identString: String,
    val quoteKeys: Boolean,
    val quoteValues: Boolean,
    val timeZone: TimeZone,
    val base64Variant: Base64Variants,
    val moveBytes: Boolean,
    val base64LineLength: Int
) {
    enum class Base64Variants {
        RFC4648, RFC4648Url, RFC3501
    }

    companion object {
        val Mojang = NBTStringConfig(
            pretty = false,
            identString = "    ",
            quoteKeys = false,
            quoteValues = true,
            timeZone = TimeZone.currentSystemDefault(),
            base64Variant = Base64Variants.RFC4648,
            moveBytes = false,
            base64LineLength = 64,
        )
        inline val Default get() = Mojang
        val Handtruth = NBTStringConfig(
            pretty = false,
            identString = "\t",
            quoteKeys = false,
            quoteValues = true,
            timeZone = TimeZone.currentSystemDefault(),
            base64Variant = Base64Variants.RFC4648,
            moveBytes = false,
            base64LineLength = 64,
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
