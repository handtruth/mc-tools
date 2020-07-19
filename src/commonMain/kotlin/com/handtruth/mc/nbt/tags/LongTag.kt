package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readLong
import kotlinx.io.writeLong
import kotlin.reflect.KProperty

class LongTag(var integer: Long) : MutableTag<Long>(TagID.Long) {
    override var value
        get() = integer
        set(value) { integer = value }
    override fun write(output: Output) {
        output.writeLong(integer)
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = integer
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Long) {
        integer = newValue
    }
    companion object : TagResolver<Long> {
        override fun read(input: Input) = LongTag(input.readLong())
        override val id get() = TagID.Long
        override fun wrap(value: Long) = LongTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append(integer.toString())
        builder.append('l')
    }
}
