package com.handtruth.mc.client.proto

import com.handtruth.mc.paket.SinglePaket

object RequestPaket : SinglePaket<RequestPaket>() {
    override val id = PaketID.HandshakeRequestResponse
}
