package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.Knot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Closeable

/**
 * Actually, I can't imagine any situation where this may be useful.
 */
@ExperimentalPaketApi
interface PaketBroadcast : Closeable {
    fun openSubscription(): PaketReceiver
}

@ExperimentalPaketApi
interface PaketBroadcastSender : PaketSender, PaketBroadcast {
    override fun openSubscription(): PaketTransmitter
}

/**
 * DOES NOT WORK IN MULTIPLE THREADS
 */
@ExperimentalPaketApi
fun PaketReceiver.broadcast(): PaketBroadcast = PaketBroadcastImpl(this)

@ExperimentalPaketApi
fun PaketTransmitter.broadcast(): PaketBroadcastSender = PaketBroadcastSenderImpl(this, this)

private class PaketBroadcastSenderImpl private constructor(
    private val sender: PaketSender,
    private val receiver: PaketBroadcast
) : PaketBroadcastSender, PaketSender by sender {

    private val notCloseableSender = sender.asNotCloseable()

    constructor(sender: PaketSender, receiver: PaketReceiver) : this(sender.asSynchronized(), receiver.broadcast())

    override fun openSubscription() = PaketTransmitter(
         receiver.openSubscription(), notCloseableSender
    )

    override fun close() {
        sender.close()
        receiver.close()
    }
}

@OptIn(ExperimentalCoroutinesApi::class, InternalPaketApi::class)
private class PaketBroadcastImpl(private val receiver: PaketReceiver) : Knot(), PaketBroadcast {

    override fun openSubscription(): PaketReceiver = NodeReceiver()

    override fun close() {
        super.close()
        receiver.close()
    }

    override suspend fun enter() {
        if (!receiver.isCaught)
            receiver.catchOrdinal()
    }

    override suspend fun leave() {
        if (receiver.isCaught)
            receiver.drop()
    }

    inner class NodeReceiver : AbstractPaketReceiver() {
        private val fiber = Fiber()

        override suspend fun catchOrdinal(): Int = breakableAction {
            if (isCaught) {
                drop()
                catchOrdinal()
            } else {
                fiber.start()
                isCaught = receiver.isCaught
                idOrdinal = receiver.idOrdinal
                size = receiver.size
                idOrdinal
            }
        }

        override suspend fun drop(): Unit = breakableAction {
            if (!isCaught) {
                catchOrdinal()
                drop()
            } else {
                isCaught = false
                idOrdinal = -1
                size = -1
                fiber.stop()
            }
        }

        override suspend fun receive(paket: Paket) {
            peek(paket)
            drop()
        }

        override fun peek(paket: Paket) {
            check(isCaught)
            receiver.peek(paket)
        }

        override fun close() {
            super.close()
            fiber.close()
        }
    }

}
