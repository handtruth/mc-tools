package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.test.dispatcher.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkTest {

    enum class IDS {
        First, Second
    }

    object FirstPaket : SinglePaket<FirstPaket>() {
        override val id = IDS.First
    }

    object SecondPaket : SinglePaket<SecondPaket>() {
        override val id = IDS.Second
    }

    private fun CoroutineScope.forSocket(socket: Socket, paketA: Paket, paketB: Paket): PaketTransmitter {
        val ts = PaketTransmitter(socket.openReadChannel(), socket.openWriteChannel()).asSynchronized()
        launch {
            var count = 0
            ts.replyAll {
                peek(paketA)
                println("${paketA.id}: ${count++}")
                paketB
            }
        }
        return ts
    }

    // @Test
    @KtorExperimentalAPI
    fun localhostTest() = testSuspend {
        val selector = ActorSelectorManager(Dispatchers.IO)
        // val server = aSocket(selector).tcp().bind("127.0.0.1", 0)
        // val address = server.localAddress as InetSocketAddress
        val client = aSocket(selector).tcp().connect("127.0.0.1", 4545)
        // val socket = server.accept()
        val ts = forSocket(client, SecondPaket, FirstPaket)
        // forSocket(socket, SecondPaket, FirstPaket)
        println("BEGIN")
        ts.send(FirstPaket)
    }
}
