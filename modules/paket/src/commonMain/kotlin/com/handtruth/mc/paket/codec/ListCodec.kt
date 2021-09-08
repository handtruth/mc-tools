package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.Field
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.util.measureVarInt
import com.handtruth.mc.paket.util.readVarInt
import com.handtruth.mc.paket.util.writeVarInt
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class ListCodec<T>(private val codec: Codec<T>) : Codec<List<T>> {
    override fun measure(value: List<T>): Int =
        measureVarInt(value.size) + value.sumOf { codec.measure(it) }

    override fun read(input: Input): List<T> {
        val size = input.readVarInt()
        return List(size) { codec.read(input) }
    }

    override fun write(output: Output, value: List<T>) {
        output.writeVarInt(value.size)
        value.forEach { codec.write(output, it) }
    }

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): Field<List<T>> {
        return paket.field(this, emptyList())
    }
}
