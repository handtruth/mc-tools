package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

@ObsoletePaketApi
object VarLongCodec : Codec<Long> {
    override fun measure(value: Long) = sizeVarLong(value)
    override fun read(input: Input, old: Long?) = readVarLong(input)
    override fun write(output: Output, value: Long) = writeVarLong(output, value)
}

@ObsoletePaketApi
val VarLongListCodec = ListCodec(VarLongCodec)

@ObsoletePaketApi
val NullableVarLongCodec = NullableCodec(VarLongCodec)

@ObsoletePaketApi
fun Paket.varLong(initial: Long = 0L) = field(VarLongCodec, initial)

@ObsoletePaketApi
fun Paket.nullableVarLong(initial: Long? = null) = field(NullableVarLongCodec, initial)

@ObsoletePaketApi
fun Paket.listOfVarLong(initial: MutableList<Long> = mutableListOf()) = field(VarLongListCodec, initial)

@ObsoletePaketApi
@JvmName("listOfVarLongRO")
fun Paket.listOfVarLong(initial: List<Long>) = listOfVarLong(initial.toMutableList())
