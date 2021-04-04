package com.handtruth.mc.client.proto

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.enum
import com.handtruth.mc.paket.codec.string
import com.handtruth.mc.paket.codec.ushort
import com.handtruth.mc.paket.codec.uzint
import com.handtruth.mc.paket.field.field

internal class HandshakePaket(
    version: UInt,
    address: String,
    port: UShort,
    state: States
) : Paket() {
    var version by field(uzint, version)
    var address by field(string, address)
    var port by field(ushort, port)
    var state by field(enum(), state)

    enum class States {
        Nothing, Status, Login
    }
}
