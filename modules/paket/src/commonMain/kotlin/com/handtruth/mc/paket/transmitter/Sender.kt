package com.handtruth.mc.paket.transmitter

import io.ktor.utils.io.core.*

public interface Sender : Inserter, Closeable {
    public val canSend: Boolean

    public val isPrepared: Boolean

    public suspend fun prepare()

    public suspend fun commit()

    public fun rollback()
}
