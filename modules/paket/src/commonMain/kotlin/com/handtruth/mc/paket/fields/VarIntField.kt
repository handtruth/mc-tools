package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object VarIntCodec : Codec<Int> {
    override fun measure(value: Int) = sizeVarInt(value)
    override fun read(input: Input, old: Int?) = readVarInt(input)
    override fun write(output: Output, value: Int) = writeVarInt(output, value)
}

val VarIntListCodec = ListCodec(VarIntCodec)
val NullableVarIntCodec = NullableCodec(VarIntCodec)

fun Paket.varInt(initial: Int = 0) = field(VarIntCodec, initial)
fun Paket.nullableVarInt(initial: Int? = null) = field(NullableVarIntCodec, initial)
fun Paket.listOfVarInt(initial: MutableList<Int> = mutableListOf()) = field(VarIntListCodec, initial)

@JvmName("listOfVarIntRO")
fun Paket.listOfVarInt(initial: List<Int>) = listOfVarInt(initial.toMutableList())
