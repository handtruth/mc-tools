package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt16Codec : Codec<UShort> {
    override fun measure(value: UShort) = sizeShort
    override fun read(input: Input, old: UShort?) = readShort(input).toUShort()
    override fun write(output: Output, value: UShort) = writeShort(output, value.toShort())
}

@ExperimentalPaketApi
val UInt16ListCodec = ListCodec(UInt16Codec)

@ExperimentalPaketApi
val NullableUInt16Codec = NullableCodec(UInt16Codec)

@ExperimentalPaketApi
fun Paket.uint16(initial: UShort = 0u) = field(UInt16Codec, initial)

@ExperimentalPaketApi
fun Paket.nullableUint16(initial: UShort? = null) = field(NullableUInt16Codec, initial)

@ExperimentalPaketApi
fun Paket.listOfUint16(initial: MutableList<UShort> = mutableListOf()) = field(UInt16ListCodec, initial)

@ExperimentalPaketApi
@JvmName("listOfUint16RO")
fun Paket.listOfUint16(initial: List<UShort>) = listOfUint16(initial.toMutableList())
