package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketFormat
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.util.MeasureEncoder
import com.handtruth.mc.paket.util.PaketDecoder
import com.handtruth.mc.paket.util.PaketEncoder
import com.handtruth.mc.paket.util.construct
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlin.reflect.KProperty

public class StructCodec<T>(
    public val serializer: KSerializer<T>,
    format: PaketFormat = PaketFormat()
) : Codec<T> {
    private val decoder = PaketDecoder(format.configuration, format.serializersModule)
    private val encoder = PaketEncoder(format.configuration, format.serializersModule)
    private val measure = MeasureEncoder(format.configuration, format.serializersModule)

    override fun measure(value: T): Int {
        measure.size = 0
        serializer.serialize(measure, value)
        return measure.size
    }

    override fun read(input: Input): T {
        decoder.input = input
        return serializer.deserialize(decoder)
    }

    override fun write(output: Output, value: T) {
        encoder.output = output
        serializer.serialize(encoder, value)
    }

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<T> {
        return paket.field(this, construct(serializer))
    }
}
