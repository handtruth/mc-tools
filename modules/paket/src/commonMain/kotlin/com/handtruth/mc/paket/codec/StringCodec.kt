package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.AnyField
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.util.measureString
import com.handtruth.mc.paket.util.readString
import com.handtruth.mc.paket.util.writeString
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object StringCodec : Codec<String> {
    override fun measure(value: String): Int = measureString(value)
    override fun read(input: Input): String = input.readString()
    override fun write(output: Output, value: String): Unit = output.writeString(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<String> {
        return paket.field(AnyField(this, ""))
    }
}
