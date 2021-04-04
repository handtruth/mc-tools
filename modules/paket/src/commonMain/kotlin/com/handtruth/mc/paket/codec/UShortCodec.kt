package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.UShortField
import com.handtruth.mc.paket.util.SHORT_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object UShortCodec : Codec<UShort> {
    override fun measure(value: UShort): Int = SHORT_SIZE
    override fun read(input: Input): UShort = input.readUShort()
    override fun write(output: Output, value: UShort): Unit = output.writeUShort(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): UShortField {
        return paket.field(UShortField())
    }
}
