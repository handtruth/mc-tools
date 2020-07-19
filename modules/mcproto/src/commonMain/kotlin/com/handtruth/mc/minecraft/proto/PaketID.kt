package com.handtruth.mc.minecraft.proto

import com.handtruth.mc.paket.PaketTransmitter

enum class PaketID {
    HandshakeRequestResponse,
    PingPong
}

suspend fun PaketTransmitter.catch(): PaketID {
    val id = catchOrdinal()
    return PaketID.values()[id]
}

val PaketTransmitter.id get() = PaketID.values()[idOrdinal]
