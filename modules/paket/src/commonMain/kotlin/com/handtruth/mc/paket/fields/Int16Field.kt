package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object Int16Codec : Codec<Short> {
    override fun measure(value: Short) = sizeShort
    override fun read(input: Input, old: Short?) = readShort(input)
    override fun write(output: Output, value: Short) = writeShort(output, value)
}

val Int16ListCodec = ListCodec(Int16Codec)
val NullableInt16Codec = NullableCodec(Int16Codec)

fun Paket.int16(initial: Short = 0) = field(Int16Codec, initial)
fun Paket.listOfInt16(initial: MutableList<Short> = mutableListOf()) = field(Int16ListCodec, initial)
fun Paket.nullableField(initial: Short? = null) = field(NullableInt16Codec, initial)

@JvmName("listOfInt16RO")
fun Paket.listOfInt16(initial: List<Short>) = listOfInt16(initial.toMutableList())
