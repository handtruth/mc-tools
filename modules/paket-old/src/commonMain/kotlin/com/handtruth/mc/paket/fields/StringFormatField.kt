package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.Paket
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

class StringFormatCodec<T>(val serializer: KSerializer<T>, val format: StringFormat) : Codec<T> {
    override fun measure(value: T) = StringCodec.measure(format.encodeToString(serializer, value))
    override fun read(input: Input, old: T?) = format.decodeFromString(serializer, StringCodec.read(input, null))
    override fun write(output: Output, value: T) = StringCodec.write(output, format.encodeToString(serializer, value))
}

fun <T> Paket.string(format: StringFormat, initial: T, serializer: KSerializer<T>) =
    field(StringFormatCodec(serializer, format), initial)
inline fun <reified T : Any> Paket.string(format: StringFormat, initial: T) =
    string(format, initial, serializer())
