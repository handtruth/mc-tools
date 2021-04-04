package com.handtruth.mc.paket.transmitter

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.util.Barter
import com.handtruth.mc.paket.util.Sink
import com.handtruth.mc.paket.util.StructPaket
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

public fun Transmitter(receiver: Receiver, sender: Sender): Transmitter =
    object : Transmitter, Receiver by receiver, Sender by sender {
        override fun close() {
            receiver.close()
            sender.close()
        }
    }

public fun Receiver(input: ByteReadChannel): Receiver = ByteChannelReceiver(input)

public fun Sender(output: ByteWriteChannel): Sender = ByteChannelSender(output)

public fun Transmitter(input: ByteReadChannel, output: ByteWriteChannel): Transmitter =
    Transmitter(Receiver(input), Sender(output))

public fun Transmitter(channel: ByteChannel): Transmitter = Transmitter(channel, channel)

public fun Receiver.synchronised(): Receiver = SynchronisedReceiver(this)

public fun Sender.synchronised(): Sender = SynchronisedSender(this)

public fun Transmitter.synchronised(): Transmitter =
    Transmitter((this as Receiver).synchronised(), (this as Sender).synchronised())

public suspend inline fun <R> Receiver.receive(block: (Extractor) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    catch()
    try {
        return block(this)
    } finally {
        drop()
    }
}

public suspend inline fun <R> Sender.send(block: (Inserter) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    prepare()
    try {
        val result = block(this)
        commit()
        return result
    } catch (thr: Throwable) {
        rollback()
        throw thr
    }
}

public suspend inline fun Transmitter.reply(block: (Extractor) -> Paket) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    catch()
    try {
        val response = block(this)
        send(response)
    } finally {
        drop()
    }
}

@JvmName("receiveAllPaket")
public suspend inline fun Receiver.receiveAll(block: (Extractor) -> Unit) {
    while (canReceive) {
        receive(block)
    }
    close()
}

public suspend inline fun <reified T> Receiver.receiveAll(block: (T) -> Unit) {
    while (canReceive) {
        val paket = StructPaket<T>()
        receive(paket)
        block(paket.item)
    }
    close()
}

@JvmName("replyAllPaket")
public suspend inline fun Transmitter.replyAll(block: (Extractor) -> Paket) {
    while (canTransmit) {
        reply(block)
    }
    close()
}

public suspend inline fun <reified Q, reified R> Transmitter.replyAll(block: (Q) -> R) {
    while (canTransmit) {
        val request = StructPaket<Q>()
        val response = StructPaket<R>()
        receive(request)
        response.item = block(request.item)
        send(response)
    }
    close()
}

public suspend fun Sender.send(paket: Paket) {
    if (!isPrepared) {
        prepare()
    }
    insert(paket)
    commit()
}

public suspend fun Receiver.receive(paket: Paket) {
    catch()
    extract(paket)
    drop()
}

public suspend fun Transmitter.exchange(query: Paket, result: Paket) {
    send(query)
    receive(result)
}

public inline fun <reified Q, reified R> Transmitter.openBarter(
    query: KSerializer<Q> = serializer(),
    result: KSerializer<R> = serializer()
): Barter<Q, R> = Barter(this, query, result)

public inline fun <reified Q, reified R> Transmitter.withBarter(
    query: KSerializer<Q> = serializer(),
    result: KSerializer<R> = serializer(),
    block: (Barter<Q, R>) -> Unit
) {
    use { block(openBarter(query, result)) }
}

public inline fun <reified T> Sender.openSink(serializer: KSerializer<T> = serializer()): Sink<T> {
    return Sink(this, serializer)
}

public inline fun <reified T> Sender.withSink(serializer: KSerializer<T> = serializer(), block: (Sink<T>) -> Unit) {
    use { block(openSink(serializer)) }
}

public suspend fun Receiver.dropAll() {
    while (canReceive) {
        catch()
        drop()
    }
    close()
}

public inline val Transmitter.canTransmit: Boolean get() = canReceive && canSend
