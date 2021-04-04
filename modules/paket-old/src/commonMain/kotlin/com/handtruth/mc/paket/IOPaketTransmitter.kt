@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

private class OutputPaketSender(
    val channel: Output,
    private val ioContext: CoroutineContext?
) : AbstractPaketSender() {
    override suspend fun send(paket: Paket) = breakableAction {
        val size = paket.size
        withContext(ioContext ?: coroutineContext) {
            writeVarInt(channel, size)
            paket.write(channel)
        }
    }

    override fun close() {
        super.close()
        channel.close()
    }
}

private class InputPaketReceiver(
    val channel: Input,
    private val ioContext: CoroutineContext?
) : AbstractPaketReceiver() {

    private val chunk: ByteReadPacket? = null

    override suspend fun catchOrdinal(): Int = breakableAction {
        if (isCaught) {
            drop()
            catchOrdinal()
        } else withContext(ioContext ?: EmptyCoroutineContext) {
            size = readVarInt(channel)
            val id = readVarInt(channel)
            isCaught = true
            idOrdinal = id
            id
        }
    }

    override suspend fun drop(): Unit = breakableAction {
        if (isCaught) {
            val size = size
            val toSkip = (size - sizeVarInt(idOrdinal)).toLong()
            val skipped = withContext(ioContext ?: coroutineContext) { channel.discard(toSkip) }
            validate(skipped == toSkip) {
                "Input ended, but paket not dropped correctly ($skipped bytes dropped of $size)"
            }
            isCaught = false
        } else {
            catchOrdinal()
            drop()
        }
    }

    override suspend fun receive(paket: Paket) {
        if (!isCaught) {
            catchOrdinal()
        }
        paket.read(channel)
        val estimate = (size - paket.size - sizeVarInt(idOrdinal)).toLong()
        val skipped = channel.discard(estimate)
        validate(estimate == skipped) {
            "Failed to discard paket estimate ($estimate estimated, skipped $skipped)"
        }
        isCaught = false
    }

    override fun peek(paket: Paket) {
        check(isCaught)
        channel.preview {
            paket.read(this)
        }
    }

    override fun close() {
        super.close()
        channel.close()
    }
}

fun PaketSender(output: Output, ioContext: CoroutineContext? = null): PaketSender =
    OutputPaketSender(output, ioContext)

fun PaketReceiver(input: Input, ioContext: CoroutineContext? = null): PaketReceiver =
    InputPaketReceiver(input, ioContext)

fun PaketTransmitter(input: Input, output: Output, ioContext: CoroutineContext? = null) =
    PaketTransmitter(PaketReceiver(input, ioContext), PaketSender(output, ioContext))
