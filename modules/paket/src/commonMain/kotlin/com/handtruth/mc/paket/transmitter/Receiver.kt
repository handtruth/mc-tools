package com.handtruth.mc.paket.transmitter

import io.ktor.utils.io.core.*

public interface Receiver : Extractor, Closeable {
    public val canReceive: Boolean

    public val isCaught: Boolean

    public suspend fun catch()

    public suspend fun drop()
}
