package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.*
import com.handtruth.mc.util.readSZInt
import com.handtruth.mc.util.readSZLong
import io.ktor.utils.io.core.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

internal class PaketDecoder(
    private val configuration: PaketFormat.Configuration,
    override val serializersModule: SerializersModule
) : AbstractDecoder() {
    lateinit var input: Input

    private val indexStack = ArrayList<Int>(1)

    private var index: Int
        get() = indexStack.last()
        set(value) {
            indexStack[indexStack.lastIndex] = value
        }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return index++
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        indexStack.add(0)
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        indexStack.removeLast()
    }

    override fun decodeSequentially() = true

    override fun decodeBoolean(): Boolean = input.readBoolean()

    override fun decodeChar(): Char = input.readChar()

    override fun decodeByte(): Byte = input.readByte()

    override fun decodeShort(): Short = input.readShort()

    override fun decodeInt(): Int = input.readSZInt()

    override fun decodeLong(): Long = input.readSZLong()

    override fun decodeFloat(): Float = input.readFloat()

    override fun decodeDouble(): Double = input.readDouble()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = input.readVarInt()

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = input.readVarInt()

    override fun decodeString(): String = input.readString()

    override fun decodeNotNullMark(): Boolean = input.readBoolean()

    override fun decodeNull() = null
}
