package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.Path
import com.handtruth.mc.util.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*

internal fun sizeVarInt(value: Int) = measureUZInt(value.toUInt())

internal fun readVarInt(input: Input) = input.readUZInt().toInt()

internal fun writeVarInt(output: Output, value: Int) = output.writeUZInt(value.toUInt())

internal fun sizeVarLong(value: Long) = measureUZLong(value.toULong())

internal fun readVarLong(input: Input) = input.readUZLong().toLong()

internal fun writeVarLong(output: Output, value: Long) = output.writeUZLong(value.toULong())

private val Char.isHighSurrogate get() = this >= Char.MIN_HIGH_SURROGATE && this < (Char.MAX_HIGH_SURROGATE + 1)

internal fun sizeStringChars(sequence: CharSequence): Int {
    var count = 0
    var i = 0
    val len = sequence.length
    while (i < len) {
        val ch = sequence[i]
        when {
            ch.toInt() <= 0x7F -> count++
            ch.toInt() <= 0x7FF -> count += 2
            ch.isHighSurrogate -> {
                count += 4
                ++i
            }
            else -> count += 3
        }
        i++
    }
    return count
}

internal fun sizeString(sequence: CharSequence) = sizeStringChars(sequence).let { it + sizeVarInt(it) }

internal fun readString(input: Input): String {
    val size = readVarInt(input)
    return input.readTextExactBytes(size, Charsets.UTF_8)
}

internal fun writeString(output: Output, value: String) {
    writeVarInt(output, sizeStringChars(value))
    output.writeText(value)
}

internal const val sizeBoolean = Byte.SIZE_BYTES

internal fun readBoolean(input: Input) = input.readByte().toInt() != 0

internal fun writeBoolean(output: Output, value: Boolean) {
    output.writeByte(if (value) 1 else 0)
}

internal const val sizeByte = Byte.SIZE_BYTES

internal fun readByte(input: Input) = input.readByte()

internal fun writeByte(output: Output, value: Byte) = output.writeByte(value)

internal const val sizeShort = Short.SIZE_BYTES

internal fun readShort(input: Input): Short = input.readShort()

internal fun writeShort(output: Output, value: Short) = output.writeShort(value)

internal const val sizeLong = 8

internal fun readLong(input: Input) = input.readLong()

internal fun writeLong(output: Output, value: Long) = output.writeLong(value)

@ExperimentalPaketApi
internal fun sizePath(path: Path) = path.sumBy { sizeString(it) } + 1

@ExperimentalPaketApi
internal fun readPath(input: Input): Path {
    val result = mutableListOf<String>()
    do {
        val part = readString(input)
        if (part.isEmpty()) {
            break
        }
        result += part
    } while (true)
    return Path(result)
}

@ExperimentalPaketApi
internal fun writePath(output: Output, value: Path) {
    for (segment in value)
        writeString(output, segment)
    writeByte(output, 0)
}

internal fun sizeByteArray(value: ByteArray) = value.size.let { sizeVarInt(it) + it }

internal fun readByteArray(input: Input): ByteArray {
    val size = readVarInt(input)
    return input.readBytes(size)
}

internal fun writeByteArray(output: Output, value: ByteArray) {
    val size = value.size
    writeVarInt(output, size)
    output.writeFully(value)
}

internal const val sizeInt = Int.SIZE_BYTES

internal fun readInt(input: Input): Int = input.readInt()

internal fun writeInt(output: Output, value: Int) = output.writeInt(value)

internal const val sizeFloat = Int.SIZE_BYTES

internal fun readFloat(input: Input) = input.readFloat()

internal fun writeFloat(output: Output, value: Float) = output.writeFloat(value)

internal const val sizeDouble = Long.SIZE_BYTES

internal fun readDouble(input: Input) = input.readDouble()

internal fun writeDouble(output: Output, value: Double) = output.writeDouble(value)

internal fun sizeBytes(bytes: ByteReadPacket) = bytes.remaining.toInt().let { it + sizeVarInt(it) }

internal fun readBytes(input: Input) = buildPacket {
    val size = readVarInt(input)
    // TODO: Improve later, maybe...
    repeat(size) {
        writeByte(input.readByte())
    }
}

internal fun writeBytes(output: Output, bytes: ByteReadPacket) {
    writeVarInt(output, bytes.remaining.toInt())
    bytes.copyTo(output)
}

internal fun errNulls(): Nothing = error("nulls can't be encoded with paket format")
