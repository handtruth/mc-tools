package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object BoolCodec : Codec<Boolean> {
    override fun measure(value: Boolean) = sizeBoolean
    override fun read(input: Input, old: Boolean?) = readBoolean(input)
    override fun write(output: Output, value: Boolean) = writeBoolean(output, value)
}

val BoolListCodec = ListCodec(BoolCodec)
val NullableBoolCodec = NullableCodec(BoolCodec)

fun Paket.bool(initial: Boolean = false) = field(BoolCodec, initial)
fun Paket.listOfBool(initial: MutableList<Boolean>) = field(BoolListCodec, initial)
fun Paket.nullableBool(initial: Boolean? = null) = field(NullableBoolCodec, initial)

@JvmName("listOfBoolRO")
fun Paket.listOfBool(initial: List<Boolean>) = listOfBool(initial.toMutableList())
