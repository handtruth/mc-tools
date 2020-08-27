package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.TagID
import com.handtruth.mc.nbt.util.Reader
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

abstract class Tag<out T: Any>(val id: TagID) {
    abstract val value: T
    internal abstract fun writeBinary(output: Output, conf: NBTBinaryConfig)
    internal abstract fun writeText(output: Appendable, conf: NBTStringConfig, level: Int)

    override fun toString() = buildString {
        writeText(this, NBTStringConfig.Default, 0)
    }

    fun toString(conf: NBTStringConfig) = buildString {
        writeText(this, conf, 0)
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
    fun readBinary(input: Input, conf: NBTBinaryConfig): Tag<T>
    fun readText(input: Reader, conf: NBTStringConfig): Tag<T>
    val id: TagID
    fun wrap(value: T): Tag<T>
}
