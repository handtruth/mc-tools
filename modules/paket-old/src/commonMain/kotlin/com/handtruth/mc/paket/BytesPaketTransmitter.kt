@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import io.ktor.utils.io.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

private class BytesPaketSender(private val output: SendChannel<ByteReadPacket>) : PaketSender {

    override suspend fun send(paket: Paket) {
        val data = buildPacket {
            paket.write(this)
        }
        output.send(data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val broken
        get() = output.isClosedForSend

    override fun close() {
        output.close()
    }
}

private class BytesPaketReceiver(private val input: ReceiveChannel<ByteReadPacket>) : AbstractPaketReceiver() {

    private var pending: ByteReadPacket? = null

    override suspend fun catchOrdinal(): Int = breakableAction {
        val buffer = input.receive()
        pending = buffer
        buffer.copy().use {
            idOrdinal = readVarInt(it)
        }
        size = buffer.remaining.toInt()
        isCaught = true
        return idOrdinal
    }

    private fun clear() {
        pending = null
        isCaught = false
        size = -1
        idOrdinal = -1
    }

    override suspend fun drop() = breakableAction {
        if (!isCaught) {
            catchOrdinal()
        }
        clear()
    }

    override suspend fun receive(paket: Paket) = breakableAction {
        if (!isCaught) {
            catchOrdinal()
        }
        pending!!.use { paket.read(it) }
        clear()
    }

    override fun peek(paket: Paket) = breakableAction {
        check(isCaught)
        pending!!.copy().use { paket.read(it) }
    }

    override fun close() {
        input.cancel()
        super.close()
    }
}

@ExperimentalPaketApi
fun PaketSender(output: SendChannel<ByteReadPacket>): PaketSender = BytesPaketSender(output)

@ExperimentalPaketApi
fun PaketReceiver(input: ReceiveChannel<ByteReadPacket>): PaketReceiver = BytesPaketReceiver(input)

@ExperimentalPaketApi
fun PaketTransmitter(input: ReceiveChannel<ByteReadPacket>, output: SendChannel<ByteReadPacket>): PaketTransmitter =
    PaketTransmitter(PaketReceiver(input), PaketSender(output))

@ExperimentalPaketApi
fun PaketTransmitter(channel: Channel<ByteReadPacket>): PaketTransmitter =
    PaketTransmitter(channel, channel)
