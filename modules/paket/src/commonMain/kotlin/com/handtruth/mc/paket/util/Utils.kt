@file:Suppress("FunctionName")

package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.transmitter.Transmitter
import com.handtruth.mc.util.measureUZInt
import com.handtruth.mc.util.readUZInt
import com.handtruth.mc.util.writeUZInt
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

internal fun measureVarInt(value: Int) = measureUZInt(value.toUInt())

internal fun Input.readVarInt() = readUZInt().toInt()

internal fun Output.writeVarInt(value: Int) = writeUZInt(value.toUInt())

internal fun sizeStringChars(sequence: CharSequence): Int {
    var count = 0
    var i = 0
    val len = sequence.length
    while (i < len) {
        val ch = sequence[i]
        when {
            ch.toInt() <= 0x7F -> count++
            ch.toInt() <= 0x7FF -> count += 2
            ch.isHighSurrogate() -> {
                count += 4
                ++i
            }
            else -> count += 3
        }
        i++
    }
    return count
}

internal fun measureString(sequence: CharSequence): Int = sizeStringChars(sequence).let { it + measureVarInt(it) }

internal fun Input.readString(): String {
    val size = readVarInt()
    return readTextExactBytes(size, Charsets.UTF_8)
}

internal fun Output.writeString(value: String) {
    writeVarInt(sizeStringChars(value))
    writeText(value)
}

internal const val BOOLEAN_SIZE: Int = 1

internal fun Input.readBoolean(): Boolean = readByte() != 0.toByte()

internal fun Output.writeBoolean(value: Boolean): Unit = writeByte(if (value) 1 else 0)

internal const val FLOAT_SIZE: Int = Float.SIZE_BYTES

internal const val DOUBLE_SIZE: Int = Double.SIZE_BYTES

internal const val BYTE_SIZE: Int = Byte.SIZE_BYTES

internal const val SHORT_SIZE: Int = Short.SIZE_BYTES

internal const val INT_SIZE: Int = Int.SIZE_BYTES

internal const val LONG_SIZE: Int = Long.SIZE_BYTES

internal const val CHAR_SIZE: Int = Char.SIZE_BYTES

internal fun Input.readChar(): Char = readShort().toChar()

internal fun Output.writeChar(value: Char): Unit = writeShort(value.toShort())

@PublishedApi
internal fun <T> construct(serializer: KSerializer<T>): T = serializer.deserialize(EmptyDecoder)

@PublishedApi
internal inline fun <reified T> construct(): T = construct(serializer())

@PublishedApi
internal inline fun <reified T> StructPaket(initial: T): StructPaket<T> {
    val serializer = serializer<T>()
    return StructPaket(serializer, initial)
}

@PublishedApi
internal fun <T> StructPaket(serializer: KSerializer<T>): StructPaket<T> {
    return StructPaket(serializer, construct(serializer))
}

@PublishedApi
internal inline fun <reified T> StructPaket(): StructPaket<T> {
    val serializer = serializer<T>()
    return StructPaket(serializer, construct(serializer))
}

public inline fun <reified Q, reified R> Barter(transmitter: Transmitter): Barter<Q, R> {
    return Barter(transmitter, serializer(), serializer())
}
