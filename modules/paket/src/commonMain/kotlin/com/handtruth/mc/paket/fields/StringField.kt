package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object StringCodec : Codec<String> {
    override fun measure(value: String) = sizeString(value)
    override fun read(input: Input, old: String?) = readString(input)
    override fun write(output: Output, value: String) = writeString(output, value)
}

val StringListCodec = ListCodec(StringCodec)
val NullableStringCodec = NullableCodec(StringCodec)

fun Paket.string(initial: String = "") = field(StringCodec, initial)
fun Paket.listOfString(initial: MutableList<String> = mutableListOf()) = field(StringListCodec, initial)
fun Paket.nullableString(initial: String? = null) = field(NullableStringCodec, initial)

@JvmName("listOfStringRO")
fun Paket.listOfString(initial: List<String>) = listOfString(initial.toMutableList())
