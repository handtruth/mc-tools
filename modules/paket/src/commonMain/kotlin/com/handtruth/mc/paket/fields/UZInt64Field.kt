package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.readUZInt64
import com.handtruth.mc.util.sizeUZInt64
import com.handtruth.mc.util.writeUZInt64
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object UZInt64Codec : Codec<ULong> {
    override fun measure(value: ULong) = sizeUZInt64(value)
    override fun read(input: Input, old: ULong?) = readUZInt64(input)
    override fun write(output: Output, value: ULong) = writeUZInt64(output, value)
}

object UZInt64ListCodec : ListCodec<ULong>(UZInt64Codec)

class UZInt64Field(initial: ULong) : Field<ULong>(UZInt64Codec, initial)
class UZInt64ListField(initial: MutableList<ULong>) : ListField<ULong>(UZInt64ListCodec, initial)

fun Paket.uzint64(initial: ULong = 0u) = field(UZInt64Field(initial))
fun Paket.zint(initial: ULong) = uzint64(initial)

fun Paket.listOfUzint64(initial: MutableList<ULong> = mutableListOf()) = field(UZInt64ListField(initial))
@JvmName("listOfUzint64RO")
fun Paket.listOfUzint64(initial: List<ULong>) = listOfUzint64(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<ULong>) = listOfUzint64(initial)
@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<ULong>) = listOfUzint64(initial)
