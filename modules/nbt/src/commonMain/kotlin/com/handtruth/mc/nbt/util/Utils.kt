package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.util.*
import kotlinx.io.*
import kotlinx.io.text.readUtf8String
import kotlinx.io.text.writeUtf8String
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import kotlin.contracts.contract
import kotlin.reflect.KClass

internal fun reverse(value: Short): Short {
    val integer = value.toInt() and 0xFFFF
    return ((integer ushr 8) or (integer shl 8) and 0xFFFF).toShort()
}

internal fun reverse(value: Int): Int {
    return (value shl 24) or (value ushr 24) or (value shl 8 and 0xFF0000) or (value ushr 8 and 0xFF00)
}

internal fun reverse(value: Long): Long {
    return (value shl 56) or (value ushr 56) or (value shl 48 and 0xFF0000_00000000L) or
            (value ushr 48 and 0xFF00) or (value shl 24 and 0xFF00_00000000L) or (value ushr 24 and 0xFF0000) or
            (value shl 8 and 0xFF_00000000L) or (value ushr 8 and 0xFF000000)
}

internal fun reverse(value: Float): Float {
    return Float.fromBits(reverse(value.toRawBits()))
}

internal fun reverse(value: Double): Double {
    return Double.fromBits(reverse(value.toRawBits()))
}

internal fun readInt16(input: Input, conf: NBTBinaryConfig): Short {
    val short = input.readShort()
    return when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> short
        NBTBinaryConfig.ByteOrders.Little -> reverse(short)
    }
}

internal fun writeInt16(output: Output, conf: NBTBinaryConfig, short: Short) {
    val value = when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> short
        NBTBinaryConfig.ByteOrders.Little -> reverse(short)
    }
    output.writeShort(value)
}

internal fun readInt32(input: Input, conf: NBTBinaryConfig): Int {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> {
            val integer = input.readInt()
            when (conf.endian) {
                NBTBinaryConfig.ByteOrders.Big -> integer
                NBTBinaryConfig.ByteOrders.Little -> reverse(integer)
            }
        }
        NBTBinaryConfig.Formats.ZigZag -> readUZInt32(input).toInt()
        NBTBinaryConfig.Formats.ZInt -> readSZInt32(input)
    }
}

internal fun writeInt32(output: Output, conf: NBTBinaryConfig, integer: Int) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> {
            val value = when (conf.endian) {
                NBTBinaryConfig.ByteOrders.Big -> integer
                NBTBinaryConfig.ByteOrders.Little -> reverse(integer)
            }
            output.writeInt(value)
        }
        NBTBinaryConfig.Formats.ZigZag -> writeUZInt32(output, integer.toUInt())
        NBTBinaryConfig.Formats.ZInt -> writeSZInt32(output, integer)
    }
}

internal fun readInt64(input: Input, conf: NBTBinaryConfig): Long {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> {
            val long = input.readLong()
            when (conf.endian) {
                NBTBinaryConfig.ByteOrders.Big -> long
                NBTBinaryConfig.ByteOrders.Little -> reverse(long)
            }
        }
        NBTBinaryConfig.Formats.ZigZag -> readUZInt64(input).toLong()
        NBTBinaryConfig.Formats.ZInt -> readSZInt64(input)
    }
}

internal fun writeInt64(output: Output, conf: NBTBinaryConfig, long: Long) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> {
            val value = when (conf.endian) {
                NBTBinaryConfig.ByteOrders.Big -> long
                NBTBinaryConfig.ByteOrders.Little -> reverse(long)
            }
            output.writeLong(value)
        }
        NBTBinaryConfig.Formats.ZigZag -> writeUZInt64(output, long.toULong())
        NBTBinaryConfig.Formats.ZInt -> writeSZInt64(output, long)
    }
}

internal fun readFloat(input: Input, conf: NBTBinaryConfig): Float {
    val float = input.readFloat()
    return when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> float
        NBTBinaryConfig.ByteOrders.Little -> reverse(float)
    }
}

internal fun writeFloat(output: Output, conf: NBTBinaryConfig, float: Float) {
    val value = when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> float
        NBTBinaryConfig.ByteOrders.Little -> reverse(float)
    }
    output.writeFloat(value)
}

internal fun readDouble(input: Input, conf: NBTBinaryConfig): Double {
    val double = input.readDouble()
    return when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> double
        NBTBinaryConfig.ByteOrders.Little -> reverse(double)
    }
}

