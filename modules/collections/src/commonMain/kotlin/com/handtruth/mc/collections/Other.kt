package com.handtruth.mc.collections

private val endObject = Any()

@Suppress("UNCHECKED_CAST")
internal fun <T> end() = endObject as T

internal expect inline fun assert(value: Boolean)
internal expect inline fun assert(value: Boolean, lazyMessage: () -> Any)
