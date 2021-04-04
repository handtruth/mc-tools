package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object Int8Codec : Codec<Byte> {
    override fun measure(value: Byte) = sizeByte
    override fun read(input: Input, old: Byte?) = readByte(input)
    override fun write(output: Output, value: Byte) = writeByte(output, value)
}

val Int8ListCodec = ListCodec(Int8Codec)
val NullableInt8Codec = NullableCodec(Int8Codec)

fun Paket.int8(initial: Byte = 0) = field(Int8Codec, initial)
fun Paket.listOfInt8(initial: MutableList<Byte>) = field(Int8ListCodec, initial)
fun Paket.nullableInt8(initial: Byte? = null) = field(NullableInt8Codec, initial)

@JvmName("listOfByteRO")
fun Paket.listOfInt8(initial: List<Byte>) = listOfInt8(initial.toMutableList())
