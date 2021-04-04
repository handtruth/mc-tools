@file:Suppress("UNUSED_PARAMETER")

package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.*
import com.handtruth.mc.paket.util.construct

public fun <T> Paket.field(codec: Codec<T>, initial: T): Field<T> {
    val field = AnyField(codec, initial)
    fields += field
    return field
}

public fun Paket.field(codec: BooleanCodec, initial: Boolean = false): BooleanField = field(BooleanField(initial))
public fun Paket.field(codec: ByteCodec, initial: Byte = 0): ByteField = field(ByteField(initial))
public fun Paket.field(codec: CharCodec, initial: Char = 0.toChar()): CharField = field(CharField(initial))
public fun Paket.field(codec: DoubleCodec, initial: Double = .0): DoubleField = field(DoubleField(initial))
public fun Paket.field(codec: FloatCodec, initial: Float = .0f): FloatField = field(FloatField(initial))
public fun Paket.field(codec: IntCodec, initial: Int = 0): IntField = field(IntField(initial))
public fun Paket.field(codec: LongCodec, initial: Long = 0): LongField = field(LongField(initial))
public fun Paket.field(codec: ShortCodec, initial: Short = 0): ShortField = field(ShortField(initial))
public fun Paket.field(codec: SZIntCodec, initial: Int = 0): SZIntField = field(SZIntField(initial))
public fun Paket.field(codec: SZLongCodec, initial: Long = 0L): SZLongField = field(SZLongField(initial))
public fun Paket.field(codec: UZIntCodec, initial: UInt = 0u): UZIntField = field(UZIntField(initial))
public fun Paket.field(codec: UZLongCodec, initial: ULong = 0uL): UZLongField = field(UZLongField(initial))
public fun Paket.field(codec: UByteCodec, initial: UByte = 0u): UByteField = field(UByteField(initial))
public fun Paket.field(codec: UShortCodec, initial: UShort = 0u): UShortField = field(UShortField(initial))
public fun Paket.field(codec: UIntCodec, initial: UInt = 0u): UIntField = field(UIntField(initial))
public fun Paket.field(codec: ULongCodec, initial: ULong = 0uL): ULongField = field(ULongField(initial))

public fun <T> Paket.field(codec: StructCodec<T>, initial: T = construct(codec.serializer)): Field<T> =
    field(codec as Codec<T>, initial)
