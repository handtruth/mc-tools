package com.handtruth.mc.client.test

import io.ktor.network.selector.*
import io.ktor.test.dispatcher.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ProtoTest {

    private val json = Json {}

    @OptIn(InternalAPI::class)
    val selector = SelectorManager(Dispatchers.Default)

    @Test
    fun protocolTest() = testSuspend {
        /*
        val hs = HandshakePaket(UInt.MAX_VALUE, "", 0u, HandshakePaket.States.Nothing)
        hs.address = "space.mc.handtruth.com"
        hs.state = HandshakePaket.States.Status
        val client = aSocket(selector).tcp().connect(hs.address, 25565)
        val ts = Transmitter(client.openReadChannel(), client.openWriteChannel())
        ts.send(hs)
        ts.send(RequestPaket.produce())
        assertEquals(PaketID.HandshakeRequestResponse, ts.catch())
        assertEquals(PaketID.HandshakeRequestResponse, ts.id)
        val response = ts.receive(ResponsePaket)
        val pp = PingPongPaket.produce()
        ts.send(pp)
        assertEquals(PaketID.PingPong, ts.catch())
        assertEquals(PaketID.PingPong, ts.id)
        ts.receive(PingPongPaket)
        ts.send(pp)
        ts.drop()

        val asJson = json.encodeToString(ServerStatus.serializer(), response.message)
        val status = json.decodeFromString(ServerStatus.serializer(), asJson)
        assertEquals(response.message, status.copy(description = status.description.flatten()))
        */
    }
}
