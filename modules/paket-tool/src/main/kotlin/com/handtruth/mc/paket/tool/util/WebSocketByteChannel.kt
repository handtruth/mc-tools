package com.handtruth.mc.paket.tool.util

import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.send
import io.ktor.utils.io.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.Closeable

interface WebSocketByteChannel : Closeable {
    val output: ByteReadChannel
    val input: ByteWriteChannel
}

fun WebSocketSession.asChannel(): WebSocketByteChannel = WebSocketByteChannelImpl(this)

private class WebSocketByteChannelImpl(private val ws: WebSocketSession) : WebSocketByteChannel {
    override val output = ByteChannel()
    override val input = ByteChannel()

    init {
        ws.launch {
            try {
                coroutineScope {
                    launch {
                        while (true) {
                            val data = ByteArray(output.availableForRead)
                            if (output.readAvailable(data) == -1)
                                break
                            ws.send(data)
                        }
                    }
                    launch {
                        while (true) {
                            val data = ws.incoming.receive().data
                            input.writeFully(data)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                close()
            }
        }
    }

    override fun close() {
        input.cancel(null)
        output.cancel(null)
        ws.cancel()
    }
}
