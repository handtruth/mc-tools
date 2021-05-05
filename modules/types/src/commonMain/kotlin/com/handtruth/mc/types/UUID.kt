package com.handtruth.mc.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal const val uuidClassName = "com.handtruth.mc.types.UUID"

public class MalformedUUIDException(message: String) : IllegalArgumentException(message)

internal fun char2hex(char: Char, i: Int) = when (char) {
    in '0'..'9' -> char - '0'
    in 'a'..'f' -> char - 'a' + 10
    in 'A'..'F' -> char - 'A' + 10
    else -> throw MalformedUUIDException("illegal symbol \"$char\" at position $i")
}

private fun sumPart(str: CharSequence, begin: Int): Long {
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
                result = (result shl 4) or char2hex(char, i + offset).toLong()
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

@Serializable(UUID.Serializer.Default::class)
public data class UUID(val most: Long, val least: Long) {
    public companion object {
        public val nil: UUID = UUID(0, 0)

        public fun parse(string: CharSequence, variant: Variants = Variants.Any): UUID = when (variant) {
            Variants.Any -> parseAny(string)
            Variants.Default -> parseDefault(string)
            Variants.GUID -> parseGUID(string)
            Variants.Mojang -> parseMojangUUID(string)
        }

        public fun parseAny(string: CharSequence): UUID {
            if (string.length < 32) {
                throw MalformedUUIDException("UUID token size should be at least 32 chars, got ${string.length}")
            }
            if ((string.first() == '{') != (string.last() == '}')) {
                throw MalformedUUIDException("malformed GUID format")
            }
            val guidOffset = if (string[0] == '{') 1 else 0
            val most = sumPart(string, guidOffset)
            val least = sumPart(string, 16 + guidOffset)
            return UUID(most, least)
        }

        public fun parseDefault(string: CharSequence): UUID {
            if (string.length != 36) {
                throw MalformedUUIDException("UUID token size should be 36 chars, got ${string.length}")
            }
            var most = 0L
            var least = 0L
            for (i in string.indices) {
                val char = string[i]
                when (i) {
                    8, 13, 18, 23 -> if (char != '-') {
                        throw MalformedUUIDException("illegal symbol at position $i")
                    }
                    in 0..18 -> most = (most shl 4) + char2hex(char, i)
                    else -> least = (least shl 4) + char2hex(char, i)
                }
            }
            return UUID(most, least)
        }

        public fun parseGUID(string: CharSequence): UUID {
            if (string.length != 38) {
                throw MalformedUUIDException("GUID token size should be 38 chars, got ${string.length}")
            }
            if (string.first() != '{' || string.last() != '}') {
                throw MalformedUUIDException("malformed GUID format")
            }
            return parseDefault(string.subSequence(1, string.length - 1))
        }

        private fun readPart(string: CharSequence, start: Int): Long {
            var value = 0L
            for (i in start until (16 + start)) {
                value = (value shl 4) + char2hex(string[i], i)
            }
            return value
        }

        public fun parseMojangUUID(string: CharSequence): UUID {
            if (string.length != 32) {
                throw MalformedUUIDException("Mojang UUID format should be 32 chars, got ${string.length}")
            }
            val most = readPart(string, 0)
            val least = readPart(string, 16)
            return UUID(most, least)
        }
    }

    public enum class Variants {
        Any, Default, GUID, Mojang
    }

    override fun toString(): String {
        val chars = CharArray(36)
        exportUUIDPart(most, chars, 0, false)
        exportUUIDPart(least, chars, 18, false)
        return chars.concatToString()
    }

    public fun toString(variant: Variants): String = when (variant) {
        Variants.Any, Variants.Default -> toString()
        Variants.GUID -> toGUID()
        Variants.Mojang -> toMojangUUID()
    }

    public fun toGUID(): String {
        return "{$this}"
    }

    public fun toMojangUUID(): String {
        val chars = CharArray(32)
        exportUUIDPart(most, chars, 0, true)
        exportUUIDPart(least, chars, 16, true)
        return chars.concatToString()
    }

    public sealed class Serializer : KSerializer<UUID> {
        public abstract val variant: Variants

        public object Any : Serializer() {
            override val variant: Variants get() = Variants.Any
            override fun deserialize(decoder: Decoder): UUID = parse(decoder.decodeString(), Variants.Any)
            override fun serialize(encoder: Encoder, value: UUID): Unit =
                encoder.encodeString(value.toString(Variants.Any))
        }

        public object Default : Serializer() {
            override val variant: Variants get() = Variants.Default
            override fun deserialize(decoder: Decoder): UUID = parse(decoder.decodeString(), Variants.Default)
            override fun serialize(encoder: Encoder, value: UUID): Unit =
                encoder.encodeString(value.toString(Variants.Default))
        }

        public object GUID : Serializer() {
            override val variant: Variants get() = Variants.GUID
            override fun deserialize(decoder: Decoder): UUID = parse(decoder.decodeString(), Variants.GUID)
            override fun serialize(encoder: Encoder, value: UUID): Unit = encoder.encodeString(value.toGUID())
        }

        public object Mojang : Serializer() {
            override val variant: Variants get() = Variants.Mojang
            override fun deserialize(decoder: Decoder): UUID = parse(decoder.decodeString(), Variants.Mojang)
            override fun serialize(encoder: Encoder, value: UUID): Unit = encoder.encodeString(value.toMojangUUID())
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(uuidClassName, PrimitiveKind.STRING)
    }
}
