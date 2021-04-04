package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.Codec
import io.ktor.utils.io.core.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public interface Field<T> : ReadWriteProperty<Any?, T> {
    public val size: Int get() = codec.measure(value)

    public var value: T

    public val codec: Codec<T>

    public fun read(input: Input) {
        value = codec.read(input)
    }

    public fun write(output: Output) {
        codec.write(output, value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}
