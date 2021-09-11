package com.handtruth.mc.util

private const val commonCodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

public enum class Base64Variants {
    RFC4648 {
        override val codes = "$commonCodes+/"
    },
    RFC4648Url {
        override val codes = "$commonCodes-_"
    },
    RFC3501 {
        override val codes = "$commonCodes+,"
    };

    internal fun code2char(code: Int): Char = codes[code]
    internal abstract val codes: String
}

public fun Appendable.appendBase64(
    data: ByteArray,
    ident: String = "",
    variant: Base64Variants = Base64Variants.RFC4648,
    lineLength: Int = -1
) {
    var state = 0
    var nextValue = 0
    var written = 0

    val nextLine = if (lineLength != Int.MAX_VALUE) {
        fun() {
            if (written % lineLength == 0) {
                append(ident)
            }
        }
    } else {
        fun() {
        }
    }

    val code2char = variant::code2char

    for (element in data) {
        val byte = element.toInt() and 0xFF
        when (state) {
            0 -> {
                ++written
                append(code2char(byte ushr 2))
                nextValue = (byte and 0x3) shl 4
            }
            1 -> {
                ++written
                append(code2char(nextValue or (byte ushr 4)))
                nextValue = (byte and 0xF) shl 2
            }
            2 -> {
                ++written
                append(code2char(nextValue or (byte ushr 6)))
                nextLine()
                ++written
                append(code2char(byte and 0x3F))
            }
        }
        state = (state + 1) % 3
        nextLine()
    }
    if (state != 0) {
        append(code2char(nextValue))
    }
    if (variant != Base64Variants.RFC3501) {
        when (state) {
            1 -> {
                ++written
                append('=')
                nextLine()
                append('=')
            }
            2 -> append('=')
        }
    }
}

public fun decodeBase64(
    value: CharSequence,
    variant: Base64Variants = Base64Variants.RFC4648
): ByteArray {
    var count = 0
    val codes = variant.codes
    for (char in value) {
        when (char) {
            in codes -> ++count
            in "\r\n\t =" -> {
                /* do nothing */
            }
            else -> error("illegal character in base 64 encoded string")
        }
    }

    val modulus = count % 4
    val size = count / 4 * 3 + modulus - (if (modulus != 0) 1 else 0)

    val data = ByteArray(size)
    var state = 0
    var nextValue = 0
    var stringPosition = 0
    var i = 0
    while (true) {
        if (i == size) {
            break
        }
        val code = when (val c = value[stringPosition++]) {
            in "\n\t\r \b" -> continue
            in '0'..'9' -> c - '0' + 52
            in 'a'..'z' -> c - 'a' + 26
            in 'A'..'Z' -> c - 'A'
            '-', '+' -> 62
            '/', ',', '_' -> 63
            else -> break
        }
        when (state) {
            0 -> nextValue = code shl 2
            1 -> {
                data[i++] = (nextValue or (code ushr 4)).toByte()
                nextValue = (code and 0xF) shl 4
            }
            2 -> {
                data[i++] = (nextValue or (code ushr 2)).toByte()
                nextValue = (code and 0x3) shl 6
            }
            3 -> data[i++] = (nextValue or code).toByte()
        }
        state = (state + 1) and 0x3
    }
    return data
}
