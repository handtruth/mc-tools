package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.FLOAT_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.FloatField
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object FloatCodec : Codec<Float> {
    override fun measure(value: Float): Int = FLOAT_SIZE
    override fun read(input: Input): Float = input.readFloat()
    override fun write(output: Output, value: Float): Unit = output.writeFloat(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): FloatField {
        return paket.field(FloatField())
    }
}
