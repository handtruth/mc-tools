package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.ListCodec
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.util.measureSZLong
import com.handtruth.mc.util.readSZLong
import com.handtruth.mc.util.writeSZLong
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object SZInt64Codec : Codec<Long> {
    override fun measure(value: Long) = measureSZLong(value)
    override fun read(input: Input, old: Long?) = input.readSZLong()
    override fun write(output: Output, value: Long) = output.writeSZLong(value)
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
