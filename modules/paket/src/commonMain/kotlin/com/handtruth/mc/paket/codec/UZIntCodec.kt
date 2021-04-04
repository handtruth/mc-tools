package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.UZIntField
import com.handtruth.mc.util.measureUZInt
import com.handtruth.mc.util.readUZInt
import com.handtruth.mc.util.writeUZInt
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object UZIntCodec : Codec<UInt> {
    override fun measure(value: UInt): Int = measureUZInt(value)
    override fun read(input: Input): UInt = input.readUZInt()
    override fun write(output: Output, value: UInt): Unit = output.writeUZInt(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): UZIntField {
        return paket.field(UZIntField())
    }
}
