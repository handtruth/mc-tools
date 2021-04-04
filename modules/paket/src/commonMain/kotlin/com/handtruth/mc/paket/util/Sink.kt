package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.transmitter.Sender
import com.handtruth.mc.paket.transmitter.send
import kotlinx.serialization.KSerializer

public class Sink<T>(private val sender: Sender, serializer: KSerializer<T>) {
    public val valid: Boolean get() = sender.canSend

    private val request = StructPaket(serializer)

    public suspend fun drain(item: T) {
        request.item = item
        sender.send(request)
    }
}
