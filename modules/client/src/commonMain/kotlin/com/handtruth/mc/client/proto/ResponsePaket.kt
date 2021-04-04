package com.handtruth.mc.client.proto

import com.handtruth.mc.client.model.ServerStatus
import com.handtruth.mc.client.util.json
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.string

internal class ResponsePaket : Paket() {
    var message: ServerStatus by string(json)
}
