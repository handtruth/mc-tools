package com.handtruth.mc.minecraft.proto.test

import com.handtruth.mc.minecraft.model.ServerStatus
import com.handtruth.mc.minecraft.proto.*
import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.receive
import com.soywiz.korio.net.createTcpClient
import com.soywiz.korio.stream.toAsyncStream
import io.ktor.test.dispatcher.testSuspend
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtoTest {

    private val json = Json(JsonConfiguration.Stable)

    @Test
    fun protocolTest() = testSuspend {
        val hs = HandshakePaket.produce()
        hs.address = "space.mc.handtruth.com"
        hs.state = HandshakePaket.States.Status
        val client = createTcpClient(hs.address, 25565)
        val ts = PaketTransmitter(client.toAsyncStream())
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

        val asJson = json.stringify(ServerStatus.serializer(), response.message)
        val status = json.parse(ServerStatus.serializer(), asJson)
        assertEquals(response.message, status)
    }

}
