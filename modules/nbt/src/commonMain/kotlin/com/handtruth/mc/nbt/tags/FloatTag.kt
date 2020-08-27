package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyFloating
import com.handtruth.mc.nbt.util.readFloat
import com.handtruth.mc.nbt.util.writeFloat
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

class FloatTag(var number: Float) : MutableTag<Float>(TagID.Float) {
    override var value
        get() = number
        set(value) {
            number = value
        }

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeFloat(output, conf, number)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        output.append(number.toString())
        output.append('f')
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = number
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Float) {
        number = newValue
    }

    companion object : TagResolver<Float> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = FloatTag(readFloat(input, conf))
        override fun readText(input: Reader, conf: NBTStringConfig) =
            FloatTag(readAnyFloating(input, 'f') { it.toFloat() })

        override val id get() = TagID.Float
        override fun wrap(value: Float) = FloatTag(value)
    }
}
