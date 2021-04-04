package com.handtruth.mc.paket.transmitter

import com.handtruth.mc.paket.ExhaustedChannelException
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.util.readUZInt
import com.handtruth.mc.util.writeUZInt
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

internal class ByteChannelReceiver(private val channel: ByteReadChannel) : Receiver {
    private var data: ByteReadPacket? = null

    override val isCaught: Boolean get() = data != null

    override val canReceive: Boolean get() = isCaught || !channel.isClosedForRead

    override val remaining: Int get() = data?.remaining?.toInt() ?: -1

    override suspend fun catch() {
        if (!isCaught) {
            if (channel.isClosedForRead) {
                throw ExhaustedChannelException("receiver exhausted")
            }
            val size = channel.readUZInt().toInt()
            data = channel.readPacket(size)
        }
    }

    override suspend fun drop() {
        data?.let {
            it.close()
            data = null
        }
    }

    override fun extract(paket: Paket) {
        val data = checkNotNull(data) { "paket not caught" }
        paket.read(data)
    }

    override fun close() {
        data?.close()
        channel.cancel()
    }
}

internal class ByteChannelSender(private val channel: ByteWriteChannel) : Sender {
    private var data: BytePacketBuilder? = null

    override val canSend: Boolean get() = !channel.isClosedForWrite

    override val isPrepared: Boolean get() = data != null

    override val collected: Int get() = data?.size ?: -1

    override suspend fun prepare() {
        if (!isPrepared) {
            data = BytePacketBuilder()
        }
    }

    override suspend fun commit() {
        val prepared = data ?: return
        if (channel.isClosedForWrite) {
            throw ExhaustedChannelException("sender closed")
        }
        channel.writeUZInt(prepared.size.toUInt())
        prepared.build().use { channel.writePacket(it) }
        prepared.close()
        channel.flush()
        data = null
    }

    override fun rollback() {
        val prepared = data ?: return
        prepared.close()
        data = null
    }

    override fun insert(paket: Paket) {
        val data = checkNotNull(data)
        paket.write(data)
    }

    override fun close() {
        data?.close()
        channel.close()
    }
}
