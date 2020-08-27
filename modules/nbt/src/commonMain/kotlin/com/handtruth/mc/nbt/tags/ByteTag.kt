package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.StringReader
import com.handtruth.mc.nbt.util.readAnyInt
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte
import kotlin.reflect.KProperty

class ByteTag(var byte: Byte) : MutableTag<Byte>(TagID.Byte) {
    override var value
        get() = byte
        set(value) {
            byte = value
        }

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        output.writeByte(byte)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        output.append(byte.toString()).append('b')
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = byte
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Byte) {
        byte = newValue
    }

    companion object : TagResolver<Byte> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = ByteTag(input.readByte())
        override fun readText(input: Reader, conf: NBTStringConfig) =
            ByteTag(readAnyInt(input, 'b') { it.toByte() })

        override val id get() = TagID.Byte
        override fun wrap(value: Byte) = ByteTag(value)
    }
}
