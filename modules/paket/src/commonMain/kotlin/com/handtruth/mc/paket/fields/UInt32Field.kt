package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt32Codec : Codec<UInt> {
    override fun measure(value: UInt) = sizeInt
    override fun read(input: Input, old: UInt?) = readInt(input).toUInt()
    override fun write(output: Output, value: UInt) = writeInt(output, value.toInt())
}

@ExperimentalPaketApi
val UInt32ListCodec = ListCodec(UInt32Codec)

@ExperimentalPaketApi
val NullableUInt32Codec = NullableCodec(UInt32Codec)

@ExperimentalPaketApi
fun Paket.uint32(initial: UInt = 0u) = field(UInt32Codec, initial)

@ExperimentalPaketApi
fun Paket.nullableUint32(initial: UInt? = null) = field(NullableUInt32Codec, initial)

@ExperimentalPaketApi
fun Paket.listOfUint32(initial: MutableList<UInt> = mutableListOf()) = field(UInt32ListCodec, initial)

@ExperimentalPaketApi
@JvmName("listOfUint32RO")
fun Paket.listOfUint32(initial: List<UInt>) = listOfUint32(initial.toMutableList())
