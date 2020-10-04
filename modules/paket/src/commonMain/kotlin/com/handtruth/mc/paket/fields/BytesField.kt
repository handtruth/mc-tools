package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Bytes
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.buildBytes
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object BytesCodec : Codec<Bytes> {
    override fun measure(value: Bytes) = sizeBytes(value)
    override fun read(input: Input, old: Bytes?) = readBytes(input)
    override fun write(output: Output, value: Bytes) = writeBytes(output, value)
}

@ExperimentalPaketApi
val BytesListCodec = ListCodec(BytesCodec)

@ExperimentalPaketApi
val NullableBytesCodec = NullableCodec(BytesCodec)

@ExperimentalPaketApi
fun Paket.bytes(initial: Bytes = buildBytes { }) = field(BytesCodec, initial)

@ExperimentalPaketApi
fun Paket.listOfBytes(initial: MutableList<Bytes>) = field(BytesListCodec, initial)

@ExperimentalPaketApi
fun Paket.nullableBytes(initial: Bytes? = null) = field(NullableBytesCodec, initial)

@ExperimentalPaketApi
@JvmName("listOfBytesRO")
fun Paket.listOfBytes(initial: List<Bytes>) = listOfBytes(initial.toMutableList())
