package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte
import kotlin.reflect.KProperty

class ByteTag(var byte: Byte) : MutableTag<Byte>(TagID.Byte) {
    override var value
        get() = byte
        set(value) { byte = value }
    override fun write(output: Output) {
        output.writeByte(byte)
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = byte
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Byte) {
        byte = newValue
    }
    companion object : TagResolver<Byte> {
        override fun read(input: Input) = ByteTag(input.readByte())
        override val id get() = TagID.Byte
        override fun wrap(value: Byte) = ByteTag(value)
    }
    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append(byte.toString()).append('b')
    }
}
