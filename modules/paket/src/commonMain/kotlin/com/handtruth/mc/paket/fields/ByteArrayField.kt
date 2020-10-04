package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object ByteArrayCodec : Codec<ByteArray> {
    override fun measure(value: ByteArray) = sizeByteArray(value)
    override fun read(input: Input, old: ByteArray?) = readByteArray(input)
    override fun write(output: Output, value: ByteArray) = writeByteArray(output, value)
}

val ByteArrayListCodec = ListCodec(ByteArrayCodec)
val NullableByteArray = NullableCodec(ByteArrayCodec)

fun Paket.byteArray(initial: ByteArray = ByteArray(0)) = field(ByteArrayCodec, initial)
fun Paket.listOfByteArray(initial: MutableList<ByteArray>) = field(ByteArrayListCodec, initial)
fun Paket.nullableByteArray(initial: ByteArray? = null) = field(NullableByteArray, initial)

@JvmName("listOfByteArrayRO")
fun Paket.listOfByteArray(initial: List<ByteArray>) = listOfByteArray(initial.toMutableList())
