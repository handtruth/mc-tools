package com.handtruth.mc.util

import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte
import kotlin.math.abs

/**
 * Measure encoded UZInt size of 32-bit integer value.
 * @param value integer value
 * @return size of integer value
 */
fun sizeUZInt32(value: UInt): Int {
    var integer = value
    var count = 0
    do {
        integer = integer shr 7
        ++count
    } while (integer != 0u)
    return count
}

/**
 * Read UZInt encoded integer with maximum size of 32-bit.
 * @param input input object to read from
 * @return 32-bit integer number that was read
 */
fun readUZInt32(input: Input): UInt {
    var numRead = 0
    var result = 0u
    var read: UInt
    do {
        check(numRead < 5) { "UZInt32 is too big" }
        read = input.readByte().toUInt()
        val value = read and 127u
        result = result or (value shl 7 * numRead)
        ++numRead
    } while (read and 128u != 0u)
    return result
}

/**
 * Write 32-bit integer value in UZInt format to [output].
 * @param output output object to write to
 * @param integer 32-bit integer number to write
 */
fun writeUZInt32(output: Output, integer: UInt) {
    var value = integer
    do {
        var temp = (value and 127u)
        value = value shr 7
        if (value != 0u) {
            temp = temp or 128u
        }
        output.writeByte(temp.toByte())
    } while (value != 0u)
}

/**
 * Measure encoded UZInt size of 64-bit integer value.
 * @param value integer value
 * @return size of integer value
 */
fun sizeUZInt64(value: ULong): Int {
    var integer = value
    var count = 0
    do {
        integer = integer shr 7
        ++count
    } while (integer != 0uL)
    return count
}

/**
 * Read UZInt encoded integer with maximum size of 64-bit.
 * @param input input object to read from
 * @return 64-bit integer number that was read
 */
fun readUZInt64(input: Input): ULong {
    var numRead = 0
    var result = 0uL
    var read: ULong
    do {
        check(numRead < 10) { "UZInt64 is too big" }
        read = input.readByte().toULong()
        val value = read and 127uL
        result = result or (value shl 7 * numRead)
        ++numRead
    } while (read and 128uL != 0uL)
    return result
}

/**
 * Write 64-bit integer value in UZInt format to [output].
 * @param output output object to write to
 * @param integer 64-bit integer number to write
 */
fun writeUZInt64(output: Output, integer: ULong) {
    var value = integer
    do {
        var temp = (value and 127u)
        value = value shr 7
        if (value != 0uL) {
            temp = temp or 128u
        }
        output.writeByte(temp.toByte())
    } while (value != 0uL)
}

/**
 * Measure encoded SZInt size of 32-bit integer value.
 * @param integer integer value
 * @return size of integer value
 */
fun sizeSZInt32(integer: Int): Int {
    var value = abs(integer).toUInt() shr 6
    var count = 1
    while (value != 0u) {
        value = value shr 7
        ++count
    }
    return count
}

/**
 * Read SZInt encoded integer with maximum size of 32-bit.
 * @param input input object to read from
 * @return 32-bit integer number that was read
 */
fun readSZInt32(input: Input): Int {
    var numRead = 0
    var read = input.readByte().toUInt()
    val sign = read and 1u == 1u
    var result = read and 126u shr 1
    while (read and 128u != 0u) {
        check(numRead < 4) { "SZInt32 is too big" }
        read = input.readByte().toUInt()
        val value = read and 127u
        result = result or (value shl (7 * numRead + 6))
        ++numRead
    }
    return result.toInt().let { if (sign) -it else it }
}

/**
 * Write 32-bit integer value in SZInt format to [output].
 * @param output output object to write to
 * @param integer 32-bit integer number to write
 */
fun writeSZInt32(output: Output, integer: Int) {
    val sign: UInt
    var value: UInt
    if (integer < 0) {
        sign = 1u
        value = (-integer).toUInt()
    } else {
        sign = 0u
        value = integer.toUInt()
    }
    var first = value and 63u shl 1 or sign
    value = value shr 6
    if (value != 0u) {
        first = first or 128u
    }
    output.writeByte(first.toByte())
    while (value != 0u) {
        var temp = value and 127u
        value = value shr 7
        if (value != 0u) {
            temp = temp or 128u
        }
        output.writeByte(temp.toByte())
    }
}

/**
 * Measure encoded SZInt size of 64-bit integer value.
 * @param integer integer value
 * @return size of integer value
 */
fun sizeSZInt64(integer: Long): Int {
    var value = abs(integer).toULong() shr 6
    var count = 1
    while (value != 0uL) {
        value = value shr 7
        ++count
    }
    return count
}

/**
 * Read SZInt encoded integer with maximum size of 64-bit.
 * @param input input object to read from
 * @return 64-bit integer number that was read
 */
fun readSZInt64(input: Input): Long {
    var numRead = 0
    var read = input.readByte().toULong()
    val sign = read and 1u == 1uL
    var result = read and 126u shr 1
    while (read and 128u != 0uL) {
        check(numRead < 9) { "SZInt64 is too big" }
        read = input.readByte().toULong()
        val value = read and 127u
        result = result or (value shl (7 * numRead + 6))
        ++numRead
    }
    return result.toLong().let { if (sign) -it else it }
}

/**
 * Write 64-bit integer value in SZInt format to [output].
 * @param output output object to write to
 * @param integer 64-bit integer number to write
 */
fun writeSZInt64(output: Output, integer: Long) {
    val sign: ULong
    var value: ULong
    if (integer < 0) {
        sign = 1u
        value = (-integer).toULong()
    } else {
        sign = 0u
        value = integer.toULong()
    }
    var first = value and 63u shl 1 or sign
    value = value shr 6
    if (value != 0uL) {
        first = first or 128u
    }
    output.writeByte(first.toByte())
    while (value != 0uL) {
        var temp = value and 127u
        value = value shr 7
        if (value != 0uL) {
            temp = temp or 128u
        }
        output.writeByte(temp.toByte())
    }
}
