package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.readSZInt32
import com.handtruth.mc.util.sizeSZInt32
import com.handtruth.mc.util.writeSZInt32
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object SZInt32Codec : Codec<Int> {
    override fun measure(value: Int) = sizeSZInt32(value)
    override fun read(input: Input, old: Int?) = readSZInt32(input)
    override fun write(output: Output, value: Int) = writeSZInt32(output, value)
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
