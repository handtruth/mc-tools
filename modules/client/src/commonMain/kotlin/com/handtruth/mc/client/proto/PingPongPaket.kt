package com.handtruth.mc.client.proto

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.long

class PingPongPaket : Paket() {
    var payload by long
}
