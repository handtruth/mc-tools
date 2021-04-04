package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object BytesCodec : Codec<ByteReadPacket> {
    override fun measure(value: ByteReadPacket) = sizeBytes(value)
    override fun read(input: Input, old: ByteReadPacket?) = readBytes(input)
    override fun write(output: Output, value: ByteReadPacket) = writeBytes(output, value)
}

@ExperimentalPaketApi
val BytesListCodec = ListCodec(BytesCodec)

@ExperimentalPaketApi
val NullableBytesCodec = NullableCodec(BytesCodec)

@ExperimentalPaketApi
fun Paket.bytes(initial: ByteReadPacket = buildPacket { }) = field(BytesCodec, initial)

@ExperimentalPaketApi
fun Paket.listOfBytes(initial: MutableList<ByteReadPacket>) = field(BytesListCodec, initial)

@ExperimentalPaketApi
fun Paket.nullableBytes(initial: ByteReadPacket? = null) = field(NullableBytesCodec, initial)

@ExperimentalPaketApi
@JvmName("listOfBytesRO")
fun Paket.listOfBytes(initial: List<ByteReadPacket>) = listOfBytes(initial.toMutableList())
