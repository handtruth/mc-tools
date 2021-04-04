package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.LongField
import com.handtruth.mc.paket.util.LONG_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object LongCodec : Codec<Long> {
    override fun measure(value: Long): Int = LONG_SIZE
    override fun read(input: Input): Long = input.readLong()
    override fun write(output: Output, value: Long): Unit = output.writeLong(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): LongField {
        return paket.field(LongField())
    }
}
