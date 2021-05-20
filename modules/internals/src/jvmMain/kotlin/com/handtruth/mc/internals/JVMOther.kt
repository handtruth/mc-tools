package com.handtruth.mc.internals

@Suppress("NOTHING_TO_INLINE")
actual inline fun assert(value: Boolean) = kotlin.assert(value)

actual inline fun assert(value: Boolean, lazyMessage: () -> Any) = kotlin.assert(value, lazyMessage)
