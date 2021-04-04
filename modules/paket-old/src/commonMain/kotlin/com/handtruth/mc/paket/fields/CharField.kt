package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object CharCodec : Codec<Char> {
    override fun measure(value: Char) = sizeBoolean
    override fun read(input: Input, old: Char?) = readShort(input).toChar()
    override fun write(output: Output, value: Char) = writeShort(output, value.toShort())
}

val CharListCodec = ListCodec(CharCodec)
val NullableCharCodec = NullableCodec(CharCodec)

fun Paket.char(initial: Char = 0.toChar()) = field(CharCodec, initial)
fun Paket.listOfChar(initial: MutableList<Char>) = field(CharListCodec, initial)
fun Paket.nullableChar(initial: Char? = null) = field(NullableCharCodec, initial)

@JvmName("listOfCharRO")
fun Paket.listOfChar(initial: List<Char>) = listOfChar(initial.toMutableList())
