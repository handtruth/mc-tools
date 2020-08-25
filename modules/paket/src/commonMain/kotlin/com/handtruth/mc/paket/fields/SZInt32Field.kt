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

object SZInt32ListCodec : ListCodec<Int>(SZInt32Codec)

class SZInt32Field(initial: Int) : Field<Int>(SZInt32Codec, initial)
class SZInt32ListField(initial: MutableList<Int>) : ListField<Int>(SZInt32ListCodec, initial)

fun Paket.szint32(initial: Int = 0) = field(SZInt32Field(initial))
fun Paket.zint(initial: Int) = szint32(initial)

fun Paket.listOfSzint32(initial: MutableList<Int> = mutableListOf()) = field(SZInt32ListField(initial))
@JvmName("listOfSzint32RO")
fun Paket.listOfSzint32(initial: List<Int>) = listOfSzint32(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<Int>) = listOfSzint32(initial)
@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<Int>) = listOfSzint32(initial)
