package com.handtruth.mc.nbt.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class NBTException(message: String) : RuntimeException(message)

inline fun validate(value: Boolean, message: () -> Any?) {
    contract {
        callsInPlace(message, InvocationKind.AT_MOST_ONCE)
        returns() implies value
    }
    if (!value)
        throw NBTException(message().toString())
}
