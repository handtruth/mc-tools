package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.util.construct
import com.handtruth.mc.paket.util.readVarInt
import com.handtruth.mc.paket.util.writeVarInt
import io.ktor.utils.io.core.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlin.reflect.KProperty

public class BinaryFormatCodec<T>(public val serializer: KSerializer<T>, public val format: BinaryFormat) : Codec<T> {
    override fun measure(value: T): Int = throw UnsupportedOperationException()

    override fun read(input: Input): T {
        val size = input.readVarInt()
        val bytes = input.readBytes(size)
        return format.decodeFromByteArray(serializer, bytes)
    }

    override fun write(output: Output, value: T) {
        val bytes = format.encodeToByteArray(serializer, value)
        output.writeVarInt(bytes.size)
        output.writeFully(bytes)
    }

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<T> {
        return paket.field(this, construct(serializer))
    }
}
