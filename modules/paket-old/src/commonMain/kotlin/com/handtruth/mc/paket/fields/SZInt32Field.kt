package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.ListCodec
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.util.measureSZInt
import com.handtruth.mc.util.readSZInt
import com.handtruth.mc.util.writeSZInt
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object SZInt32Codec : Codec<Int> {
    override fun measure(value: Int) = measureSZInt(value)
    override fun read(input: Input, old: Int?) = input.readSZInt()
    override fun write(output: Output, value: Int) = output.writeSZInt(value)
}

val SZInt32ListCodec = ListCodec(SZInt32Codec)
val NullableSZInt32Codec = NullableCodec(SZInt32Codec)

fun Paket.szint32(initial: Int = 0) = field(SZInt32Codec, initial)
fun Paket.zint(initial: Int) = szint32(initial)
fun Paket.nullableSzint32(initial: Int? = null) = field(NullableSZInt32Codec, initial)
fun Paket.nullableZint(initial: Int?) = nullableSzint32(initial)

fun Paket.listOfSzint32(initial: MutableList<Int> = mutableListOf()) = field(SZInt32ListCodec, initial)

@JvmName("listOfSzint32RO")
fun Paket.listOfSzint32(initial: List<Int>) = listOfSzint32(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<Int>) = listOfSzint32(initial)

@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<Int>) = listOfSzint32(initial)
