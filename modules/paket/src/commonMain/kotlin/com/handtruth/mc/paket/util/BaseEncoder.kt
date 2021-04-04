package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.PaketFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

internal abstract class BaseEncoder(
    protected val configuration: PaketFormat.Configuration,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    private var unsigned: Boolean = false

    private companion object {
        private val unsignedDescriptors = listOf(
            UByte.serializer(),
            UShort.serializer(),
            UInt.serializer(),
            ULong.serializer()
        ).map { it.descriptor }
    }

    abstract fun encodeSize(size: Int)

    abstract fun encodeSByte(value: Byte)
    abstract fun encodeUByte(value: UByte)
    abstract fun encodeSShort(value: Short)
    abstract fun encodeUShort(value: UShort)
    abstract fun encodeSInt(value: Int)
    abstract fun encodeUInt(value: UInt)
    abstract fun encodeSLong(value: Long)
    abstract fun encodeULong(value: ULong)

    final override fun encodeByte(value: Byte) {
        if (unsigned) {
            encodeUByte(value.toUByte())
            unsigned = false
        } else {
            encodeSByte(value)
        }
    }

    final override fun encodeShort(value: Short) {
        if (unsigned) {
            encodeUShort(value.toUShort())
            unsigned = false
        } else {
            encodeSShort(value)
        }
    }

    final override fun encodeInt(value: Int) {
        if (unsigned) {
            encodeUInt(value.toUInt())
            unsigned = false
        } else {
            encodeSInt(value)
        }
    }

    final override fun encodeLong(value: Long) {
        if (unsigned) {
            encodeULong(value.toULong())
            unsigned = false
        } else {
            encodeSLong(value)
        }
    }

    final override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        encodeSize(collectionSize)
        return super.beginCollection(descriptor, collectionSize)
    }

    final override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
        if (inlineDescriptor in unsignedDescriptors) {
            unsigned = true
        }
        return this
    }
}
