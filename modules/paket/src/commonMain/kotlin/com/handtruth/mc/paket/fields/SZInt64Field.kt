package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.readSZInt64
import com.handtruth.mc.util.sizeSZInt64
import com.handtruth.mc.util.writeSZInt64
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object SZInt64Codec : Codec<Long> {
    override fun measure(value: Long) = sizeSZInt64(value)
    override fun read(input: Input, old: Long?) = readSZInt64(input)
    override fun write(output: Output, value: Long) = writeSZInt64(output, value)
}

val SZInt64ListCodec = ListCodec(SZInt64Codec)
val NullableSZInt64Codec = NullableCodec(SZInt64Codec)

fun Paket.szint64(initial: Long = 0) = field(SZInt64Codec, initial)
fun Paket.zint(initial: Long) = szint64(initial)
fun Paket.nullableSzint64(initial: Long? = null) = field(NullableSZInt64Codec, initial)
fun Paket.nullableZint(initial: Long?) = nullableSzint64(initial)

fun Paket.listOfSzint64(initial: MutableList<Long> = mutableListOf()) = field(SZInt64ListCodec, initial)

@JvmName("listOfUzint64RO")
fun Paket.listOfSzint64(initial: List<Long>) = listOfSzint64(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<Long>) = listOfSzint64(initial)

@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<Long>) = listOfSzint64(initial)
