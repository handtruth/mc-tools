package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.ListCodec
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.util.measureUZInt
import com.handtruth.mc.util.readUZInt
import com.handtruth.mc.util.writeUZInt
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

object UZInt32Codec : Codec<UInt> {
    override fun measure(value: UInt) = measureUZInt(value)
    override fun read(input: Input, old: UInt?) = input.readUZInt()
    override fun write(output: Output, value: UInt) = output.writeUZInt(value)
}

val UZInt32ListCodec = ListCodec(UZInt32Codec)
val NullableUZInt32Codec = NullableCodec(UZInt32Codec)

fun Paket.uzint32(initial: UInt = 0u) = field(UZInt32Codec, initial)
fun Paket.zint(initial: UInt) = uzint32(initial)

fun Paket.nullableUzint32(initial: UInt? = null) = field(NullableUZInt32Codec, initial)
fun Paket.nullableZint(initial: UInt?) = field(NullableUZInt32Codec, initial)

fun Paket.listOfUzint32(initial: MutableList<UInt> = mutableListOf()) = field(UZInt32ListCodec, initial)

@JvmName("listOfUzint32RO")
fun Paket.listOfUzint32(initial: List<UInt>) = listOfUzint32(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<UInt>) = listOfUzint32(initial)

@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<UInt>) = listOfUzint32(initial)
