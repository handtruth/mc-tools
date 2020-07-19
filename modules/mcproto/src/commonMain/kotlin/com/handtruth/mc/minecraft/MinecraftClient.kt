package com.handtruth.mc.minecraft

import com.handtruth.mc.minecraft.model.ServerStatus
import com.handtruth.mc.minecraft.proto.HandshakePaket
import com.handtruth.mc.minecraft.proto.PingPongPaket
import com.handtruth.mc.minecraft.proto.RequestPaket
import com.handtruth.mc.minecraft.proto.ResponsePaket
import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.receive
import com.soywiz.korio.net.AsyncClient
import com.soywiz.korio.net.createTcpClient
import com.soywiz.korio.stream.toAsyncStream
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.time.measureTime

class MinecraftClient internal constructor(
    private val ts: PaketTransmitter,
    private val client: AsyncClient
) {

    suspend fun getStatus(): ServerStatus {
        ts.send(RequestPaket)
        return ts.receive(ResponsePaket).message
    }

    suspend fun ping() = flow {
        val paket = PingPongPaket()
        while (true) {
            paket.payload = Random.nextLong()
            val duration = measureTime {
                ts.send(paket)
                ts.receive(paket)
            }
            emit(duration)
        }
    }

    suspend fun disconnect() {
        ts.close()
        client.close()
    }
}

@Suppress("FunctionName")
suspend fun MinecraftClient(address: String, port: Int): MinecraftClient {
    val client = createTcpClient(address, port)
    val ts = PaketTransmitter(client.toAsyncStream())
    ts.send(HandshakePaket(address = address, port = port, state = HandshakePaket.States.Status))
    return MinecraftClient(ts, client)
}

suspend inline fun <R> MinecraftClient.use(block: (MinecraftClient) -> R): R {
    try {
        return block(this)
    } finally {
        withContext(NonCancellable) {
            disconnect()
        }
    }
}
