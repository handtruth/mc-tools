package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt16
import com.handtruth.mc.nbt.util.writeInt16
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

data class ShortTag(var short: Short) : MutableTag<Short>(TagID.Short) {
    override var value
        get() = short
        set(value) {
            short = value
        }

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeInt16(output, conf, short)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        output.append(short.toString())
        output.append('s')
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = short
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Short) {
        short = newValue
    }

    companion object : TagResolver<Short> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = ShortTag(readInt16(input, conf))
        override fun readText(input: Reader, conf: NBTStringConfig) =
            ShortTag(readAnyInt(input, 's') { it.toShort() })
        override val id get() = TagID.Short
        override fun wrap(value: Short) = ShortTag(value)
    }
}
