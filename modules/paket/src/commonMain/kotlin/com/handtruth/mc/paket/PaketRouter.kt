package com.handtruth.mc.paket

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

inline fun PaketReceiver.split(
    count: Int,
    crossinline splitter: (PaketPeeking) -> Int
): List<PaketReceiver> {
    val result = object : PaketRouter(count, this) {
        override fun splitter(peeking: PaketPeeking): Int {
            return splitter(peeking)
        }
    }
    return result.children
}

inline infix fun <reified E : Enum<E>> PaketReceiver.split(
    crossinline splitter: (PaketPeeking) -> E?
): List<PaketReceiver> {
    return split(enumValues<E>().size) { splitter(it)?.ordinal ?: -1 }
}

inline fun PaketTransmitter.split(
    count: Int,
    crossinline splitter: (PaketPeeking) -> Int
): List<PaketTransmitter> {
    val sender = (this as PaketSender).asSynchronized()
    return (this as PaketReceiver).split(count, splitter).map { RouterPaketTransmitter(it, sender) }
}

inline infix fun <reified E : Enum<E>> PaketTransmitter.split(
    crossinline splitter: (PaketPeeking) -> E?
): List<PaketTransmitter> {
    val sender = (this as PaketSender).asSynchronized()
    return (this as PaketReceiver).split(splitter).map { RouterPaketTransmitter(it, sender) }
}

@PublishedApi
internal class RouterPaketTransmitter(
    private val receiver: PaketReceiver,
    sender: PaketSender
) : PaketTransmitter, PaketReceiver by receiver, PaketSender by sender {
    override val broken get() = receiver.broken
    override fun close() {
        receiver.close()
    }
}

@PublishedApi
@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class PaketRouter(private val count: Int, private val receiver: PaketReceiver) {

    abstract fun splitter(peeking: PaketPeeking): Int

    private val _children = List(count) { PartialReceiver(it) }
    val children: List<PaketReceiver> get() = _children

    private val conductor = BroadcastChannel<Unit>(Channel.CONFLATED)
    private val mutex = Mutex()

    private val code = atomic(-1)

    private suspend fun catchNext(): Int {
        receiver.isCaught && return code.value
        try {
            while (true) {
                receiver.catchOrdinal()
                code.value = splitter(receiver)
                if (code.value == -1)
                    continue
                if (code.value !in 0 until count)
                    throw IndexOutOfBoundsException()
                if (!_children[code.value].broken)
                    break
            }
        } finally {
            conductor.offer(Unit)
        }
        return code.value
    }

    private inner class PartialReceiver(val identity: Int) : AbstractPaketReceiver() {

        private fun invokeOnReceive(requested: Int): Boolean {
            return if (requested == identity) {
                idOrdinal = receiver.idOrdinal
                size = receiver.size
                isCaught = true
                false
            } else {
                true
            }
        }

        override suspend fun catchOrdinal(): Int = breakableAction {
            return if (isCaught) {
                drop()
                catchOrdinal()
            } else {
                val channel = conductor.openSubscription()
                try {
                    val loop = invokeOnReceive(mutex.withLock { catchNext() })
                    if (loop) do {
                        channel.receive()
                        val repeat = invokeOnReceive(mutex.withLock { catchNext() })
                    } while (repeat)
                } finally {
                    channel.cancel()
                }
                idOrdinal
            }
        }

        private fun clear() {
            isCaught = false
            idOrdinal = -1
            size = -1
            code.value = -1
        }

        override suspend fun drop() {
            if (!isCaught) {
                catchOrdinal()
                drop()
            } else {
                mutex.withLock {
                    clear()
                    receiver.drop()
                    conductor.offer(Unit)
                }
            }
        }

        override suspend fun receive(paket: Paket) {
            if (!isCaught)
                catchOrdinal()
            mutex.withLock {
                receiver.receive(paket)
                clear()
                conductor.offer(Unit)
            }
        }

        override fun peek(paket: Paket) {
            check(isCaught)
            receiver.peek(paket)
        }

        override var broken = false
            get() = receiver.broken || field
    }
}
