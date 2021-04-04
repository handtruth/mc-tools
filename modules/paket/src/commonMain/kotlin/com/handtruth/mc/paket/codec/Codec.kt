package com.handtruth.mc.paket.codec

import io.ktor.utils.io.core.*

public interface Codec<T> {
    public fun measure(value: T): Int
    public fun read(input: Input): T
    public fun write(output: Output, value: T)
}
