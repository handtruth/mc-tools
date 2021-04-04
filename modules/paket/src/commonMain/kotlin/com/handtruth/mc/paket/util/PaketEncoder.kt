package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.writeSZInt
import com.handtruth.mc.util.writeSZLong
import com.handtruth.mc.util.writeUZInt
import com.handtruth.mc.util.writeUZLong
import io.ktor.utils.io.core.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class PaketEncoder(
    configuration: PaketFormat.Configuration,
    serializersModule: SerializersModule
) : BaseEncoder(configuration, serializersModule) {
    lateinit var output: Output

    override fun encodeSize(size: Int) = output.writeVarInt(size)

    override fun encodeBoolean(value: Boolean) = output.writeBoolean(value)

    override fun encodeChar(value: Char) = output.writeChar(value)

    override fun encodeSByte(value: Byte) = output.writeByte(value)
    override fun encodeUByte(value: UByte) = output.writeUByte(value)

    override fun encodeSShort(value: Short) = output.writeShort(value)
    override fun encodeUShort(value: UShort) = output.writeUShort(value)

    override fun encodeSInt(value: Int) = output.writeSZInt(value)
    override fun encodeUInt(value: UInt) = output.writeUZInt(value)

    override fun encodeSLong(value: Long) = output.writeSZLong(value)
    override fun encodeULong(value: ULong) = output.writeUZLong(value)

    override fun encodeFloat(value: Float) = output.writeFloat(value)

    override fun encodeDouble(value: Double) = output.writeDouble(value)

    override fun encodeString(value: String) = output.writeString(value)

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = output.writeVarInt(index)

    override fun encodeNull() {
        output.writeByte(0)
    }

    override fun encodeNotNullMark() {
        output.writeByte(1)
    }
}
