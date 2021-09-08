@file:Suppress("FunctionName")

package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.Codec
import com.handtruth.mc.paket.codec.EnumCodec
import com.handtruth.mc.paket.codec.StringCodec
import com.handtruth.mc.paket.field.AnyField
import com.handtruth.mc.paket.transmitter.Receiver
import com.handtruth.mc.paket.transmitter.Sender
import com.handtruth.mc.paket.transmitter.Transmitter
import com.handtruth.mc.paket.transmitter.synchronised
import io.ktor.utils.io.core.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public interface Splitter<T> : Closeable {
    public fun spill(key: T): Closeable
    public fun default(): Closeable
}

public interface SpillSender<T> : Sender {
    public var sentKey: T
}

public interface SendSplitter<T> : Splitter<T> {
    override fun spill(key: T): Sender
    public override fun default(): SpillSender<T>
}

public interface SpillReceiver<T> : Receiver {
    public val receivedKey: T
}

public interface SpillTransmitter<T> : Transmitter, SpillSender<T>, SpillReceiver<T>

public interface ReceiveSplitter<T> : Splitter<T> {
    override fun spill(key: T): Receiver
    public override fun default(): SpillReceiver<T>
}

public interface TransmitSplitter<T> : SendSplitter<T>, ReceiveSplitter<T> {
    override fun spill(key: T): Transmitter
    public override fun default(): SpillTransmitter<T>
}

public fun <T> SendSplitter(keyCodec: Codec<T>, initial: T, sender: Sender): SendSplitter<T> =
    SendSplitterImpl(keyCodec, initial, sender)

public fun <T> ReceiveSplitter(keyCodec: Codec<T>, initial: T, receiver: Receiver): ReceiveSplitter<T> =
    ReceiveSplitterImpl(keyCodec, initial, receiver)

public fun <T> TransmitSplitter(
    keyCodec: Codec<T>,
    initial: T,
    transmitter: Transmitter
): TransmitSplitter<T> =
    TransmitSplitterImpl(ReceiveSplitter(keyCodec, initial, transmitter), SendSplitter(keyCodec, initial, transmitter))

public inline fun <reified E : Enum<E>> Sender.splitBy(): SendSplitter<E> =
    SendSplitter(EnumCodec(), enumValues<E>()[0], this)

public fun Sender.splitNamed(): SendSplitter<String> =
    SendSplitter(StringCodec, "", this)

public inline fun <reified E : Enum<E>> Receiver.splitBy(): ReceiveSplitter<E> =
    ReceiveSplitter(EnumCodec(), enumValues<E>()[0], this)

public fun Receiver.splitNamed(): ReceiveSplitter<String> =
    ReceiveSplitter(StringCodec, "", this)

public inline fun <reified E : Enum<E>> Transmitter.splitBy(): TransmitSplitter<E> =
    TransmitSplitter(EnumCodec(), enumValues<E>()[0], this)

public fun Transmitter.splitNamed(): TransmitSplitter<String> =
    TransmitSplitter(StringCodec, "", this)

internal class SendSplitterImpl<T>(
    keyCodec: Codec<T>,
    initial: T,
    sender: Sender
) : SendSplitter<T> {
    private val keyField = AnyField(keyCodec, initial)

    private var keyFieldSize = 0

    private val sender = sender.synchronised()

    private val branches = hashMapOf<T, BranchSender>()

    private val lock = reentrantLock()

    private val current: AtomicRef<BaseSender?> = atomic(null)

    override fun spill(key: T): Sender = lock.withLock {
        val old = branches[key]
        if (old == null || !old.canSend) {
            val new = BranchSender(key)
            branches[key] = new
            new
        } else {
            old
        }
    }

    override fun close() {
        sender.close()
    }

    private val header = Paket(mutableListOf(keyField))

    private var default: DefaultSender? = null

    override fun default(): SpillSender<T> {
        val result = default
        return if (result === null) {
            val sender = DefaultSender()
            default = sender
            sender
        } else {
            result
        }
    }

    private abstract inner class BaseSender : Sender {
        override val isPrepared: Boolean get() = current.value === this

        override val collected: Int get() = sender.collected - keyFieldSize

        suspend fun prepareWith(key: T) {
            sender.prepare()
            current.value = this
            keyField.value = key
            insert(header)
            keyFieldSize = sender.collected
        }

        override suspend fun commit() {
            if (!isPrepared) {
                return
            }
            keyFieldSize = 0
            current.value = null
            sender.commit()
        }

        override fun rollback() {
            if (!isPrepared) {
                return
            }
            keyFieldSize = 0
            current.value = null
            sender.rollback()
        }

        override fun insert(paket: Paket) {
            check(isPrepared) { "not prepared" }
            sender.insert(paket)
        }
    }

    private inner class BranchSender(val key: T) : BaseSender() {
        private val attached = atomic(false)

        override val canSend: Boolean get() = attached.value && sender.canSend

        override suspend fun prepare() {
            prepareWith(key)
        }

        override fun close() {
            attached.value = false
            rollback()
        }
    }

    private inner class DefaultSender : SpillSender<T>, BaseSender() {
        override var sentKey: T = keyField.value

        override val canSend: Boolean get() = default === this && sender.canSend

        override val isPrepared: Boolean get() = current.value === this

        override val collected: Int get() = sender.collected - keyFieldSize

        override suspend fun prepare() {
            prepareWith(sentKey)
        }

        override fun close() {
            default = null
            rollback()
        }
    }
}

