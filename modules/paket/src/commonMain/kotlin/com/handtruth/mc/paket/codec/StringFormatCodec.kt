package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.util.construct
import com.handtruth.mc.paket.util.readString
import com.handtruth.mc.paket.util.writeString
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlin.reflect.KProperty

public class StringFormatCodec<T>(public val serializer: KSerializer<T>, public val format: StringFormat) : Codec<T> {
    override fun measure(value: T): Int = throw UnsupportedOperationException()
    override fun read(input: Input): T = format.decodeFromString(serializer, input.readString())
    override fun write(output: Output, value: T): Unit = output.writeString(format.encodeToString(serializer, value))

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<T> {
        return paket.field(this, construct(serializer))
    }
}
