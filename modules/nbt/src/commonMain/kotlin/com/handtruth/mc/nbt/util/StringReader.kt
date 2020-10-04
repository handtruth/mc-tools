package com.handtruth.mc.nbt.util

import kotlinx.io.Closeable
import kotlinx.io.EOFException

data class Position(val line: Int, val column: Int, val offset: Int)

interface Reader : Closeable {
    fun read(): Char
    fun previous(back: Int): String
    fun take(forward: Int): String
    fun back(count: Int = 1)
    fun position(): Position
}

@Suppress("ControlFlowWithEmptyBody")
internal fun Reader.skipSpace() {
    while (read() in "\n\t\r \b") {}
    back()
}

internal class StringReader(val source: String, var offset: Int = 0, val length: Int = source.length) : Reader {
    init {
        require(length <= source.length)
        require(offset <= length)
    }

    override fun read(): Char {
        if (offset >= length) {
            throw EOFException("end of string")
        }
        return source[offset++]
    }

    override fun back(count: Int) {
        offset -= count
        check(offset >= 0) { offset += count; "out of reader buffer" }
    }

    override fun previous(back: Int): String = source.substring(offset - back, offset)

    override fun position(): Position {
        var line = 0
        var column = 0
        for (i in 0 until offset) {
            val c = source[i]
            when {
                c == '\n' && i > 0 && source[i - 1] == '\r' -> {}
                c == '\n' || c == '\r' -> {
                    ++line
                    column = 0
                }
                else -> ++column
            }
        }
        return Position(line + 1, column + 1, offset)
    }

    override fun take(forward: Int): String {
        val result = source.substring(offset, offset + forward)
        offset += forward
        return result
    }

    override fun close() {
        offset = length
    }

    override fun toString(): String {
        return "StringReader(position=${position()}, input=$source)"
    }
}
