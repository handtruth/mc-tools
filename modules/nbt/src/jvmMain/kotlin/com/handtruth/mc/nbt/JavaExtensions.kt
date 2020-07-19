package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import kotlinx.io.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream

fun NBT.read(input: InputStream) = read(input.asInput())
fun NBT.write(output: OutputStream, root: CompoundTag) = write(output.asOutput(), root)

fun Input.asNBTInput(): Input {
    val magic = preview {
        readShort()
    }
    if (magic.toInt() == 0x1f8b)
        return GZIPInputStream(this.asInputStream()).asInput()
    return this
}

fun InputStream.asNBTInput() = asInput().asNBTInput()

fun NBT.read(file: File) = read(file.inputStream().asInput().asNBTInput())
