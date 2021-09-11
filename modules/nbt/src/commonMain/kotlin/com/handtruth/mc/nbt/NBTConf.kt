package com.handtruth.mc.nbt

import com.handtruth.mc.util.Base64Variants
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
        Flat, LEB128, ZInt
    }

    companion object {
        val Java = NBTBinaryConfig(
            endian = ByteOrders.Big,
            format = Formats.Flat,
            compressBooleans = false
        )
        inline val Default get() = Java
        val BedrockDisk = NBTBinaryConfig(
            endian = ByteOrders.Little,
            format = Formats.Flat,
            compressBooleans = false
        )
        inline val Bedrock get() = BedrockDisk
        val BedrockNet = NBTBinaryConfig(
            endian = ByteOrders.Little,
            format = Formats.LEB128,
            compressBooleans = false
        )
        val KBT = NBTBinaryConfig(
            endian = ByteOrders.Big,
            format = Formats.ZInt,
            compressBooleans = true
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
    val base64LineLength: Int
) {
    companion object {
        val Mojang = NBTStringConfig(
            pretty = false,
            identString = "    ",
            quoteKeys = false,
            quoteValues = true,
            timeZone = TimeZone.currentSystemDefault(),
            base64Variant = Base64Variants.RFC4648,
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
