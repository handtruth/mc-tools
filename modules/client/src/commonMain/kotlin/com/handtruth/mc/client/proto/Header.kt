package com.handtruth.mc.client.proto

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.enum

internal class Header : Paket() {
    var id by enum<PaketID>()
}
