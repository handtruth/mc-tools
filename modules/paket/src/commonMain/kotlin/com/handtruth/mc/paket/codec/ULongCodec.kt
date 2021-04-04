package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.ULongField
import com.handtruth.mc.paket.util.LONG_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object ULongCodec : Codec<ULong> {
    override fun measure(value: ULong): Int = LONG_SIZE
    override fun read(input: Input): ULong = input.readULong()
    override fun write(output: Output, value: ULong): Unit = output.writeULong(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): ULongField {
        return paket.field(ULongField())
    }
}
