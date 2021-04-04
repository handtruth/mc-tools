package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.util.SHORT_SIZE
import com.handtruth.mc.paket.field.ShortField
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object ShortCodec : Codec<Short> {
    override fun measure(value: Short): Int = SHORT_SIZE
    override fun read(input: Input): Short = input.readShort()
    override fun write(output: Output, value: Short): Unit = output.writeShort(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): ShortField {
        return paket.field(ShortField())
    }
}
