package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.readUZInt32
import com.handtruth.mc.util.sizeUZInt32
import com.handtruth.mc.util.writeUZInt32
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object UZInt32Codec : Codec<UInt> {
    override fun measure(value: UInt) = sizeUZInt32(value)
    override fun read(input: Input, old: UInt?) = readUZInt32(input)
    override fun write(output: Output, value: UInt) = writeUZInt32(output, value)
}

object UZInt32ListCodec : ListCodec<UInt>(UZInt32Codec)

class UZInt32Field(initial: UInt) : Field<UInt>(UZInt32Codec, initial)
class UZInt32ListField(initial: MutableList<UInt>) : ListField<UInt>(UZInt32ListCodec, initial)

fun Paket.uzint32(initial: UInt = 0u) = field(UZInt32Field(initial))
fun Paket.zint(initial: UInt) = uzint32(initial)

fun Paket.listOfUzint32(initial: MutableList<UInt> = mutableListOf()) = field(UZInt32ListField(initial))
@JvmName("listOfUzint32RO")
fun Paket.listOfUzint32(initial: List<UInt>) = listOfUzint32(initial.toMutableList())
fun Paket.listOfZint(initial: MutableList<UInt>) = listOfUzint32(initial)
@JvmName("listOfZintRO")
fun Paket.listOfZint(initial: List<UInt>) = listOfUzint32(initial)
