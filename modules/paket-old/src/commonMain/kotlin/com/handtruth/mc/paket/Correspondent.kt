package com.handtruth.mc.paket

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

inline fun <reified E : Enum<E>> correspondent(ts: PaketTransmitter): Correspondent<E> =
    correspondent(enumValues(), ts.asSynchronized())

@PublishedApi
internal fun <E : Enum<E>> correspondent(values: Array<E>, ts: PaketTransmitter): Correspondent<E> {
    return Correspondent(ts, values)
}

class Correspondent<E : Enum<E>> internal constructor(
    private val ts: PaketTransmitter,
    private val values: Array<E>
) : PaketSender {
    val variants = values.asList()

    private val router = ts.asRouter()

    private val branches = arrayOfNulls<Correspondent<*>?>(values.size)

    @PublishedApi
    internal fun <T : Enum<T>> branch(values: Array<T>, bid: E): Correspondent<T> {
        val id = bid.ordinal
        val it = branches[id]
        return if (it == null || it.broken) {
            val result = Correspondent(BranchPaketTransmitter(bid, router.route { it.idOrdinal == id }), values)
            branches[id] = result
            result
        } else {
            require(values contentEquals it.values) { "there is an open branch for other paket id type" }
            @Suppress("UNCHECKED_CAST")
            it as Correspondent<T>
        }
    }

    inline fun <reified T : Enum<T>> branch(bid: E): Correspondent<T> {
        return branch(enumValues(), bid)
    }

    private val mutex = Mutex()

    suspend fun <R> request(paket: Paket, block: PaketPeeking.() -> R): R {
        mutex.withLock {
            ts.send(paket)
            return ts.receive(block)
        }
    }

    override val broken get() = ts.broken

    override suspend infix fun send(paket: Paket) {
        mutex.withLock {
            router.send(paket)
        }
    }

    override fun close() {
        router.close()
    }
}
