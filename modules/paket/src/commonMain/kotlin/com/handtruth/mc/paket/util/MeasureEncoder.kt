package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.measureSZInt
import com.handtruth.mc.util.measureSZLong
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

internal class MeasureEncoder(
    private val configuration: PaketFormat.Configuration,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    var size = 0

    override fun encodeBoolean(value: Boolean) {
        size += BOOLEAN_SIZE
    }

    override fun encodeChar(value: Char) {
        size += CHAR_SIZE
    }

    override fun encodeByte(value: Byte) {
        size += BYTE_SIZE
    }

    override fun encodeShort(value: Short) {
        size += CHAR_SIZE
    }

    override fun encodeInt(value: Int) {
        size += measureSZInt(value)
    }

    override fun encodeLong(value: Long) {
        size += measureSZLong(value)
    }

    override fun encodeFloat(value: Float) {
        size += FLOAT_SIZE
    }

    override fun encodeDouble(value: Double) {
        size += DOUBLE_SIZE
    }

    override fun encodeString(value: String) {
        size += measureString(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        size += measureVarInt(index)
    }

    override fun encodeNull() {
        size += 1
    }

    override fun encodeNotNullMark() {
        size += 1
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        size += measureVarInt(collectionSize)
        return super.beginCollection(descriptor, collectionSize)
    }
}
