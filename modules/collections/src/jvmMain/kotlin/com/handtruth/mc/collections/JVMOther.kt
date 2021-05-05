package com.handtruth.mc.collections

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun assert(value: Boolean) = kotlin.assert(value)

internal actual inline fun assert(value: Boolean, lazyMessage: () -> Any) = kotlin.assert(value, lazyMessage)
