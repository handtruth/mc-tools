package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.PaketReceiver
import com.handtruth.mc.paket.PaketSender
import com.handtruth.mc.paket.peek
import io.ktor.test.dispatcher.testSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.io.use
import kotlin.test.assertEquals

class ConcurrencyTest {

    // TODO: Peek is not concurrent. This is not normal.
    // @Test
    fun peekTest() = testSuspend {
        val paketA = ProtocolTest.paket
        val bytes = run {
            val bytes = ByteArrayOutput()
            PaketSender(bytes).use {
                it.send(paketA)
            }
            bytes.toByteArray()
        }
        PaketReceiver(ByteArrayInput(bytes)).use { rx ->
            rx.catchOrdinal()
            repeat(10) {
                val paket = rx.peek(ProtocolTest.ExamplePaket)
                assertEquals(paketA, paket)
            }
            coroutineScope {
                repeat(100) {
                    launch(Dispatchers.Default) {
                        repeat(100) {
                            val paket = rx.peek(ProtocolTest.ExamplePaket)
                            assertEquals(paketA, paket, "#$it")
                        }
                    }
                }
            }
        }
    }
}
