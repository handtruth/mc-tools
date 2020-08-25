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

object SZInt64ListCodec : ListCodec<Long>(SZInt64Codec)

class SZInt64Field(initial: Long) : Field<Long>(SZInt64Codec, initial)
class SZInt64ListField(initial: MutableList<Long>) : ListField<Long>(SZInt64ListCodec, initial)

fun Paket.szint64(initial: Long = 0) = field(SZInt64Field(initial))
fun Paket.zint(initial: Long) = szint64(initial)

fun Paket.listOfSzint64(initial: MutableList<Long> = mutableListOf()) = field(SZInt64ListField(initial))
@JvmName("listOfUzint64RO")
fun Paket.listOfSzint64(initial: List<Long>) = listOfSzint64(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<Long>) = listOfSzint64(initial)
@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<Long>) = listOfSzint64(initial)
