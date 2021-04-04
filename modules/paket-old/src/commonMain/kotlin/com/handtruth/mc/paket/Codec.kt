package com.handtruth.mc.paket

import io.ktor.utils.io.core.*

interface Codec<T> {
    fun measure(value: T): Int
    fun read(input: Input, old: T?): T
    fun write(output: Output, value: T)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Codec<T>.read(input: Input) = read(input, null)
