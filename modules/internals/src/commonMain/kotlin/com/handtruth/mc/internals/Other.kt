package com.handtruth.mc.internals

private val endObject = Any()

@Suppress("UNCHECKED_CAST")
fun <T> end() = endObject as T

expect inline fun assert(value: Boolean)
expect inline fun assert(value: Boolean, lazyMessage: () -> Any)
