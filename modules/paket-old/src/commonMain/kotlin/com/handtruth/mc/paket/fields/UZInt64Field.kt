package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.ListCodec
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.util.measureUZLong
import com.handtruth.mc.util.readUZLong
import com.handtruth.mc.util.writeUZLong
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object UZInt64Codec : Codec<ULong> {
    override fun measure(value: ULong) = measureUZLong(value)
    override fun read(input: Input, old: ULong?) = input.readUZLong()
    override fun write(output: Output, value: ULong) = output.writeUZLong(value)
}

val UZInt64ListCodec = ListCodec(UZInt64Codec)
val NullableUZInt64Codec = NullableCodec(UZInt64Codec)

fun Paket.uzint64(initial: ULong = 0u) = field(UZInt64Codec, initial)
fun Paket.zint(initial: ULong) = uzint64(initial)

fun Paket.nullableUzint64(initial: ULong? = null) = field(NullableUZInt64Codec, initial)
fun Paket.nullableZint(initial: ULong?) = nullableUzint64(initial)

fun Paket.listOfUzint64(initial: MutableList<ULong> = mutableListOf()) = field(UZInt64ListCodec, initial)

@JvmName("listOfUzint64RO")
fun Paket.listOfUzint64(initial: List<ULong>) = listOfUzint64(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<ULong>) = listOfUzint64(initial)

@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<ULong>) = listOfUzint64(initial)
