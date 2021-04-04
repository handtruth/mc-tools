package com.handtruth.mc.paket

import com.handtruth.mc.paket.codec.Codec
import com.handtruth.mc.paket.codec.EnumCodec
import com.handtruth.mc.paket.codec.StringCodec
import com.handtruth.mc.paket.transmitter.*
import com.handtruth.mc.paket.util.*
import com.handtruth.mc.paket.util.StructPaket
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

@DslMarker
public annotation class PaketTree

public class ProtocolDomain<T>(
    override val coroutineContext: CoroutineContext,
    private val splitter: TransmitSplitter<T>
) : CoroutineScope {
    private class Task(val job: Job, val transmitter: Transmitter)

    private val tasks = hashMapOf<T, Task>()
    private val default: AtomicRef<Task?> = atomic(null)

    private val lock = reentrantLock()

    private fun addTask(key: T, task: Job, transmitter: Transmitter) {
        lock.withLock {
            tasks.put(key, Task(task, transmitter))?.let {
                it.transmitter.close()
                it.job.cancel()
            }
        }
    }

    private fun removeTask(key: T): Boolean {
        return lock.withLock {
            val task = tasks.remove(key)
            if (task != null) {
                task.transmitter.close()
                task.job.cancel()
                true
            } else {
                false
            }
        }
    }

    private fun setDefault(task: Job, transmitter: Transmitter) {
        val old = default.getAndSet(Task(task, transmitter))
        if (old != null) {
            old.transmitter.close()
            old.job.cancel()
        }
    }

    private fun stopDefault() {
        val old = default.getAndSet(null)
        if (old != null) {
            old.transmitter.close()
            old.job.cancel()
        }
    }

    @PaketTree
    public fun branch(key: T, routine: suspend CoroutineScope.(Transmitter) -> Unit) {
        val transmitter = splitter.spill(key)
        val task = launch(start = CoroutineStart.LAZY) {
            try {
                routine(transmitter)
            } finally {
                transmitter.close()
            }
        }
        addTask(key, task, transmitter)
        task.start()
    }

    @PaketTree
    public fun default(routine: suspend CoroutineScope.(SpillTransmitter<T>) -> Unit) {
        val transmitter = splitter.default()
        val task = launch(start = CoroutineStart.LAZY) {
            try {
                routine(transmitter)
            } finally {
                transmitter.close()
            }
        }
        setDefault(task, transmitter)
        task.start()
    }

    public fun cancelBranch(key: T) {
        removeTask(key)
    }

    public fun cancelDefault() {
        stopDefault()
    }

    @PaketTree
    @JvmName("receivePaket")
    public fun receive(key: T, block: suspend CoroutineScope.(Extractor) -> Unit) {
        branch(key) { ts ->
            ts.receiveAll { block(it) }
        }
    }

    @PaketTree
    @JvmName("replyPaket")
    public fun reply(key: T, block: suspend CoroutineScope.(Extractor) -> Paket) {
        branch(key) { ts ->
            ts.replyAll { block(it) }
        }
    }

    @PaketTree
    public inline fun <reified S> receive(
        key: T,
        noinline block: suspend CoroutineScope.(S) -> Unit
    ) {
        val request = StructPaket<S>()
        receive(key) {
            it.extract(request)
            block(request.item)
        }
    }

    @PaketTree
    public inline fun <reified Q, reified R> reply(
        key: T,
        noinline block: suspend CoroutineScope.(Q) -> R
    ) {
        val request = StructPaket<Q>()
        val response = StructPaket<R>()
        reply(key) {
            it.extract(request)
            response.item = block(request.item)
            response
        }
    }

    @PaketTree
    public inline fun <reified Q, reified R> barter(
        key: T,
        noinline block: suspend CoroutineScope.(Barter<Q, R>) -> Unit
    ) {
        branch(key) { ts ->
            ts.withBarter<Q, R> { block(it) }
        }
    }

    @PaketTree
    public inline fun <reified Q> sink(
        key: T,
        noinline block: suspend CoroutineScope.(Sink<Q>) -> Unit
    ) {
        branch(key) { ts ->
            ts.withSink<Q> { block(it) }
        }
    }

    @PaketTree
    public fun drop(key: T) {
        branch(key) { ts -> ts.dropAll() }
    }
}

@PaketTree
public suspend fun <T> domain(
    transmitter: Transmitter,
    keyCodec: Codec<T>,
    initial: T,
    block: suspend ProtocolDomain<T>.() -> Unit
) {
    val splitter = TransmitSplitter(keyCodec, initial, transmitter)
    try {
        coroutineScope {
            val domain = ProtocolDomain(kotlin.coroutines.coroutineContext, splitter)
            domain.block()
        }
    } finally {
        splitter.close()
    }
}

@PaketTree
public suspend fun namedDomain(transmitter: Transmitter, block: suspend ProtocolDomain<String>.() -> Unit) {
    domain(transmitter, StringCodec, "", block)
}

@PaketTree
public suspend inline fun <reified E : Enum<E>> enumeratedDomain(
    transmitter: Transmitter,
    noinline block: suspend ProtocolDomain<E>.() -> Unit
) {
    domain(transmitter, EnumCodec(), enumValues<E>()[0], block)
}

@PaketTree
public fun <T> ProtocolDomain<T>.named(key: T, block: suspend ProtocolDomain<String>.() -> Unit) {
    branch(key) {
        domain(it, StringCodec, "", block)
    }
}

@PaketTree
public inline fun <T, reified E : Enum<E>> ProtocolDomain<T>.enumerated(
    key: T,
    noinline block: suspend ProtocolDomain<E>.() -> Unit
) {
    branch(key) {
        domain(it, EnumCodec(), enumValues<E>()[0], block)
    }
}
