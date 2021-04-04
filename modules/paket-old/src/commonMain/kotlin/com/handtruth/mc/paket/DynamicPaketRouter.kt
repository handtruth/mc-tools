package com.handtruth.mc.paket

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel

fun PaketTransmitter.asRouter(): PaketSenderRouter = PaketSenderRouterImpl(asSynchronized())
fun PaketReceiver.asRouter(): PaketRouter = PaketRouterImpl(this)

interface PaketSenderRouter : PaketSender, PaketRouter {
    override fun route(condition: (PaketPeeking) -> Boolean): PaketTransmitter
    override fun default(): PaketTransmitter
}

private class PaketSenderRouterImpl(private val ts: PaketTransmitter) : PaketSenderRouter {
    override val broken get() = ts.broken

    private var default: PaketTransmitter? = null

    private val router = PaketRouterImpl(ts)

    override suspend fun send(paket: Paket) = ts.send(paket)

    override fun route(condition: (PaketPeeking) -> Boolean): PaketTransmitter = TSRoute(router.route(condition), ts)

    override fun default(): PaketTransmitter {
        val default = default
        return if (default == null || default.broken) {
            val result = TSRoute(router.default(), ts)
            this.default = result
            result
        } else {
            default
        }
    }

    override fun close() = ts.close()

    private class TSRoute(
        val receiver: PaketReceiver,
        val sender: PaketSender
    ) : PaketTransmitter, PaketReceiver by receiver, PaketSender by sender {
        override val broken get() = receiver.broken
        override fun close() {
            receiver.close()
        }

        override suspend fun send(paket: Paket) {
            receiver.isCaught
            sender.send(paket)
        }
    }
}

interface PaketRouter : Closeable, Breakable {
    fun route(condition: (PaketPeeking) -> Boolean): PaketReceiver
    fun default(): PaketReceiver
}

private class PaketRouterImpl(receiver: PaketReceiver) : PaketRouter, PaketRouterBase(receiver) {

    private val routes = Channel<DynamicRoute>(Channel.UNLIMITED)
    private var default: Route? = null

    private val acc = mutableListOf<DynamicRoute>()

    override fun select(peeking: PaketPeeking): Route? {
        try {
            while (true) {
                val route = routes.poll() ?: break
                if (!route.broken) {
                    acc += route
                    if (route.condition(peeking)) {
                        return route
                    }
                }
            }
        } finally {
            for (route in acc)
                routes.offer(route)
            acc.clear()
        }
        val default = default
        return if (default != null && !default.broken) default else null
    }

    override fun route(condition: (PaketPeeking) -> Boolean): PaketReceiver {
        val result = DynamicRoute(condition)
        routes.offer(result)
        return result
    }

    override fun default(): PaketReceiver {
        val default = default
        return if (default == null || default.broken) {
            val result = Route()
            this.default = result
            result
        } else {
            default
        }
    }

    private inner class DynamicRoute(val condition: (PaketPeeking) -> Boolean) : Route()
}
