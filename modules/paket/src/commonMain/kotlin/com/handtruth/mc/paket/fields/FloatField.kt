package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object FloatCodec : Codec<Float> {
    override fun measure(value: Float) = sizeFloat
    override fun read(input: Input, old: Float?) = readFloat(input)
    override fun write(output: Output, value: Float) = writeFloat(output, value)
}

val FloatListCodec = ListCodec(FloatCodec)
val NullableFloatCodec = NullableCodec(FloatCodec)

fun Paket.float(initial: Float = 0f) = field(FloatCodec, initial)
fun Paket.listOfFloat(initial: MutableList<Float> = mutableListOf()) = field(FloatListCodec, initial)
fun Paket.nullableFloat(initial: Float? = null) = field(NullableFloatCodec, initial)

@JvmName("listOfFloatRO")
fun Paket.listOfFloat(initial: List<Float>) = listOfFloat(initial.toMutableList())
