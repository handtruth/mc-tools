package com.handtruth.mc.nbt

data class NBTBinaryConfig internal constructor(
    val endian: ByteOrders,
    val format: Formats
) {
    enum class ByteOrders {
        Big, Little
    }
    enum class Formats {
        Flat, ZigZag, ZInt
    }

    companion object {
        val Java = NBTBinaryConfig(endian = ByteOrders.Big, format = Formats.Flat)
        inline val Default get() = Java
        val BedrockDisk = NBTBinaryConfig(endian = ByteOrders.Little, format = Formats.Flat)
        inline val Bedrock get() = BedrockDisk
        val BedrockNet = NBTBinaryConfig(endian = ByteOrders.Little, format = Formats.ZigZag)
        val KBT = NBTBinaryConfig(endian = ByteOrders.Big, format = Formats.ZInt)
    }
}

data class NBTStringConfig internal constructor(
    val pretty: Boolean,
    val quoteKeys: Boolean,
    val quoteValues: Boolean
) {
    companion object {
        val Default = NBTStringConfig(pretty = false, quoteKeys = false, quoteValues = true)
    }
}

class NBTSerialConfig {
    companion object {
        val Default = NBTSerialConfig()
    }
}
