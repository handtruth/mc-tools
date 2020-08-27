package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyInt
import com.handtruth.mc.nbt.util.readInt64
import com.handtruth.mc.nbt.util.writeInt64
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

class LongTag(var long: Long) : MutableTag<Long>(TagID.Long) {
    override var value
        get() = long
        set(value) {
            long = value
        }

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeInt64(output, conf, long)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        output.append(long.toString())
        output.append('l')
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = long
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Long) {
        long = newValue
    }

    companion object : TagResolver<Long> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = LongTag(readInt64(input, conf))
        override fun readText(input: Reader, conf: NBTStringConfig) =
            LongTag(readAnyInt(input, 'l') { it.toLong() })

        override val id get() = TagID.Long
        override fun wrap(value: Long) = LongTag(value)
    }
}
