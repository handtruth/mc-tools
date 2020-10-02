package com.handtruth.mc.paket

fun PaketReceiver.split(
    count: Int,
    splitter: (PaketPeeking) -> Int
): List<PaketReceiver> {
    val result = object : PaketStaticRouter(count, this) {
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

fun PaketTransmitter.split(
    count: Int,
    splitter: (PaketPeeking) -> Int
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

internal abstract class PaketStaticRouter(private val count: Int, receiver: PaketReceiver) : PaketRouterBase(receiver) {

    abstract fun splitter(peeking: PaketPeeking): Int

    private val _children = List(count) { Route() }
    val children: List<PaketReceiver> get() = _children

    override fun select(peeking: PaketPeeking): Route? {
        return when (val next = splitter(peeking)) {
            -1 -> null
            in 0 until count -> _children[next]
            else -> throw IndexOutOfBoundsException()
        }
    }
}
