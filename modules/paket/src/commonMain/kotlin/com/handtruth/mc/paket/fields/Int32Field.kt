package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object Int32Codec : Codec<Int> {
    override fun measure(value: Int) = sizeInt
    override fun read(input: Input, old: Int?) = readInt(input)
    override fun write(output: Output, value: Int) = writeInt(output, value)
}

val Int32ListCodec = ListCodec(Int32Codec)
val NullableInt32Codec = NullableCodec(Int32Codec)

fun Paket.int32(initial: Int = 0) = field(Int32Codec, initial)
fun Paket.listOfInt32(initial: MutableList<Int> = mutableListOf()) = field(Int32ListCodec, initial)
fun Paket.nullableInt32(initial: Int = 0) = field(NullableInt32Codec, initial)

@JvmName("listOfInt32RO")
fun Paket.listOfInt32(initial: List<Int>) = listOfInt32(initial.toMutableList())
