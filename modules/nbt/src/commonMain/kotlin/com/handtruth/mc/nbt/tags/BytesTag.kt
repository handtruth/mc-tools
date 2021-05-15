package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readSize
import com.handtruth.mc.nbt.util.skipSpace
import com.handtruth.mc.nbt.util.writeSize
import io.ktor.utils.io.core.*

object BytesTag : Tag<ByteArray> {
    override val type = ByteArray::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): ByteArray {
        val size = readSize(input, conf.binaryConfig)
        return input.readBytes(size)
    }

    override fun readText(input: Reader, conf: NBTStringCodec): ByteArray {
        input.skipSpace()
        check(input.read() == '(') { "bytes token expected" }
        return buildPacket {
            var state = 0
            var nextValue = 0
            val endReached: Boolean
            while (true) {
                val code = when (val c = input.read()) {
                    in "\n\t\r \b" -> continue
                    in '0'..'9' -> c - '0' + 52
                    in 'a'..'z' -> c - 'a' + 26
                    in 'A'..'Z' -> c - 'A'
                    '-', '+' -> 62
                    '/', ',', '_' -> 63
                    '=' -> {
                        endReached = false
                        break
                    }
                    ')' -> {
                        endReached = true
                        break
                    }
                    else -> error("illegal character in bytes tag token")
                }
                when (state) {
                    0 -> nextValue = code shl 2
                    1 -> {
                        writeByte((nextValue or (code ushr 4)).toByte())
                        nextValue = (code and 0xF) shl 4
                    }
                    2 -> {
                        writeByte((nextValue or (code ushr 2)).toByte())
                        nextValue = (code and 0x3) shl 6
                    }
                    3 -> writeByte((nextValue or code).toByte())
                }
                state = (state + 1) and 0x3
            }
            if (endReached) {
                if (state != 0) {
                    writeByte(nextValue.toByte())
                }
            } else {
                while (true) {
                    when (input.read()) {
                        '\n', '\t', '\r', ' ', '\b', '=' -> {
                        }
                        ')' -> break
                        else -> error("illegal character in bytes tag token")
                    }
                }
            }
        }.use { it.readBytes() }
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: ByteArray) {
        val size = value.size
        writeSize(output, conf.binaryConfig, size)
        if (size != 0) {
            output.writeFully(value)
        }
    }

    private fun code2char(code: Int, variant: NBTStringConfig.Base64Variants): Char = when (code) {
        in 0..25 -> 'A' + code
        in 26..51 -> 'a' + (code - 26)
        in 52..61 -> '0' + (code - 52)
        62 -> when (variant) {
            NBTStringConfig.Base64Variants.RFC4648, NBTStringConfig.Base64Variants.RFC3501 -> '+'
            NBTStringConfig.Base64Variants.RFC4648Url -> '-'
        }
        63 -> when (variant) {
            NBTStringConfig.Base64Variants.RFC4648 -> '/'
            NBTStringConfig.Base64Variants.RFC4648Url -> '_'
            NBTStringConfig.Base64Variants.RFC3501 -> ','
        }
        else -> error("unreachable")
    }

    private fun Input.writeBase64(output: Appendable, size: Int, config: NBTStringConfig, level: Int) {
        var state = 0
        var nextValue = 0
        output.append('(')
        var written = 0
        fun nextLine() {
            if (config.pretty && config.base64LineLength != Int.MAX_VALUE && written % config.base64LineLength == 0) {
                output.append('\n')
                repeat(level + 1) {
                    output.append(config.identString)
                }
            }
        }
        for (i in 0 until size) {
            nextLine()
            val byte = readByte().toInt() and 0xFF
            when (state) {
                0 -> {
                    ++written
                    output.append(code2char(byte ushr 2, config.base64Variant))
                    nextValue = (byte and 0x3) shl 4
                }
                1 -> {
                    ++written
                    output.append(code2char(nextValue or (byte ushr 4), config.base64Variant))
                    nextValue = (byte and 0xF) shl 2
                }
                2 -> {
                    ++written
                    output.append(code2char(nextValue or (byte ushr 6), config.base64Variant))
                    nextLine()
                    ++written
                    output.append(code2char(byte and 0x3F, config.base64Variant))
                }
            }
            state = (state + 1) % 3
        }
        if (state != 0) {
            output.append(code2char(nextValue, config.base64Variant))
        }
        if (config.base64Variant != NBTStringConfig.Base64Variants.RFC3501) {
            when (state) {
                1 -> {
                    ++written
                    output.append('=')
                    nextLine()
                    output.append('=')
                }
                2 -> output.append('=')
            }
        }
        if (config.pretty) {
            output.append('\n')
            repeat(level) {
                output.append(config.identString)
            }
        }
        output.append(')')
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: ByteArray, level: Int) {
        val size = value.size
        if (size != 0) {
            ByteReadPacket(value).use { it.writeBase64(output, size, conf.stringConfig, level) }
        } else {
            output.append("()")
        }
    }

    override fun toString() = "TAG_Bytes"
}
