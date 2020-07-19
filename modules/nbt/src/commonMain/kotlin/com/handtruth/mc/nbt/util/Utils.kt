package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.TagID
import kotlinx.io.*
import kotlinx.io.text.readUtf8String
import kotlinx.io.text.writeUtf8String
import kotlin.contracts.contract
import kotlin.reflect.KClass

fun readString(input: Input): String {
    val size = input.readShort().toInt()
    val bytes = buildBytes {
        input.copyTo(this, size)
    }
    validate(size >= 0) { "string size is negative: $size" }
    return bytes.input().readUtf8String()
}

fun writeString(output: Output, value: String) {
    val bytes = buildBytes {
        writeUtf8String(value)
    }
    output.writeShort(bytes.size().toShort())
    bytes.input().copyTo(output)
}

fun quoteString(value: String) = value.replace("\"", "\\\"")

internal fun Appendable.next(level: Int, pretty: Boolean) {
    if (pretty) {
        append("\n")
        repeat(level) {
            append("    ")
        }
    }
}

internal inline fun <T> smartJoin(iter: Iterator<T>, builder: Appendable,
                                  prefix: String = "", suffix: String = "", postfix: String = "",
                                  level: Int = 0, pretty: Boolean = false,
                                  chain: Appendable.(T) -> Unit = { append(it.toString()) }) {
    if (iter.hasNext()) {
        builder.append(prefix)
        builder.next(level + 1, pretty)
        builder.chain(iter.next())
        builder.append(suffix)
        for (each in iter) {
            builder.append(',')
            builder.next(level + 1, pretty)
            builder.chain(each)
            builder.append(suffix)
        }
        builder.next(level, pretty)
        builder.append(postfix)
    } else
        builder.append(prefix).append(postfix)
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
