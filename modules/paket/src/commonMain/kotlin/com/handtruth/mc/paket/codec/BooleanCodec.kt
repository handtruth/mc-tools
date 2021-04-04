package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.BOOLEAN_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.BooleanField
import com.handtruth.mc.paket.util.readBoolean
import com.handtruth.mc.paket.util.writeBoolean
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object BooleanCodec : Codec<Boolean> {
    override fun measure(value: Boolean): Int = BOOLEAN_SIZE
    override fun read(input: Input): Boolean = input.readBoolean()
    override fun write(output: Output, value: Boolean): Unit = output.writeBoolean(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): BooleanField {
        return paket.field(BooleanField())
    }
}
