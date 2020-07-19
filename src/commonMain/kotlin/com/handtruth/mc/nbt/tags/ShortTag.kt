package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readShort
import kotlinx.io.writeShort
import kotlin.reflect.KProperty

data class ShortTag(var short: Short) : MutableTag<Short>(TagID.Short) {
    override var value
        get() = short
        set(value) { short = value }
    override fun write(output: Output) {
        output.writeShort(short)
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = short
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Short) {
        short = newValue
    }
    companion object : TagResolver<Short> {
        override fun read(input: Input) = ShortTag(input.readShort())
        override val id get() = TagID.Short
        override fun wrap(value: Short) = ShortTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append(short.toString())
        builder.append('s')
    }
}
