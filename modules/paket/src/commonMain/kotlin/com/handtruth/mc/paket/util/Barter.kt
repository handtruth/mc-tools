package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.transmitter.Transmitter
import com.handtruth.mc.paket.transmitter.canTransmit
import com.handtruth.mc.paket.transmitter.exchange
import kotlinx.serialization.KSerializer

public class Barter<Q, R>(
    private val transmitter: Transmitter,
    requestSerializer: KSerializer<Q>,
    responseSerializer: KSerializer<R>
) {
    public val valid: Boolean get() = transmitter.canTransmit

    private val request = StructPaket(requestSerializer)
    private val response = StructPaket(responseSerializer)

    public suspend fun exchange(item: Q): R {
        request.item = item
        transmitter.exchange(request, response)
        return response.item
    }
}
