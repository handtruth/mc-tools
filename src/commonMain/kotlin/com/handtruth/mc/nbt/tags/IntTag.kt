package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readInt
import kotlinx.io.writeInt
import kotlin.reflect.KProperty

class IntTag(var integer: Int) : MutableTag<Int>(TagID.Int) {
    override var value
        get() = integer
        set(value) { integer = value }
    override fun write(output: Output) {
        output.writeInt(integer)
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = integer
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Int) {
        integer = newValue
    }
    companion object : TagResolver<Int> {
        override fun read(input: Input) = IntTag(input.readInt())
        override val id get() = TagID.Int
        override fun wrap(value: Int) = IntTag(value)
    }

    override fun toMojangson(builder: Appendable, pretty: Boolean, level: Int) {
        builder.append(integer.toString())
    }
}
