package com.handtruth.mc.nbt

import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream

fun NBTBinaryCodec.read(input: InputStream) = readNamedBinary(input.asInput())
fun NBTBinaryCodec.write(output: OutputStream, key: String, value: Any) = writeNamedBinary(output.asOutput(), key, value)

private class KtorInputStream(val input: ByteReadPacket) : InputStream() {
    override fun available(): Int = input.remaining.toInt()

    override fun read(): Int = if (input.endOfInput) -1 else input.readByte().toInt() and 0xFF

    override fun read(b: ByteArray): Int {
        return input.readAvailable(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return input.readAvailable(b, off, len)
    }

    override fun skip(n: Long): Long {
        return input.discard(n)
    }

    override fun close() {
        input.close()
    }
}

fun Input.asNBTInput(): Input {
    val magic = readShort()
    val oldInput = this
    val newInput = buildPacket {
        writeShort(magic)
        oldInput.copyTo(this)
    }
    close()
    if (magic == 0x1f8b.toShort()) {
        return GZIPInputStream(KtorInputStream(newInput)).asInput()
    }
    return newInput
}

fun InputStream.asNBTInput() = asInput().asNBTInput()

fun NBTBinaryCodec.read(file: File) = readNamedBinary(file.inputStream().asInput().asNBTInput())
