package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt64Codec : Codec<ULong> {
    override fun measure(value: ULong) = sizeLong
    override fun read(input: Input, old: ULong?) = readLong(input).toULong()
    override fun write(output: Output, value: ULong) = writeLong(output, value.toLong())
}

@ExperimentalPaketApi
val UInt64ListCodec = ListCodec(UInt64Codec)

@ExperimentalPaketApi
val NullableUInt64Codec = NullableCodec(UInt64Codec)

@ExperimentalPaketApi
fun Paket.uint64(initial: ULong = 0u) = field(UInt64Codec, initial)

@ExperimentalPaketApi
fun Paket.nullableUint64(initial: ULong? = null) = field(NullableUInt64Codec, initial)

@ExperimentalPaketApi
fun Paket.listOfUint64(initial: MutableList<ULong> = mutableListOf()) = field(UInt64ListCodec, initial)

@ExperimentalPaketApi
@JvmName("listOfUint64RO")
fun Paket.listOfUint64(initial: List<ULong>) = listOfUint64(initial.toMutableList())