internal fun writeDouble(output: Output, conf: NBTBinaryConfig, double: Double) {
    val value = when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> double
        NBTBinaryConfig.ByteOrders.Little -> reverse(double)
    }
    output.writeDouble(value)
}

internal fun readSize(input: Input, conf: NBTBinaryConfig): Int {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> input.readInt().let {
            when (conf.endian) {
                NBTBinaryConfig.ByteOrders.Big -> it
                NBTBinaryConfig.ByteOrders.Little -> reverse(it)
            }
        }
        NBTBinaryConfig.Formats.ZigZag, NBTBinaryConfig.Formats.ZInt -> readUZInt32(input).toInt()
    }
}

internal fun writeSize(output: Output, conf: NBTBinaryConfig, size: Int) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> output.writeInt(
            when (conf.endian) {
                NBTBinaryConfig.ByteOrders.Big -> size
                NBTBinaryConfig.ByteOrders.Little -> reverse(size)
            }
        )
        NBTBinaryConfig.Formats.ZigZag, NBTBinaryConfig.Formats.ZInt -> writeUZInt32(output, size.toUInt())
    }
}

internal fun readString(input: Input, conf: NBTBinaryConfig): String {
    val size =
        if (conf.format == NBTBinaryConfig.Formats.Flat) readInt16(input, conf).toInt() else readSize(input, conf)
    val bytes = buildBytes {
        // TODO: Redo when fixed
        repeat(size) {
            writeByte(input.readByte())
        }
    }
    validate(size >= 0) { "string size is negative: $size" }
    return bytes.input().readUtf8String()
}

internal fun writeString(output: Output, conf: NBTBinaryConfig, value: String) {
    val bytes = buildBytes {
        writeUtf8String(value)
    }
    val size = bytes.size()
    if (conf.format == NBTBinaryConfig.Formats.Flat)
        writeInt16(output, conf, size.toShort())
    else
        writeSize(output, conf, size)
    bytes.input().copyTo(output)
}

internal fun quoteString(value: String) = value.replace("\"", "\\\"")

internal fun Appendable.next(level: Int, pretty: Boolean) {
    if (pretty) {
        append("\n")
        repeat(level) {
            append("    ")
        }
    }
}

internal inline fun <reified T> smartJoin(
    iter: Iterator<T>, builder: Appendable,
    prefix: String = "", suffix: String = "", postfix: String = "", delimiter: String = ",",
    level: Int = 0, pretty: Boolean = false,
    chain: Appendable.(T) -> Unit = { append(it.toString()) }
) {
    if (iter.hasNext()) {
        builder.append(prefix)
        builder.next(level + 1, pretty)
        builder.chain(iter.next())
        builder.append(suffix)
        for (each in iter) {
            builder.append(delimiter)
            builder.next(level + 1, pretty)
            builder.chain(each)
            builder.append(suffix)
        }
        builder.next(level, pretty)
        builder.append(postfix)
    } else
        builder.append(prefix).append(postfix)
}

internal inline fun <reified T> joinArray(
    iterator: Iterator<T>,
    builder: Appendable,
    conf: NBTStringConfig,
    type: Char,
    suffix: String
) {
    smartJoin(
        iterator,
        builder,
        prefix = "[$type;",
        suffix = suffix,
        postfix = "]",
        delimiter = if (conf.pretty) ", " else ","
    )
}

internal fun validate(value: Boolean, expected: KClass<*>, actual: TagID) {
    contract {
        returns() implies value
    }
    validate(value) {
        "deserialization not possible: unable to translate $actual tag to $expected type"
    }
}

internal enum class CurrentElement { Key, Value }

internal fun deduceTag(reader: Reader): TagID {
    reader.skipSpace()
    var credit = 0
    fun read(): Char {
        val c = reader.read()
        ++credit
        return c
    }
    try {
        when (read()) {
            '{' -> return TagID.Compound
            '}', ']' -> return TagID.End
            '[' -> {
                val id = when (read()) {
                    'B' -> TagID.ByteArray
                    'I' -> TagID.IntArray
                    'L' -> TagID.LongArray
                    else -> TagID.List
                }
                return if (id != TagID.List) {
                    ++credit
                    if (reader.read() != ';')
                        TagID.List
                    else
                        id
                } else
                    TagID.List
            }
            in '0'..'9', '-', '+', '.' -> {
                while (true) {
                    when (read()) {
                        in '0'..'9' -> {
                        }
                        'b' -> return TagID.Byte
                        's' -> return TagID.Short
                        'l' -> return TagID.Long
                        'f' -> return TagID.Float
                        'd' -> return TagID.Double
                        'e', 'E' -> {
                            when (read()) {
                                in '0'..'9', '-', '+' -> {
                                    while (true) {
                                        when (read()) {
                                            in '0'..'9' -> {
                                            }
                                            'f' -> return TagID.Float
                                            /*'d'*/ else -> return TagID.Double
                                        }
                                    }
                                }
                                else -> TagID.Int
                            }
                        }
                        '.' -> {
                            while (true) {
                                when (read()) {
                                    in '0'..'9' -> {
                                    }
                                    'f' -> return TagID.Float
                                    /*'d'*/ else -> return TagID.Double
                                }
                            }
                        }
                        else -> return TagID.Int
                    }
                }
            }
            '"', in 'a'..'z', in 'A'..'Z', '_' -> return TagID.String
            else -> error("unknown token")
        }
    } finally {
        reader.back(credit)
    }
}

