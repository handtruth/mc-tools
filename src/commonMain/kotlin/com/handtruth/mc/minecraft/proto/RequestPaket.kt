package com.handtruth.mc.minecraft.proto

import com.handtruth.mc.paket.SinglePaket

object RequestPaket : SinglePaket<RequestPaket>() {
    override val id = PaketID.HandshakeRequestResponse
}
