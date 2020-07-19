package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.TagID
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

abstract class Tag<out T: Any>(val id: TagID) {
    abstract val value: T
    abstract fun write(output: Output)
    abstract fun toMojangson(builder: Appendable, pretty: Boolean = false, level: Int = 0)

    override fun toString() = buildString {
        toMojangson(this)
    }

    fun toString(pretty: Boolean) = buildString {
        toMojangson(this, pretty, 0)
    }

    override fun hashCode() = value.hashCode()
    override fun equals(other: Any?) = this === other || other is Tag<Any> && value == other.value

    companion object {
        val empty by lazy { EndTag() }
    }
}

abstract class MutableTag<T: Any>(id: TagID) : Tag<T>(id) {
    abstract override var value: T
}

operator fun <T: Any> Tag<T>.getValue(thisRef: Any?, property: KProperty<*>) = value
operator fun <T: Any> MutableTag<T>.getValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
    value = newValue
}

interface TagResolver<T: Any> {
    fun read(input: Input): Tag<T>
    val id: TagID
    fun wrap(value: T): Tag<T>
}