internal fun readAnyIntString(reader: Reader, suffix: Char?): String {
    reader.skipSpace()
    var credit = 0
    fun read(): Char {
        val c = reader.read()
        ++credit
        return c
    }

    val string: String
    cycle@ while (true) {
        when (read()) {
            in '0'..'9', '-', '+' -> {
            }
            suffix -> {
                reader.back()
                string = reader.previous(credit - 1)
                reader.read()
                break@cycle
            }
            else -> {
                reader.back()
                string = reader.previous(credit - 1)
                break@cycle
            }
        }
    }
    return string
}

internal inline fun <reified T> readAnyInt(reader: Reader, suffix: Char?, parse: (String) -> T): T {
    return parse(readAnyIntString(reader, suffix))
}

internal inline fun <reified T> readArray(
    reader: Reader, type: Char,
    suffix: Char? = null, parse: (String) -> T
): List<T> {
    reader.skipSpace()
    if (reader.take(3) != "[$type;")
        error("not an array of '$type'")
    val result = mutableListOf<T>()
    cycle@ while (true) {
        reader.skipSpace()
        when (reader.read()) {
            ']' -> return result
            else -> {
                reader.back()
                result += readAnyInt(reader, suffix, parse)
                reader.skipSpace()
                when (reader.read()) {
                    ',' -> continue@cycle
                    ']' -> return result
                    else -> error("unexpected token")
                }
            }
        }
    }
}

internal val json = Json(JsonConfiguration.Stable)

internal fun readString(reader: Reader): String {
    reader.skipSpace()
    when (reader.read()) {
        '"' -> {
            var credit = 1
            cycle1@ while (true) {
                ++credit
                when (reader.read()) {
                    '\\' -> {
                        ++credit
                        reader.read()
                    }
                    '"' -> {
                        val raw = reader.previous(credit)
                        return json.parse(String.serializer(), raw)
                    }
                }
            }
        }
        in 'a'..'z', in 'A'..'Z', '_' -> {
            var credit = 1
            cycle2@ while (true) {
                ++credit
                when (reader.read()) {
                    in 'a'..'z', in 'A'..'Z', '-', '+', '_', '.', in '0'..'9' -> {
                    }
                    else -> {
                        reader.back()
                        return reader.previous(credit - 1)
                    }
                }
            }
        }
        else -> error("not a string")
    }
}

internal fun readAnyFloatingString(reader: Reader, suffix: Char): String {
    reader.skipSpace()
    var credit = 0
    fun read(): Char {
        val c = reader.read()
        ++credit
        return c
    }

    val string: String
    cycle@ while (true) {
        when (read()) {
            in '0'..'9', '-', '+', 'e', 'E', '.' -> {
            }
            suffix -> {
                reader.back()
                string = reader.previous(credit - 1)
                reader.read()
                break@cycle
            }
            else -> {
                reader.back()
                string = reader.previous(credit - 1)
                break@cycle
            }
        }
    }
    return string
}

internal inline fun <reified T> readAnyFloating(reader: Reader, suffix: Char, parse: (String) -> T): T {
    return parse(readAnyFloatingString(reader, suffix))
}

internal fun writeString(appendable: Appendable, quote: Boolean, value: String) {
    val shouldQuote = quote || value.isEmpty() ||
            value[0].let { it !in 'a'..'z' && it !in 'A'..'Z' && it != '_' } ||
            value.any { it !in 'a'..'z' && it !in 'A'..'Z' && it !in "-+_." && it !in '0'..'9' }
    if (shouldQuote)
        appendable.append(json.stringify(String.serializer(), value))
    else
        appendable.append(value)
}
