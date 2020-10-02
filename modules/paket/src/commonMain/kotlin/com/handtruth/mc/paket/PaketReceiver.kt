@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface PaketPeeking {
    val idOrdinal: Int
    val size: Int
    fun peek(paket: Paket)
}

interface PaketReceiver : PaketPeeking, Breakable {
    val isCaught: Boolean
    suspend fun catchOrdinal(): Int
    suspend fun drop()
    suspend fun receive(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        peek(paket)
        drop()
    }
}

suspend inline fun <P : Paket> PaketReceiver.receive(source: PaketSource<P>) = source.produce().also { receive(it) }

fun <P : Paket> PaketPeeking.peek(source: PaketSource<P>) = source.produce().also { peek(it) }

suspend inline fun <R> PaketReceiver.receive(block: PaketPeeking.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (!isCaught)
        catchOrdinal()
    try {
        return block()
    } finally {
        drop()
    }
}

suspend inline fun PaketReceiver.receiveAll(block: PaketPeeking.() -> Unit): Nothing {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    try {
        while (true)
            receive(block)
    } finally {
        close()
    }
}

abstract class AbstractPaketReceiver : AbstractBreakable(), PaketReceiver {
    override var idOrdinal = -1
        get() = breakableAction { field }
        protected set
    override var size = -1
        get() = breakableAction { field }
        protected set
    override var isCaught = false
        get() = breakableAction { field }
        protected set
}

inline fun <reified E : Enum<E>> PaketPeeking.getId() = enumValues<E>()[idOrdinal]
suspend inline fun <reified E : Enum<E>> PaketReceiver.catchAs() = enumValues<E>()[catchOrdinal()]

suspend fun PaketReceiver.dropAll(): Nothing {
    while (true) drop()
}

fun PaketReceiver.asNotCloseable(): PaketReceiver = object : PaketReceiver by this {
    override fun close() {}
}
