package com.handtruth.mc.paket

import kotlinx.coroutines.CancellationException
import kotlinx.io.Closeable

class BrokenObjectException : RuntimeException {
    constructor() : super("broken object")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super("broken object", cause)
}

interface Breakable : Closeable {
    val broken: Boolean
}

abstract class AbstractBreakable : Breakable {
    override var broken = false
        protected set

    @PublishedApi
    internal var cause: Throwable? = null

    protected inline fun <R> breakableAction(lambda: () -> R): R {
        if (broken) {
            throw BrokenObjectException("object broken, consider recreate", cause)
        }
        try {
            return lambda()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            cause = e
            close()
            throw e
        }
    }

    override fun close() {
        broken = true
    }
}
