package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readDouble
import kotlinx.io.writeDouble
import kotlin.reflect.KProperty

class DoubleTag(var number: Double) : MutableTag<Double>(TagID.Double) {
    override var value
        get() = number
        set(value) { number = value }
    override fun write(output: Output) {
        output.writeDouble(number)
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = number
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Double) {
        number = newValue
    }
    companion object : TagResolver<Double> {
        override fun read(input: Input) = DoubleTag(input.readDouble())
        override val id get() = TagID.Double
        override fun wrap(value: Double) = DoubleTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append(number.toString())
        builder.append('d')
    }
}
