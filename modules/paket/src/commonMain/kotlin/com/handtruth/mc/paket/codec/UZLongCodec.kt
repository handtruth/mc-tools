package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.UZLongField
import com.handtruth.mc.util.measureUZLong
import com.handtruth.mc.util.readUZLong
import com.handtruth.mc.util.writeUZLong
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object UZLongCodec : Codec<ULong> {
    override fun measure(value: ULong): Int = measureUZLong(value)
    override fun read(input: Input): ULong = input.readUZLong()
    override fun write(output: Output, value: ULong): Unit = output.writeUZLong(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): UZLongField {
        return paket.field(UZLongField())
    }
}
