package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.DOUBLE_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.DoubleField
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object DoubleCodec : Codec<Double> {
    override fun measure(value: Double): Int = DOUBLE_SIZE
    override fun read(input: Input): Double = input.readDouble()
    override fun write(output: Output, value: Double): Unit = output.writeDouble(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): DoubleField {
        return paket.field(DoubleField())
    }
}
