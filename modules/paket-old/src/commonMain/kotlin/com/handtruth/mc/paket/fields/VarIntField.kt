package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

@ObsoletePaketApi
object VarIntCodec : Codec<Int> {
    override fun measure(value: Int) = sizeVarInt(value)
    override fun read(input: Input, old: Int?) = readVarInt(input)
    override fun write(output: Output, value: Int) = writeVarInt(output, value)
}

@ObsoletePaketApi
val VarIntListCodec = ListCodec(VarIntCodec)

@ObsoletePaketApi
val NullableVarIntCodec = NullableCodec(VarIntCodec)

@ObsoletePaketApi
fun Paket.varInt(initial: Int = 0) = field(VarIntCodec, initial)

@ObsoletePaketApi
fun Paket.nullableVarInt(initial: Int? = null) = field(NullableVarIntCodec, initial)

@ObsoletePaketApi
fun Paket.listOfVarInt(initial: MutableList<Int> = mutableListOf()) = field(VarIntListCodec, initial)

@ObsoletePaketApi
@JvmName("listOfVarIntRO")
fun Paket.listOfVarInt(initial: List<Int>) = listOfVarInt(initial.toMutableList())
