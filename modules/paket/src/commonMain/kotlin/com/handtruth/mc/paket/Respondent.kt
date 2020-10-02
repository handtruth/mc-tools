package com.handtruth.mc.paket

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@DslMarker
annotation class PaketTreeDsl

@PublishedApi
internal fun <E : Enum<E>> respondent(values: Array<E>, name: String, ts: PaketTransmitter, context: CoroutineContext) =
    Respondent(name, context, values, ts)

@PaketTreeDsl
suspend inline fun <reified E : Enum<E>> respondent(
    ts: PaketTransmitter, name: String = "root", crossinline builder: Respondent<E>.() -> Unit
) {
    coroutineScope {
        respondent(enumValues<E>(), name, ts, coroutineContext).apply(builder)
    }
}

class Respondent<E : Enum<E>> internal constructor(
    branchName: String,
    parentContext: CoroutineContext,
    private val values: Array<E>,
    private val ts: PaketTransmitter
) : CoroutineScope, PaketSender {
    val variants = values.asList()

    private val jobs: Array<Job>
    init {
        val job = Job()
        jobs = Array(values.size + 1) { job }
    }

    private fun nextName(context: CoroutineName?, name: String): CoroutineName {
        return if (context == null)
            CoroutineName(name)
        else
            CoroutineName("${context.name}/$name")
    }

    override val coroutineContext =
        parentContext + Job(parentContext[Job]) + nextName(parentContext[CoroutineName], branchName)

    private val router = ts.asRouter()

    @PaketTreeDsl
    inline fun <reified T : Enum<T>> branch(bid: E, builder: Respondent<T>.() -> Unit) {
        branch(bid, enumValues<T>()).apply(builder)
    }

    @PublishedApi
    internal fun <T : Enum<T>> branch(bid: E, values: Array<T>): Respondent<T> {
        val id = bid.ordinal
        val ts = router.route { it.idOrdinal == id }
        val box = Respondent(bid.name, coroutineContext, values, BranchPaketTransmitter(bid, ts))
        val job = box.coroutineContext[Job]!!
        jobs[id].cancel()
        job.invokeOnCompletion { ts.close() }
        jobs[id] = job
        return box
    }

    @PaketTreeDsl
    fun node(bid: E, action: suspend CoroutineScope.(PaketTransmitter) -> Unit) {
        val id = bid.ordinal
        val ts = router.route { it.idOrdinal == id }
        val job = launch(nextName(coroutineContext[CoroutineName], bid.name)) {
            action(ts)
        }
        jobs[id].cancel()
        job.invokeOnCompletion { ts.close() }
        jobs[id] = job
    }

    @PaketTreeDsl
    fun transmitter(bid: E, action: suspend CoroutineScope.(PaketTransmitter) -> Unit) {
        node(bid) {
            action(BranchPaketTransmitter(bid, ts))
        }
    }

    @PaketTreeDsl
    fun <P : Paket> receive(paket: P, action: suspend CoroutineScope.(P) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        node(paket.id as E) { ts ->
            try {
                coroutineScope<Nothing> {
                    ts.receiveAll {
                        peek(paket)
                        action(paket)
                        paket.clear()
                    }
                }
            } finally {
                paket.recycle()
            }
        }
    }

    @PaketTreeDsl
    fun <P : Paket> receive(source: PaketSource<P>, action: suspend CoroutineScope.(P) -> Unit) {
        receive(source.produce(), action)
    }

    @PaketTreeDsl
    fun <P : Paket> reply(paket: P, action: suspend CoroutineScope.(P) -> Paket) {
        @Suppress("UNCHECKED_CAST")
        node(paket.id as E) { ts ->
            try {
                coroutineScope<Nothing> {
                    ts.receiveAll {
                        peek(paket)
                        ts.send(action(paket))
                        paket.clear()
                    }
                }
            } finally {
                paket.recycle()
            }
        }
    }

    @PaketTreeDsl
    fun <P : Paket> reply(source: PaketSource<P>, action: suspend CoroutineScope.(P) -> Paket) {
        reply(source.produce(), action)
    }

    @PaketTreeDsl
    fun ignore(bid: E) {
        node(bid) { it.dropAll() }
    }

    @PaketTreeDsl
    fun default(action: suspend CoroutineScope.(PaketTransmitter) -> Unit) {
        val id = values.size
        val ts = router.default()
        val job = launch(nextName(coroutineContext[CoroutineName], "?")) {
            action(ts)
        }
        jobs[id].cancel()
        job.invokeOnCompletion { ts.close() }
        jobs[id] = job
    }

    @PaketTreeDsl
    fun replyDefault(action: suspend CoroutineScope.(PaketPeeking) -> Paket) {
        default {
            it.replyAll { action(this) }
        }
    }

    @PaketTreeDsl
    fun receiveDefault(action: suspend CoroutineScope.(PaketPeeking) -> Unit) {
        default {
            it.receiveAll { action(this) }
        }
    }

    @PaketTreeDsl
    override suspend fun send(paket: Paket) {
        ts.send(paket)
    }

    override val broken get() = ts.broken

    override fun close() {
        cancel()
    }
}
