package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.util.measureVarInt
import com.handtruth.mc.paket.util.readVarInt
import com.handtruth.mc.paket.util.writeVarInt
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class EnumCodec<E : Enum<E>>(private val values: Array<E>) : Codec<E> {
    override fun measure(value: E): Int = measureVarInt(value.ordinal)
    override fun read(input: Input): E = values[input.readVarInt()]
    override fun write(output: Output, value: E): Unit = output.writeVarInt(value.ordinal)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<E> {
        return paket.field(this, values[0])
    }
}
