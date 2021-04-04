package com.handtruth.mc.paket

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Closeable

internal abstract class PaketRouterBase(private val receiver: PaketReceiver) : Closeable, Breakable {

    private val conductor = BroadcastChannel<Unit>(Channel.CONFLATED)
    private val mutex = Mutex()

    private val current: AtomicRef<Route?> = atomic(null)

    protected abstract fun select(peeking: PaketPeeking): Route?

    override fun close() {
        receiver.close()
    }

    override val broken: Boolean get() = receiver.broken

    private suspend fun catchNext(): Route? {
        mutex.withLock {
            receiver.isCaught && current.value!!.let { !it.broken && return it }
            try {
                while (true) {
                    if (!receiver.isCaught) {
                        receiver.catchOrdinal()
                    }
                    val route = select(receiver)
                    if (route != null) {
                        check(!route.broken)
                        current.value = route
                        return route
                    }
                    receiver.drop()
                }
            } finally {
                conductor.offer(Unit)
            }
        }
    }

    open inner class Route : AbstractPaketReceiver() {

        private fun invokeOnReceive(requested: Route?): Boolean {
            return if (requested === this) {
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
                    val loop = invokeOnReceive(catchNext())
                    if (loop) do {
                        channel.receive()
                        val repeat = invokeOnReceive(catchNext())
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
            current.value = null
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
            if (!isCaught) {
                catchOrdinal()
            }
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
            get() = super.broken || receiver.broken || field

        override fun close() {
            super.close()
            conductor.offer(Unit)
        }
    }
}
