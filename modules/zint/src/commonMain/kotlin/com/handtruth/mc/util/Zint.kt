package com.handtruth.mc.util

import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte
import kotlin.math.abs

fun sizeUZInt32(value: UInt): Int {
    var integer = value
    var count = 0
    do {
        integer = integer shr 7
        ++count
    } while (integer != 0u)
    return count
}

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

fun writeUZInt32(output: Output, integer: UInt) {
    var value = integer
    do {
        var temp = (value and 127u)
        value = value shr 7
        if (value != 0u)
            temp = temp or 128u
        output.writeByte(temp.toByte())
    } while (value != 0u)
}

fun sizeUZInt64(value: ULong): Int {
    var integer = value
    var count = 0
    do {
        integer = integer shr 7
        ++count
    } while (integer != 0uL)
    return count
}

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

fun sizeSZInt32(integer: Int): Int {
    var value = abs(integer).toUInt() shr 6
    var count = 1
    while (value != 0u) {
        value = value shr 7
        ++count
    }
    return count
}

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
    if (value != 0u)
        first = first or 128u
    output.writeByte(first.toByte())
    while (value != 0u) {
        var temp = value and 127u
        value = value shr 7
        if (value != 0u)
            temp = temp or 128u
        output.writeByte(temp.toByte())
    }
}

fun sizeSZInt64(integer: Long): Int {
    var value = abs(integer).toULong() shr 6
    var count = 1
    while (value != 0uL) {
        value = value shr 7
        ++count
    }
    return count
}

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
    if (value != 0uL)
        first = first or 128u
    output.writeByte(first.toByte())
    while (value != 0uL) {
        var temp = value and 127u
        value = value shr 7
        if (value != 0uL)
            temp = temp or 128u
        output.writeByte(temp.toByte())
    }
}
