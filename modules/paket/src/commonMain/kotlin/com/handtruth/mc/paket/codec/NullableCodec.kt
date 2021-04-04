package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.BOOLEAN_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.util.readBoolean
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class NullableCodec<T>(private val codec: Codec<T>) : Codec<T?> {
    override fun measure(value: T?): Int = if (value === null) BOOLEAN_SIZE else BOOLEAN_SIZE + codec.measure(value)

    override fun read(input: Input): T? {
        val isNotNull = input.readBoolean()
        return if (isNotNull) codec.read(input) else null
    }

    override fun write(output: Output, value: T?) {
        if (value === null) {
            output.writeByte(0)
        } else {
            output.writeByte(1)
            codec.write(output, value)
        }
    }

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<T?> {
        return paket.field(this, null)
    }
}
