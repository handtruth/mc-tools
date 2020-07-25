@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.toCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.ByteArrayOutput

interface PaketSender : Breakable {
    suspend fun send(paket: Paket)
}

abstract class AbstractPaketSender : AbstractBreakable(), PaketSender {
    protected fun prepareByteArray(paket: Paket): ByteArray {
        val size = paket.size
        val sizeOfSize = sizeVarInt(size)
        val output = ByteArrayOutput(size + sizeOfSize)
        writeVarInt(output, size)
        paket.write(output)
        val bytes = output.toByteArray()
        validate(bytes.size - sizeOfSize == size) {
            "Paket produced wrong amount of data ($size expected, got ${bytes.size - sizeOfSize})"
        }
        return bytes
    }
}

fun PaketSender.asSynchronized(): PaketSender = SynchronizedPaketSender(this)

private class SynchronizedPaketSender(private val sender: PaketSender) : PaketSender {

    private val mutex = Mutex()

    override val broken get() = sender.broken
    override suspend fun send(paket: Paket) = mutex.withLock { sender.send(paket) }

    override fun close() = sender.close()
}

object EmptyPaketSender : PaketSender {
    override suspend fun send(paket: Paket) {}
    override val broken get() = false
    override fun close() {}
}

suspend infix fun Flow<Paket>.sendTo(sender: PaketSender) {
    collect { sender.send(it) }
}

suspend infix fun ReceiveChannel<Paket>.sendTo(sender: PaketSender) {
    consumeEach { sender.send(receive()) }
}

fun PaketSender.asNotCloseable(): PaketSender = object : PaketSender by this {
    override fun close() {}
}
