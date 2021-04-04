package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.SZLongField
import com.handtruth.mc.util.measureSZLong
import com.handtruth.mc.util.readSZLong
import com.handtruth.mc.util.writeSZLong
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object SZLongCodec : Codec<Long> {
    override fun measure(value: Long): Int = measureSZLong(value)
    override fun read(input: Input): Long = input.readSZLong()
    override fun write(output: Output, value: Long): Unit = output.writeSZLong(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): SZLongField {
        return paket.field(SZLongField())
    }
}
