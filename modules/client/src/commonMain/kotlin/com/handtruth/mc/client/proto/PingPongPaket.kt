package com.handtruth.mc.client.proto

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketCreator
import com.handtruth.mc.paket.fields.int64

class PingPongPaket(payload: Long = 0) : Paket() {
    override val id = PaketID.PingPong
    var payload by int64(payload)

    companion object : PaketCreator<PingPongPaket> {
        override fun produce() = PingPongPaket()
    }
}
