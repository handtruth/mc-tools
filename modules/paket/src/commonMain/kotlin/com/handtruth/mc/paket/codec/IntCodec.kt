package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.INT_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.IntField
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object IntCodec : Codec<Int> {
    override fun measure(value: Int): Int = INT_SIZE
    override fun read(input: Input): Int = input.readInt()
    override fun write(output: Output, value: Int): Unit = output.writeInt(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): IntField {
        return paket.field(IntField())
    }
}
