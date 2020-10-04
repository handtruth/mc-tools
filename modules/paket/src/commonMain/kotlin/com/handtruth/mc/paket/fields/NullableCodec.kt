package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByte

class NullableCodec<T>(val inner: Codec<T>) : Codec<T?> {
    override fun measure(value: T?) = if (value == null) 1 else inner.measure(value) + 1

    override fun read(input: Input, old: T?): T? = when (input.readByte().toInt()) {
        0 -> null
        1 -> inner.read(input, old)
        else -> error("element count for nullable type should be ether 0 or 1")
    }

    override fun write(output: Output, value: T?) {
        if (value == null) {
            output.writeByte(0)
        } else {
            output.writeByte(1)
            inner.write(output, value)
        }
    }
}
