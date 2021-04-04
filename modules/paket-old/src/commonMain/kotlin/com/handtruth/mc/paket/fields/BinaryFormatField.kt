package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.Paket
import io.ktor.utils.io.core.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class BinaryFormatCodec<T>(val serializer: KSerializer<T>, val format: BinaryFormat) : Codec<T> {
    override fun measure(value: T) = ByteArrayCodec.measure(format.encodeToByteArray(serializer, value))
    override fun read(input: Input, old: T?) = format.decodeFromByteArray(serializer, ByteArrayCodec.read(input, null))
    override fun write(output: Output, value: T) = ByteArrayCodec.write(output, format.encodeToByteArray(serializer, value))
}

fun <T> Paket.binary(format: BinaryFormat, initial: T, serializer: KSerializer<T>) =
    field(BinaryFormatCodec(serializer, format), initial)
inline fun <reified T : Any> Paket.binary(format: BinaryFormat, initial: T) = binary(format, initial, serializer())
