package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KClass

interface Tag<T : Any> {
    val type: KClass<T>

    fun readBinary(input: Input, conf: NBTBinaryCodec): T
    fun readText(input: Reader, conf: NBTStringCodec): T

    fun writeBinary(output: Output, conf: NBTBinaryCodec, value: T)
    fun writeText(output: Appendable, conf: NBTStringCodec, value: T, level: Int = 0)
}
