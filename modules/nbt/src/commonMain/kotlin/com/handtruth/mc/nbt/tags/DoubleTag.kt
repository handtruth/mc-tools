package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readAnyFloating
import com.handtruth.mc.nbt.util.readDouble
import com.handtruth.mc.nbt.util.writeDouble
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

class DoubleTag(var number: Double) : MutableTag<Double>(TagID.Double) {
    override var value
        get() = number
        set(value) {
            number = value
        }

    override fun writeBinary(output: Output, conf: NBTBinaryConfig) {
        writeDouble(output, conf, number)
    }

    override fun writeText(output: Appendable, conf: NBTStringConfig, level: Int) {
        output.append(number.toString())
        output.append('d')
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = number
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Double) {
        number = newValue
    }

    companion object : TagResolver<Double> {
        override fun readBinary(input: Input, conf: NBTBinaryConfig) = DoubleTag(readDouble(input, conf))
        override fun readText(input: Reader, conf: NBTStringConfig) =
            DoubleTag(readAnyFloating(input, 'd') { it.toDouble() })

        override val id get() = TagID.Double
        override fun wrap(value: Double) = DoubleTag(value)
    }
}
