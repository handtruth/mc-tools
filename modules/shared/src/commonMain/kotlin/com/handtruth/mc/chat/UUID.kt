package com.handtruth.mc.minecraft

import kotlinx.serialization.*

internal const val uuidClassName = "com.handtruth.mc.minecraft.UUID"

sealed class UUIDSerializer : KSerializer<UUID> {
    object Default : UUIDSerializer() {
        override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
    }
    object GUID : UUIDSerializer() {
        override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toGUID())
    }
    object Mojang : UUIDSerializer() {
        override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toMojangUUID())
    }

    override val descriptor = PrimitiveDescriptor(uuidClassName, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) =
        UUID(decoder.decodeString())
}

private fun char2int(char: Char) = when(char) {
    in '0'..'9' -> char - '0'
    in 'a'..'f' -> char - 'a' + 10
    in 'A'..'F' -> char - 'A' + 10
    else -> error("illegal symbol \"$char\"")
}

private fun sumPart(str: String, begin: Int): Long {
    var result = 0L
    var offset = 0
    for (i in 0 until begin) {
        while (str[i + offset] == '-')
            ++offset
    }
    for (i in begin until (begin + 16)) {
        while (true) {
            val char = str[i + offset]
            if (char == '-') {
                ++offset
            } else {
                result = (result shl 4) or char2int(char).toLong()
                break
            }
        }
    }
    return result
}

private fun int2char(value: Int) = when (value) {
    in 0..9 -> value + '0'.toInt()
    in 10..15 -> value - 10 + 'a'.toInt()
    else -> error("bad value")
}.toChar()

private fun exportUUIDPart(part: Long, chars: CharArray, begin: Int, isMojang: Boolean) {
    var offset = begin
    var value = part
    for (i in 0 until 16) {
        val index = i + offset
        if (!isMojang && (index == 8 || index == 13 || index == 18 || index == 23)) {
            chars[index] = '-'
            ++offset
        }
        chars[i + offset] =
            int2char(((value ushr (64 - 4)) and 0b1111).toInt())
        value = value shl 4
    }
}

private inline val String.guidOff get() = if (get(0) == '{') 1 else 0

@Serializable(UUIDSerializer.Default::class)
data class UUID(val most: Long, val least: Long) {
    constructor() : this(0, 0)
    constructor(string: String) : this(
        sumPart(string, string.guidOff),
        sumPart(string, 16 + string.guidOff)
    ) {
        require((string[0] == '{') == (string.last() == '}')) { "malformed GUID format" }
    }

    companion object {
        val empty = UUID()
    }

    override fun toString(): String {
        val chars = CharArray(36)
        exportUUIDPart(most, chars, 0, false)
        exportUUIDPart(least, chars, 18, false)
        return String(chars)
    }

    fun toGUID(): String {
        return "{$this}"
    }

    fun toMojangUUID(): String {
        val chars = CharArray(32)
        exportUUIDPart(most, chars, 0, true)
        exportUUIDPart(least, chars, 16, true)
        return String(chars)
    }
}
