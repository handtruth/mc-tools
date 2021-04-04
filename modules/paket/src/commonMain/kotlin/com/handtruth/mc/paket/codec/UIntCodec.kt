package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.UIntField
import com.handtruth.mc.paket.util.INT_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object UIntCodec : Codec<UInt> {
    override fun measure(value: UInt): Int = INT_SIZE
    override fun read(input: Input): UInt = input.readUInt()
    override fun write(output: Output, value: UInt): Unit = output.writeUInt(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): UIntField {
        return paket.field(UIntField())
    }
}
