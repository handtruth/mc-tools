package com.handtruth.mc.nbt.test

import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.contentDeepEquals
import com.handtruth.mc.types.contentToString
import kotlin.test.assertEquals

data class DynamicWrapper(val dynamic: Dynamic) {
    override fun equals(other: Any?) =
        this === other || other is DynamicWrapper && this.dynamic.contentDeepEquals(other.dynamic)

    override fun hashCode() = dynamic.hashCode()

    override fun toString() = dynamic.contentToString()
}

fun assertDynamicEquals(expected: Dynamic, actual: Dynamic, message: String? = null) {
    assertEquals(DynamicWrapper(expected), DynamicWrapper(actual), message)
}
