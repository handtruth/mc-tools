package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt8Codec : Codec<UByte> {
    override fun measure(value: UByte) = sizeByte
    override fun read(input: Input, old: UByte?) = readByte(input).toUByte()
    override fun write(output: Output, value: UByte) = writeByte(output, value.toByte())
}

@ExperimentalPaketApi
val UInt8ListCodec = ListCodec(UInt8Codec)

@ExperimentalPaketApi
val NullableUInt8Codec = NullableCodec(UInt8Codec)

@ExperimentalPaketApi
fun Paket.uint8(initial: UByte = 0u) = field(UInt8Codec, initial)

@ExperimentalPaketApi
fun Paket.nullableUint8(initial: UByte? = null) = field(NullableUInt8Codec, initial)

@ExperimentalPaketApi
fun Paket.listOfUint8(initial: MutableList<UByte> = mutableListOf()) = field(UInt8ListCodec, initial)

@ExperimentalPaketApi
@JvmName("listOfUint8RO")
fun Paket.listOfUint8(initial: List<UByte>) = listOfUint8(initial.toMutableList())
