package com.handtruth.mc.mojang.util

import kotlinx.io.Input
import kotlinx.io.buffer.Buffer
import kotlinx.io.text.readUtf8String
import kotlinx.io.use

private class Base64DecoderInput(val value: String): Input() {
    var index = 0

    fun read(): Int {
        val i = index++
        if (i >= value.length)
            return -1
        val a = tr(value[i])
        return when (i and 3) {
            0 -> {
                val b = tr(value[index])
                (((a shl 2) or (b ushr 4)) and 0b11111111)
            }
            1 -> {
                val b = tr(value[index])
                if (b == -1)
                    index += 2
                (((a shl 4) or (b ushr 2)) and 0b11111111)
            }
            2 -> {
                val b = tr(value[index++])
                (((a shl 6) or (b)) and 0b11111111)
            }
            else -> throw IllegalStateException()
        }
    }

    private fun tr(char: Char) = when (char) {
        in 'A'..'Z' -> char - 'A'
        in 'a'..'z' -> char - 'a' + 26
        in '0'..'9' -> char - '0' + 52
        '+', '-' -> 62
        '/', '_' -> 63
        '=' -> -1
        else -> error("not Base64 symbol")
    }

    override fun closeSource() {}

    override fun fill(buffer: Buffer, startIndex: Int, endIndex: Int): Int {
        for (i in startIndex until endIndex) {
            val byte = read()
            if (byte == -1)
                return i - startIndex
            buffer.storeByteAt(i, byte.toByte())
        }
        return endIndex - startIndex
    }
}

fun base64decoder(value: String): Input = Base64DecoderInput(value)

fun decodeBase64AsString(value: String) = base64decoder(value).use { it.readUtf8String() }
