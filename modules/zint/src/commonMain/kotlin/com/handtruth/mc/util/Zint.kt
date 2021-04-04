package com.handtruth.mc.util

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

private fun toZigZag(value: Int): UInt {
    return ((value shl 1) xor (value shr 31)).toUInt()
}

private fun toZigZag(value: Long): ULong {
    return ((value shl 1) xor (value shr 63)).toULong()
}

private fun fromZigZag(value: UInt): Int {
    return (value shr 1).toInt() xor (-(value and 1u).toInt())
}

private fun fromZigZag(value: ULong): Long {
    return (value shr 1).toLong() xor (-(value and 1u).toLong())
}

/**
 * Measure encoded LEB128 size of 32-bit integer value.
 * @param value integer value
 * @return size of encoded integer value
 */
public fun measureUZInt(value: UInt): Int {
    var integer = value
    var count = 0
    do {
        integer = integer shr 7
        ++count
    } while (integer != 0u)
    return count
}

private inline fun readUZInt(readByte: () -> Byte): UInt {
    var numRead = 0
    var result = 0u
    var read: UInt
    do {
        check(numRead < 5) { "LEB128 is too big (bigger than 32 bit)" }
        read = readByte().toUInt()
        val value = read and 127u
        result = result or (value shl 7 * numRead)
        ++numRead
    } while (read and 128u != 0u)
    return result
}

private inline fun writeUZInt(value: UInt, writeByte: (Byte) -> Unit) {
    var mutable = value
    do {
        var temp = (mutable and 127u)
        mutable = mutable shr 7
        if (mutable != 0u) {
            temp = temp or 128u
        }
        writeByte(temp.toByte())
    } while (mutable != 0u)
}

/**
 * Read LEB128 encoded integer with maximum size of 32-bit.
 * @receiver input object to read from
 * @return 32-bit integer number that was read
 */
public fun Input.readUZInt(): UInt = readUZInt(this::readByte)

/**
 * Write 32-bit integer value in LEB128 format to [this].
 * @receiver output object to write to
 * @param value 32-bit integer number to write
 */
public fun Output.writeUZInt(value: UInt): Unit = writeUZInt(value, this::writeByte)

public suspend fun ByteReadChannel.readUZInt(): UInt = readUZInt { readByte() }

public suspend fun ByteWriteChannel.writeUZInt(value: UInt): Unit = writeUZInt(value) { writeByte(it) }

/**
 * Measure encoded LEB128 size of 64-bit integer value.
 * @param value integer value
 * @return size of encoded integer value
 */
public fun measureUZLong(value: ULong): Int {
    var integer = value
    var count = 0
    do {
        integer = integer shr 7
        ++count
    } while (integer != 0uL)
    return count
}

private inline fun readUZLong(readByte: () -> Byte): ULong {
    var numRead = 0
    var result = 0uL
    var read: ULong
    do {
        check(numRead < 10) { "LEB128 is too big (bigger than 64 bit)" }
        read = readByte().toULong()
        val value = read and 127uL
        result = result or (value shl 7 * numRead)
        ++numRead
    } while (read and 128uL != 0uL)
    return result
}

private inline fun writeUZLong(value: ULong, writeByte: (Byte) -> Unit) {
    var mutable = value
    do {
        var temp = (mutable and 127u)
        mutable = mutable shr 7
        if (mutable != 0uL) {
            temp = temp or 128u
        }
        writeByte(temp.toByte())
    } while (mutable != 0uL)
}

/**
 * Read LEB128 encoded integer with maximum size of 64-bit.
 * @receiver input object to read from
 * @return 64-bit integer number that was read
 */
public fun Input.readUZLong(): ULong = readUZLong(this::readByte)

/**
 * Write 64-bit integer value in LEB128 format to [this].
 * @receiver output object to write to
 * @param value 64-bit integer number to write
 */
public fun Output.writeUZLong(value: ULong): Unit = writeUZLong(value, this::writeByte)

public suspend fun ByteReadChannel.readUZLong(): ULong = readUZLong { readByte() }

public suspend fun ByteWriteChannel.writeUZLong(value: ULong): Unit = writeUZLong(value) { writeByte(it) }

/**
 * Measure encoded ZigZag+LEB128 size of 32-bit integer value.
 * @param value integer value
 * @return size of encoded integer value
 */
public fun measureSZInt(value: Int): Int = measureUZInt(toZigZag(value))

/**
 * Read ZigZag+LEB128 encoded integer with maximum size of 32-bit.
 * @receiver input object to read from
 * @return 32-bit integer number that was read
 */
public fun Input.readSZInt(): Int = fromZigZag(readUZInt())

/**
 * Write 32-bit integer value in ZigZag+LEB128 format to [this].
 * @receiver output object to write to
 * @param value 32-bit integer number to write
 */
public fun Output.writeSZInt(value: Int): Unit = writeUZInt(toZigZag(value))

public suspend fun ByteReadChannel.readSZInt(): Int = fromZigZag(readUZInt())

public suspend fun ByteWriteChannel.writeSZInt(value: Int): Unit = writeUZInt(toZigZag(value))

/**
 * Measure encoded ZigZag+LEB128 size of 64-bit integer value.
 * @param value integer value
 * @return size of encoded integer value
 */
public fun measureSZLong(value: Long): Int = measureUZLong(toZigZag(value))

/**
 * Read ZigZag+LEB128 encoded integer with maximum size of 64-bit.
 * @receiver input object to read from
 * @return 64-bit integer number that was read
 */
public fun Input.readSZLong(): Long = fromZigZag(readUZLong())

/**
 * Write 64-bit integer value in ZigZag+LEB128 format to [this].
 * @receiver output object to write to
 * @param value 64-bit integer number to write
 */
public fun Output.writeSZLong(value: Long): Unit = writeUZLong(toZigZag(value))

public suspend fun ByteReadChannel.readSZLong(): Long = fromZigZag(readUZLong())

public suspend fun ByteWriteChannel.writeSZLong(value: Long): Unit = writeUZLong(toZigZag(value))
