package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readFloat
import kotlinx.io.writeFloat
import kotlin.reflect.KProperty

class FloatTag(var number: Float) : MutableTag<Float>(TagID.Float) {
    override var value
        get() = number
        set(value) { number = value }
    override fun write(output: Output) {
        output.writeFloat(number)
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = number
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Float) {
        number = newValue
    }
    companion object : TagResolver<Float> {
        override fun read(input: Input) = FloatTag(input.readFloat())
        override val id get() = TagID.Float
        override fun wrap(value: Float) = FloatTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append(number.toString())
        builder.append('f')
    }
}
