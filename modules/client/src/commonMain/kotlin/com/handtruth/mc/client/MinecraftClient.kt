package com.handtruth.mc.client

import com.handtruth.mc.client.model.ServerStatus
import com.handtruth.mc.client.proto.*
import com.handtruth.mc.client.proto.HandshakePaket
import com.handtruth.mc.client.proto.Header
import com.handtruth.mc.client.proto.PaketID
import com.handtruth.mc.client.proto.ResponsePaket
import com.handtruth.mc.paket.transmitter.Transmitter
import com.handtruth.mc.paket.transmitter.receive
import com.handtruth.mc.paket.transmitter.send
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.measureTime

class MinecraftClient internal constructor(
    private val socket: Socket,
    private val ts: Transmitter
) : Closeable {
    private val header = Header()

    internal suspend fun handshake(version: UInt, address: String, port: UShort) {
        ts.send {
            header.id = PaketID.HandshakeRequestResponse
            ts.insert(header)
            val handshake = HandshakePaket(
                version = version,
                address = address,
                port = port,
                state = HandshakePaket.States.Status
            )
            ts.insert(handshake)
        }
    }

    suspend fun getStatus(): ServerStatus {
        header.id = PaketID.HandshakeRequestResponse
        ts.send(header)
        val response = ResponsePaket()
        ts.receive {
            it.extract(header)
            it.extract(response)
        }
        return response.message
    }

    suspend fun ping() = flow {
        header.id = PaketID.PingPong
        val paket = PingPongPaket()
        while (true) {
            paket.payload = Random.nextLong()
            val duration = measureTime {
                ts.send {
                    it.insert(header)
                    it.insert(paket)
                }
                ts.receive {
                    it.extract(header)
                    it.extract(paket)
                }
            }
            emit(duration)
        }
    }

    override fun close() {
        ts.close()
        socket.close()
    }
}

@OptIn(InternalAPI::class)
@Suppress("FunctionName")
suspend fun MinecraftClient(
    selector: SelectorManager,
    address: String,
    port: Int,
    version: UInt = UInt.MAX_VALUE,
    shadowAddress: String = address,
    shadowPort: UShort = port.toUShort()
): MinecraftClient {
    val socket = aSocket(selector).tcp().connect(address, port)
    val ts = Transmitter(socket.openReadChannel(), socket.openWriteChannel())
    val client = MinecraftClient(socket, ts)
    try {
        client.handshake(version, shadowAddress, shadowPort)
    } catch (thr: Throwable) {
        client.close()
        throw thr
    }
    return client
}
