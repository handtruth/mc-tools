package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

class EnumCodec<T : Enum<T>>(private val all: Array<T>) : Codec<T> {
    override fun measure(value: T) = sizeVarInt(value.ordinal)
    override fun read(input: Input, old: T?) = all[readVarInt(input)]
    override fun write(output: Output, value: T) = writeVarInt(output, value.ordinal)

    val values get() = all.asSequence()
}

inline fun <reified T : Enum<T>> EnumCodec() = EnumCodec(enumValues<T>())

fun <T : Enum<T>> EnumListCodec(values: Array<T>) = ListCodec(EnumCodec(values))
inline fun <reified T : Enum<T>> EnumListCodec() = EnumListCodec(enumValues<T>())
fun <T : Enum<T>> NullableEnumCodec(values: Array<T>) = NullableCodec(EnumCodec(values))
inline fun <reified T : Enum<T>> NullableEnumCodec() = NullableEnumCodec(enumValues<T>())

inline fun <reified E : Enum<E>> Paket.enum(initial: E = enumValues<E>()[0]) =
    field(EnumCodec(), initial)
inline fun <reified E : Enum<E>> Paket.listOfEnum(initial: MutableList<E> = mutableListOf()) =
    field(EnumListCodec(), initial)
inline fun <reified E : Enum<E>> Paket.nullableEnum(initial: E? = null) = field(NullableEnumCodec(), initial)

@JvmName("listOfEnumRO")
inline fun <reified E : Enum<E>> Paket.listOfEnum(initial: List<E>) = listOfEnum(initial.toMutableList())
