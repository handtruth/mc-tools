package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.*
import com.handtruth.mc.nbt.tags.*
import com.handtruth.mc.util.*
import kotlinx.datetime.*
import kotlinx.io.*
import kotlinx.io.text.readUtf8String
import kotlinx.io.text.writeUtf8String
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
    var result = (value shl 56) or (value ushr 56) or (value shl 48 and 0xFF0000_00000000L)
    result = result or (value ushr 48 and 0xFF00) or (value shl 24 and 0xFF00_00000000L)
    result = result or (value ushr 24 and 0xFF0000) or (value shl 8 and 0xFF_00000000L)
    return result or (value ushr 8 and 0xFF000000)
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

internal fun readFlatInt32(input: Input, conf: NBTBinaryConfig): Int {
    val integer = input.readInt()
    return when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> integer
        NBTBinaryConfig.ByteOrders.Little -> reverse(integer)
    }
}

internal fun readInt32(input: Input, conf: NBTBinaryConfig): Int {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> readFlatInt32(input, conf)
        NBTBinaryConfig.Formats.ZigZag -> readUZInt32(input).toInt()
        NBTBinaryConfig.Formats.ZInt -> readSZInt32(input)
    }
}

internal fun readUInt32(input: Input, conf: NBTBinaryConfig): UInt {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> readFlatInt32(input, conf).toUInt()
        NBTBinaryConfig.Formats.ZigZag, NBTBinaryConfig.Formats.ZInt -> readUZInt32(input)
    }
}

internal fun writeFlatInt32(output: Output, conf: NBTBinaryConfig, integer: Int) {
    val value = when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> integer
        NBTBinaryConfig.ByteOrders.Little -> reverse(integer)
    }
    output.writeInt(value)
}

internal fun writeInt32(output: Output, conf: NBTBinaryConfig, value: Int) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> writeFlatInt32(output, conf, value)
        NBTBinaryConfig.Formats.ZigZag -> writeUZInt32(output, value.toUInt())
        NBTBinaryConfig.Formats.ZInt -> writeSZInt32(output, value)
    }
}

internal fun writeUInt32(output: Output, conf: NBTBinaryConfig, value: UInt) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> writeFlatInt32(output, conf, value.toInt())
        NBTBinaryConfig.Formats.ZigZag, NBTBinaryConfig.Formats.ZInt -> writeUZInt32(output, value)
    }
}

internal fun readFlatInt64(input: Input, conf: NBTBinaryConfig): Long {
    val long = input.readLong()
    return when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> long
        NBTBinaryConfig.ByteOrders.Little -> reverse(long)
    }
}

internal fun readInt64(input: Input, conf: NBTBinaryConfig): Long {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> readFlatInt64(input, conf)
        NBTBinaryConfig.Formats.ZigZag -> readUZInt64(input).toLong()
        NBTBinaryConfig.Formats.ZInt -> readSZInt64(input)
    }
}

internal fun readUInt64(input: Input, conf: NBTBinaryConfig): ULong {
    return when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> readFlatInt64(input, conf).toULong()
        NBTBinaryConfig.Formats.ZigZag, NBTBinaryConfig.Formats.ZInt -> readUZInt64(input)
    }
}

internal fun writeFlatInt64(output: Output, conf: NBTBinaryConfig, long: Long) {
    val value = when (conf.endian) {
        NBTBinaryConfig.ByteOrders.Big -> long
        NBTBinaryConfig.ByteOrders.Little -> reverse(long)
    }
    output.writeLong(value)
}

internal fun writeInt64(output: Output, conf: NBTBinaryConfig, long: Long) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> writeFlatInt64(output, conf, long)
        NBTBinaryConfig.Formats.ZigZag -> writeUZInt64(output, long.toULong())
        NBTBinaryConfig.Formats.ZInt -> writeSZInt64(output, long)
    }
}

