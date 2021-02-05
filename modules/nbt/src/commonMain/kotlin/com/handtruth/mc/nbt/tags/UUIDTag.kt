package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import com.handtruth.mc.types.UUID
import kotlinx.io.Input
import kotlinx.io.Output

object UUIDTag : Tag<UUID> {
    override val type = UUID::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = when (conf.binaryConfig.endian) {
        NBTBinaryConfig.ByteOrders.Big -> {
            val most = readFlatInt64(input, conf.binaryConfig)
            val least = readFlatInt64(input, conf.binaryConfig)
            UUID(most, least)
        }
        NBTBinaryConfig.ByteOrders.Little -> {
            val least = readFlatInt64(input, conf.binaryConfig)
            val most = readFlatInt64(input, conf.binaryConfig)
            UUID(most, least)
        }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): UUID {
        input.skipSpace()
        check(isUUIDToken(input)) { "expected UUID token" }
        return UUID.parseGUID(input.take(38))
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: UUID) = when (conf.binaryConfig.endian) {
        NBTBinaryConfig.ByteOrders.Big -> {
            writeFlatInt64(output, conf.binaryConfig, value.most)
            writeFlatInt64(output, conf.binaryConfig, value.least)
        }
        NBTBinaryConfig.ByteOrders.Little -> {
            writeFlatInt64(output, conf.binaryConfig, value.least)
            writeFlatInt64(output, conf.binaryConfig, value.most)
        }
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: UUID, level: Int) {
        output.append(value.toGUID())
    }

    override fun toString() = "TAG_UUID"
}
