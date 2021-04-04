package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object DoubleCodec : Codec<Double> {
    override fun measure(value: Double) = sizeDouble
    override fun read(input: Input, old: Double?) = readDouble(input)
    override fun write(output: Output, value: Double) = writeDouble(output, value)
}

val DoubleListCodec = ListCodec(DoubleCodec)
val NullableDoubleCodec = NullableCodec(DoubleCodec)

fun Paket.double(initial: Double = .0) = field(DoubleCodec, initial)
fun Paket.listOfDouble(initial: MutableList<Double> = mutableListOf()) = field(DoubleListCodec, initial)
fun Paket.nullableDouble(initial: Double? = null) = field(NullableDoubleCodec, initial)

@JvmName("listOfDoubleRO")
fun Paket.listOfDouble(initial: List<Double>) = listOfDouble(initial.toMutableList())