internal fun writeUInt64(output: Output, conf: NBTBinaryConfig, long: ULong) {
    when (conf.format) {
        NBTBinaryConfig.Formats.Flat -> writeFlatInt64(output, conf, long.toLong())
        NBTBinaryConfig.Formats.ZigZag, NBTBinaryConfig.Formats.ZInt -> writeUZInt64(output, long)
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
    if (conf.format == NBTBinaryConfig.Formats.Flat) {
        writeInt16(output, conf, size.toShort())
    } else {
        writeSize(output, conf, size)
    }
    bytes.input().copyTo(output)
}

internal fun Appendable.next(level: Int, pretty: Boolean, identString: String) {
    if (pretty) {
        append("\n")
        repeat(level) {
            append(identString)
        }
    }
}

internal inline fun <reified T> smartJoin(
    iter: Iterator<T>,
    builder: Appendable,
    prefix: String = "",
    suffix: String = "",
    postfix: String = "",
    delimiter: String = ",",
    level: Int = 0,
    pretty: Boolean = false,
    identString: String = "",
    chain: Appendable.(T) -> Unit = { append(it.toString()) }
) {
    if (iter.hasNext()) {
        builder.append(prefix)
        builder.next(level + 1, pretty, identString)
        builder.chain(iter.next())
        builder.append(suffix)
        for (each in iter) {
            builder.append(delimiter)
            builder.next(level + 1, pretty, identString)
            builder.chain(each)
            builder.append(suffix)
        }
        builder.next(level, pretty, identString)
        builder.append(postfix)
    } else {
        builder.append(prefix).append(postfix)
    }
}

internal inline fun <reified T> joinArray(
    iterator: Iterator<T>,
    builder: Appendable,
    conf: NBTStringConfig,
    type: String,
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

internal fun notValid(expected: KClass<*>, actual: Any): Nothing =
    throw NBTException("deserialization not possible: unable to translate $actual tag to $expected type")

internal fun validate(value: Boolean, expected: KClass<*>, actual: Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        notValid(expected, actual)
    }
}

internal enum class CurrentElement { Key, Value }

internal fun deduceTag(reader: Reader, conf: NBTStringCodec): Tag<*> {
    reader.skipSpace()
    var credit = 0
    fun read(): Char {
        val c = reader.read()
        ++credit
        return c
    }

    fun back() {
        reader.back()
        --credit
    }

    fun booleanCheck(reminder: String): Tag<*> {
        if (BooleanTag !in conf.tagsModule) {
            return StringTag
        }
        for (c in reminder) {
            if (c != read()) {
                return StringTag
            }
        }
        val next = read()
        return if (next !in 'a'..'z' && next !in 'A'..'Z' && next != '_' && next != '-' && next !in '0'..'9') {
            BooleanTag
        } else {
            StringTag
        }
    }
    try {
        when (read()) {
            '{' -> {
                if (UUIDTag in conf.tagsModule) {
                    back()
                    if (isUUIDToken(reader)) {
                        return UUIDTag
                    }
                }
                return CompoundTag
            }
            '}', ']' -> return EndTag
            '[' -> {
                val tag = when (read()) {
                    'b' -> if (BooleanArrayTag in conf.tagsModule) BooleanArrayTag else ListTag
                    'B' -> if (ByteArrayTag in conf.tagsModule) ByteArrayTag else ListTag
                    'S' -> if (ShortArrayTag in conf.tagsModule) ShortArrayTag else ListTag
                    'I' -> if (IntArrayTag in conf.tagsModule) IntArrayTag else ListTag
                    'L' -> if (LongArrayTag in conf.tagsModule) LongArrayTag else ListTag
                    else -> ListTag
                }
                return if (tag != ListTag) {
                    ++credit
                    if (reader.read() != ';') ListTag else tag
                } else {
                    ListTag
                }
            }
            in '0'..'9', '-', '+', '.' -> {
                if (InstantTag in conf.tagsModule) {
                    back()
                    if (readTime(reader, conf.stringConfig, false) != null) {
                        return InstantTag
                    }
                    read()
                }
                while (true) {
                    when (read()) {
                        in '0'..'9' -> {
                        }
                        'u' -> {
                            return when (read()) {
                                'b' -> if (UByteTag in conf.tagsModule) UByteTag else IntTag
                                's' -> if (UShortTag in conf.tagsModule) UShortTag else IntTag
                                'l' -> if (ULongTag in conf.tagsModule) ULongTag else IntTag
                                else -> if (UIntTag in conf.tagsModule) UIntTag else IntTag
                            }
                        }
                        'b' -> return ByteTag
                        's' -> return ShortTag
                        'l' -> return LongTag
                        'f' -> return FloatTag
                        'd' -> return DoubleTag
                        'e', 'E' -> {
                            when (read()) {
                                in '0'..'9', '-', '+' -> {
                                    while (true) {
                                        when (read()) {
                                            in '0'..'9' -> {
                                            }
                                            'f' -> return FloatTag
                                            /*'d'*/ else -> return DoubleTag
                                        }
                                    }
                                }
                                else -> error("unknown token")
                            }
                        }
                        '.' -> {
                            while (true) {
                                when (read()) {
                                    in '0'..'9' -> {
                                    }
                                    'f' -> return FloatTag
                                    /*'d'*/ else -> return DoubleTag
                                }
                            }
                        }
                        else -> return IntTag
                    }
                }
            }
            't' -> return booleanCheck("rue")
            'f' -> return booleanCheck("alse")
            '\'' -> if (CharTag in conf.tagsModule) return CharTag
            '"', in 'a'..'z', in 'A'..'Z', '_' -> return StringTag
            '(' -> if (BytesTag in conf.tagsModule) return BytesTag
        }
    } finally {
        reader.back(credit)
    }
    error("unknown token")
}

internal fun readAnyIntString(reader: Reader, suffix: Char?, unsigned: Boolean = false): String {
    reader.skipSpace()
    var credit = 0
    fun read(): Char {
        val c = reader.read()
        ++credit
        return c
    }

    val string: String
    if (unsigned) {
        while (true) {
            when (read()) {
                in '0'..'9', '-', '+' -> {
                }
                'u' -> {
                    val next = read()
                    reader.back(2)
                    if (next == suffix) {
                        string = reader.previous(credit - 2)
                        reader.read()
                        reader.read()
                        break
                    } else {
                        string = reader.previous(credit - 2)
                        reader.read()
                        break
                    }
                }
                else -> {
                    reader.back()
                    string = reader.previous(credit - 1)
                    break
                }
            }
        }
    } else {
        while (true) {
            when (read()) {
                in '0'..'9', '-', '+' -> {
                }
                suffix -> {
                    reader.back()
                    string = reader.previous(credit - 1)
                    reader.read()
                    break
                }
                else -> {
                    reader.back()
                    string = reader.previous(credit - 1)
                    break
                }
            }
        }
    }
    return string
}

internal inline fun <reified T> readAnyInt(
    reader: Reader,
    suffix: Char?,
    unsigned: Boolean = false,
    parse: (String) -> T
): T {
    return parse(readAnyIntString(reader, suffix, unsigned))
}

internal inline fun <reified T> readArray(
    reader: Reader,
    type: Char,
    suffix: Char? = null,
    unsigned: Boolean = false,
    parse: (String) -> T
): List<T> {
    reader.skipSpace()
    if (unsigned) {
        if (reader.take(4) != "[U$type;") {
            error("not an array of 'U$type'")
        }
    } else {
        if (reader.take(3) != "[$type;") {
            error("not an array of '$type'")
        }
    }
    val result = mutableListOf<T>()
    while (true) {
        reader.skipSpace()
        return when (reader.read()) {
            ']' -> result
            else -> {
                reader.back()
                result += readAnyInt(reader, suffix, unsigned, parse)
                reader.skipSpace()
                when (reader.read()) {
                    ',' -> continue
                    ']' -> result
                    else -> error("unexpected token")
                }
            }
        }
    }
}

internal fun readJsonString(raw: String): String {
    var i = 1
    var j = 0
    return buildString {
        while (true) {
            if (j + 1 == raw.length) {
                append(raw.substring(i, j))
                break
            }
            if (raw[j] == '\\') {
                val k = j + 1
                if (k + 1 == raw.length) {
                    append(raw.substring(i, k))
                    break
                }
                append(raw.substring(i, j))
                val (toAppend, index) = readEscaped(raw, k)
                i = index
                append(toAppend)
                j = i - 1
            }
            j++
        }
    }
}

internal fun readEscaped(string: String, start: Int): Pair<Char, Int> {
    return when (val c = string[start]) {
        '"', '\\', '/' -> c to (start + 1)
        'b' -> '\b' to (start + 1)
        'f' -> 12.toChar() to (start + 1)
        'n' -> '\n' to (start + 1)
        'r' -> '\r' to (start + 1)
        't' -> '\t' to (start + 1)
        'u' -> {
            var l = start + 1
            for (m in 0..3) {
                if (l == string.length) {
                    break
                }
                val a = string[l]
                if (a !in '0'..'9' && a !in 'a'..'f' && a !in 'A'..'F') {
                    break
                }
                l++
            }
            string.substring(start + 1, l).toInt(16).toChar() to l
        }
        else -> '\\' to start
    }
}

internal fun readChar(reader: Reader): Char {
    reader.skipSpace()
    check(reader.read() == '\'') { "not a character token" }
    var credit = 0
    while (true) {
        ++credit
        when (reader.read()) {
            '\\' -> {
                ++credit
                reader.read()
            }
            '\'' -> {
                reader.back()
                val raw = reader.previous(credit - 1)
                reader.read()
                val c = raw[0]
                return if (c == '\\') readEscaped(raw, 1).first else c
            }
        }
    }
}

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
                        return readJsonString(raw)
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

internal fun writeJsonString(appendable: Appendable, string: String) {
    appendable.append('"')
    var i = 0
    var j = 0
    fun escape(char: Char) {
        appendable.append(string.substring(i, j))
        appendable.append('\\')
        appendable.append(char)
        i = j + 1
    }
    while (true) {
        if (j == string.length) {
            appendable.append(string.substring(i, j))
            break
        }
        when (string[j]) {
            12.toChar() -> escape('f')
            '\\' -> escape('\\')
            '\b' -> escape('b')
            '"' -> escape('"')
            '\n' -> escape('n')
            '\r' -> escape('r')
        }
        j++
    }
    appendable.append('"')
}

internal fun writeString(appendable: Appendable, quote: Boolean, value: String, conf: NBTStringCodec) {
    var shouldQuote = quote || value.isEmpty()
    shouldQuote = shouldQuote || (BooleanTag in conf.tagsModule && (value == "true" || value == "false"))
    shouldQuote = shouldQuote || value[0].let { it !in 'a'..'z' && it !in 'A'..'Z' && it != '_' }
    shouldQuote = shouldQuote || value.any { it !in 'a'..'z' && it !in 'A'..'Z' && it !in "-+_." && it !in '0'..'9' }
    if (shouldQuote) {
        writeJsonString(appendable, value)
    } else {
        appendable.append(value)
    }
}

internal fun readTime(reader: Reader, conf: NBTStringConfig, consume: Boolean): Instant? {
    var credit = 0
    try {
        while (true) {
            val c = reader.read()
            ++credit
            if (c !in "0123456789WZT+-.:") break
        }
        reader.back()
        val timeString = reader.previous(credit - 1)
        try {
            return Instant.parse(timeString)
        } catch (e: Exception) {
        }
        try {
            return LocalDateTime.parse(timeString).toInstant(conf.timeZone)
        } catch (e: Exception) {
        }
        try {
            return LocalDate.parse(timeString).atStartOfDayIn(conf.timeZone)
        } catch (e: Exception) {
        }
        return null
    } finally {
        if (!consume) {
            reader.back(credit - 1)
        }
    }
}

internal fun isUUIDToken(reader: Reader): Boolean {
    var credit = 0
    fun read(): Char {
        val c = reader.read()
        ++credit
        return c
    }

    fun checkNext(count: Int): Boolean {
        repeat(count) {
            val char = read()
            if (char !in '0'..'9' && char !in 'a'..'f' && char !in 'A'..'F') {
                return false
            }
        }
        return true
    }
    try {
        if (read() != '{') {
            return false
        }
        if (!checkNext(8)) {
            return false
        }
        repeat(3) {
            if (read() != '-') {
                return false
            }
            if (!checkNext(4)) {
                return false
            }
        }
        if (read() != '-') {
            return false
        }
        if (!checkNext(12)) {
            return false
        }
        return read() == '}'
    } finally {
        reader.back(credit)
    }
}

internal inline fun compressBooleansRead(input: Input, size: Int, insert: (Int, Boolean) -> Unit) {
    val fullBytesCount = size ushr 3
    val reminder = size and 7
    var offset = 0
    for (i in 0 until fullBytesCount) {
        val byte = input.readByte().toInt()
        for (j in (0 until 8).reversed()) {
            insert(offset++, byte ushr j and 1 == 1)
        }
    }
    if (reminder != 0) {
        val byte = input.readByte().toInt()
        for (j in ((7 - reminder) until 7).reversed()) {
            insert(offset++, byte ushr j and 1 == 1)
        }
    }
}

internal inline fun compressBooleansWrite(output: Output, size: Int, get: (Int) -> Boolean) {
    val fullBytesCount = size ushr 3
    val reminder = size and 7
    var offset = 0
    for (i in 0 until fullBytesCount) {
        var byte = 0
        for (j in 0 until 8) {
            byte = byte shl 1 or (if (get(offset++)) 1 else 0)
        }
        output.writeByte(byte.toByte())
    }
    if (reminder != 0) {
        var byte = 0
        for (j in 0 until reminder) {
            byte = byte shl 1 or (if (get(offset++)) 1 else 0)
        }
        byte = byte shl (7 - reminder)
        output.writeByte(byte.toByte())
    }
}
