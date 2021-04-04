package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.SZIntField
import com.handtruth.mc.util.measureSZInt
import com.handtruth.mc.util.readSZInt
import com.handtruth.mc.util.writeSZInt
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object SZIntCodec : Codec<Int> {
    override fun measure(value: Int): Int = measureSZInt(value)
    override fun read(input: Input): Int = input.readSZInt()
    override fun write(output: Output, value: Int): Unit = output.writeSZInt(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): SZIntField {
        return paket.field(SZIntField())
    }
}
