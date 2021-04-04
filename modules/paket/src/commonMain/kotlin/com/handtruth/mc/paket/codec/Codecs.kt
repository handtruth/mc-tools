@file:Suppress("FunctionName")

package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.PaketFormat
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

public inline fun <reified E : Enum<E>> EnumCodec(): EnumCodec<E> = EnumCodec(enumValues())

public fun <T> binary(serializer: KSerializer<T>, format: BinaryFormat): BinaryFormatCodec<T> =
    BinaryFormatCodec(serializer, format)

public inline fun <reified T> binary(format: BinaryFormat): BinaryFormatCodec<T> =
    BinaryFormatCodec(serializer(), format)

public inline val boolean: BooleanCodec get() = BooleanCodec
public inline val byte: ByteCodec get() = ByteCodec
public inline val char: CharCodec get() = CharCodec
public inline val double: DoubleCodec get() = DoubleCodec
public inline fun <reified E : Enum<E>> enum(): EnumCodec<E> = EnumCodec()
public inline val float: FloatCodec get() = FloatCodec
public inline val int: IntCodec get() = IntCodec
public fun <T> list(codec: Codec<T>): ListCodec<T> = ListCodec(codec)
public inline val long: LongCodec get() = LongCodec
public fun <T> nullable(codec: Codec<T>): NullableCodec<T> = NullableCodec(codec)
public inline val short: ShortCodec get() = ShortCodec
public inline val string: StringCodec get() = StringCodec

public fun <T> string(serializer: KSerializer<T>, format: StringFormat): StringFormatCodec<T> =
    StringFormatCodec(serializer, format)

public inline fun <reified T> string(format: StringFormat): StringFormatCodec<T> =
    StringFormatCodec(serializer(), format)

public fun <T> struct(serializer: KSerializer<T>, format: PaketFormat = PaketFormat()): StructCodec<T> =
    StructCodec(serializer, format)

public inline fun <reified T> struct(format: PaketFormat = PaketFormat()): StructCodec<T> =
    StructCodec(serializer(), format)

public inline val szint: SZIntCodec get() = SZIntCodec
public inline val szlong: SZLongCodec get() = SZLongCodec
public inline val ubyte: UByteCodec get() = UByteCodec
public inline val ushort: UShortCodec get() = UShortCodec
public inline val uint: UIntCodec get() = UIntCodec
public inline val ulong: ULongCodec get() = ULongCodec
public inline val uzint: UZIntCodec get() = UZIntCodec
public inline val uzlong: UZLongCodec get() = UZLongCodec
