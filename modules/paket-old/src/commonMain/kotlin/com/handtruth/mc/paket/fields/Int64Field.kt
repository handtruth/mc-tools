package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object Int64Codec : Codec<Long> {
    override fun measure(value: Long) = sizeLong
    override fun read(input: Input, old: Long?) = readLong(input)
    override fun write(output: Output, value: Long) = writeLong(output, value)
}

val Int64ListCodec = ListCodec(Int64Codec)
val NullableInt64Codec = NullableCodec(Int64Codec)

fun Paket.int64(initial: Long = 0L) = field(Int64Codec, initial)
fun Paket.listOfInt64(initial: MutableList<Long> = mutableListOf()) = field(Int64ListCodec, initial)
fun Paket.nullableInt64(initial: Long? = null) = field(NullableInt64Codec, initial)

@JvmName("listOfInt64RO")
fun Paket.listOfInt64(initial: List<Long>) = listOfInt64(initial.toMutableList())