internal class ReceiveSplitterImpl<T>(
    keyCodec: Codec<T>,
    initial: T,
    private val receiver: Receiver
) : ReceiveSplitter<T> {
    private val keyField = AnyField(keyCodec, initial)

    private val conductor = BroadcastChannel<Unit>(Channel.CONFLATED)
    private val mutex = Mutex()
    private val current: AtomicRef<BaseReceiver?> = atomic(null)

    private val branches = hashMapOf<T, BranchReceiver>()
    private val lock = reentrantLock()

    private val header = Paket(mutableListOf(keyField))

    private val default: AtomicRef<DefaultReceiver?> = atomic(null)

    private suspend fun select(): Unit = mutex.withLock {
        if (receiver.isCaught && current.value?.canReceive == true) {
            return
        }
        while (true) {
            if (receiver.isCaught) {
                receiver.drop()
            }
            receiver.catch()
            receiver.extract(header)
            val branch = lock.withLock { branches[keyField.value] ?: default.value }
            if (branch != null) {
                current.value = branch
                break
            }
        }
        conductor.trySend(Unit)
    }

    override fun spill(key: T): Receiver {
        return lock.withLock {
            val old = branches[key]
            if (old == null || !old.canReceive) {
                val new = BranchReceiver(key)
                branches[key] = new
                new
            } else {
                old
            }
        }
    }

    override fun default(): SpillReceiver<T> {
        return lock.withLock {
            val old = default.value
            if (old == null) {
                val new = DefaultReceiver()
                default.value = new
                new
            } else {
                old
            }
        }
    }

    override fun close() {
        receiver.close()
    }

    private abstract inner class BaseReceiver : Receiver {
        override val isCaught: Boolean get() = current.value === this

        override suspend fun catch() {
            if (isCaught) {
                return
            }
            val subscription = conductor.openSubscription()
            try {
                while (!isCaught) {
                    select()
                    subscription.receive()
                }
            } finally {
                subscription.cancel()
            }
        }

        override suspend fun drop() {
            if (!isCaught) {
                return
            }
            mutex.withLock {
                current.value = null
                receiver.drop()
                conductor.trySend(Unit)
            }
        }

        override val remaining: Int get() = receiver.remaining

        override fun extract(paket: Paket) {
            check(isCaught) { "not caught" }
            receiver.extract(paket)
        }
    }

    private inner class BranchReceiver(val key: T) : BaseReceiver() {
        private val attached = atomic(true)

        override val canReceive: Boolean get() = attached.value && receiver.canReceive

        override fun close() {
            if (attached.value) {
                lock.withLock {
                    attached.value = false
                    branches.remove(key)
                }
                if (isCaught) {
                    current.value = null
                    conductor.trySend(Unit)
                }
            }
        }
    }

    private inner class DefaultReceiver : SpillReceiver<T>, BaseReceiver() {
        override var receivedKey: T = keyField.value

        override val canReceive: Boolean get() = default.value === this && receiver.canReceive

        override suspend fun catch() {
            super.catch()
            receivedKey = keyField.value
        }

        override fun close() {
            if (default.value === this) {
                lock.withLock {
                    default.value = null
                }
                if (isCaught) {
                    current.value = null
                    conductor.trySend(Unit)
                }
            }
        }
    }
}

internal class TransmitSplitterImpl<T>(
    private val receive: ReceiveSplitter<T>,
    private val send: SendSplitter<T>
) : TransmitSplitter<T> {
    override fun spill(key: T): Transmitter = Transmitter(receive.spill(key), send.spill(key))

    override fun default(): SpillTransmitter<T> {
        val receiver = receive.default()
        val sender = send.default()

        return object : SpillTransmitter<T>, SpillReceiver<T> by receiver, SpillSender<T> by sender {
            override fun close() {
                receiver.close()
                sender.close()
            }
        }
    }

    override fun close() {
        receive.close()
        send.close()
    }
}
