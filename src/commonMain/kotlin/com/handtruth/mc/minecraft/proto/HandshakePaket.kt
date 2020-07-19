package com.handtruth.mc.minecraft.proto

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketCreator
import com.handtruth.mc.paket.fields.enum
import com.handtruth.mc.paket.fields.string
import com.handtruth.mc.paket.fields.uint16
import com.handtruth.mc.paket.fields.varInt

class HandshakePaket(version: Int = -1,
                     address: String = "localhost",
                     port: Int = 25565,
                     state: States = States.Nothing) : Paket() {
    override val id = PaketID.HandshakeRequestResponse

    var version by varInt(version)
    var address by string(address)
    var port by uint16(port.toUShort())
    var state by enum(state)

    enum class States {
        Nothing, Status, Login
    }

    companion object : PaketCreator<HandshakePaket> {
        override fun produce() = HandshakePaket()
    }
}
