@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface PaketTransmitter : PaketSender, PaketReceiver

fun PaketTransmitter(receiver: PaketReceiver, sender: PaketSender): PaketTransmitter =
    CombinedPaketTransmitter(receiver, sender)

private class CombinedPaketTransmitter(private val receiver: PaketReceiver, private val sender: PaketSender) :
    PaketTransmitter, PaketReceiver by receiver, PaketSender by sender {
    override val broken get() = receiver.broken && sender.broken

    override fun close() {
        receiver.close()
        sender.close()
    }
}

fun PaketTransmitter.asSynchronized() = PaketTransmitter(
    this as PaketReceiver,
    (this as PaketSender).asSynchronized()
)

operator fun PaketReceiver.plus(sender: PaketSender) = PaketTransmitter(this, sender)
operator fun PaketSender.plus(receiver: PaketReceiver) = PaketTransmitter(receiver, this)

suspend inline fun <P : Paket> PaketTransmitter.reply(block: PaketReceiver.() -> P) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    receive {
        val response = block()
        send(response)
    }
}

suspend inline fun <P : Paket> PaketTransmitter.replyAll(block: PaketReceiver.() -> P): Nothing {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    try {
        while (true)
            reply(block)
    } finally {
        close()
    }
}

fun PaketTransmitter.asNotCloseable(): PaketTransmitter = object : PaketTransmitter by this {
    override fun close() {}
}

object BrokenPaketTransmitter : PaketTransmitter {
    private fun error(): Nothing = throw BrokenObjectException("always broken")
    override val broken = true
    override val idOrdinal get() = error()
    override val isCaught get() = error()
    override val size get() = error()
    override suspend fun catchOrdinal() = error()
    override suspend fun drop() = error()
    override fun peek(paket: Paket) = error()
    override suspend fun send(paket: Paket) = error()
    override fun close() {}
}
