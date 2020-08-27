package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt32
import com.handtruth.mc.nbt.util.writeInt32
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

class IntTag(var integer: Int) : MutableTag<Int>(TagID.Int) {
    override var value
        get() = integer
        set(value) {
            integer = value
        }

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeInt32(output, conf, integer)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        output.append(integer.toString())
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = integer
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Int) {
        integer = newValue
    }

    companion object : TagResolver<Int> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = IntTag(readInt32(input, conf))
        override fun readText(input: Reader, conf: NBTStringConfig) =
            IntTag(readAnyInt(input, null) { it.toInt() })

        override val id get() = TagID.Int
        override fun wrap(value: Int) = IntTag(value)
    }
}
