package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.int64
import com.handtruth.mc.paket.fields.string
import io.ktor.test.dispatcher.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.io.Bytes
import kotlin.coroutines.coroutineContext
import kotlin.test.Test

class JvmServerTest {

    @Test
    fun serverTest() = testSuspend {
        val leftStream = Channel<Bytes>()
        val rightStream = Channel<Bytes>()
        val serverTs = PaketTransmitter(leftStream, rightStream)
        val clientTs = PaketTransmitter(rightStream, leftStream)

        val job = launch {
            execServer(serverTs)
        }

        execClient(clientTs)
        execClient(clientTs)
        execClient(clientTs)
        job.cancel()
    }

    enum class IDS {
        First, Second, Third
    }

    class FirstPaket : Paket() {
        override val id = IDS.First

        var payload by int64()
    }

    enum class SubIDS {
        One, Two, Three
    }

    object OnePaket : Paket() {
        override val id = SubIDS.One
    }

    class TwoPaket : Paket() {
        override val id = SubIDS.Two

        var data by string()
    }

    object ThreePaket : Paket() {
        override val id = SubIDS.Three
    }

    object ThirdPaket : Paket() {
        override val id = IDS.Third
    }

    suspend fun log(message: String) {
        val name = coroutineContext[CoroutineName]!!.name
        println("$name: $message")
    }

    private suspend fun execClient(ts: PaketTransmitter) {
        val c = correspondent<ServerTest.IDS>(ts)
        c.send(ServerTest.FirstPaket().apply { payload = 232323232323 })
        c.branch<ServerTest.SubIDS>(ServerTest.IDS.Second) send ServerTest.OnePaket
        val data = c.branch<ServerTest.SubIDS>(ServerTest.IDS.Second).request(ServerTest.TwoPaket().apply { data = "hi lol" }) {
            peek(
                ServerTest.TwoPaket
            ).data
        }
        println("response: $data")
        c.branch<ServerTest.SubIDS>(ServerTest.IDS.Second) send ServerTest.ThreePaket
        c send ServerTest.ThirdPaket
    }

    private suspend fun execServer(ts: PaketTransmitter) {
        respondent<IDS>(ts) {
            receive { paket: FirstPaket ->
                log("received: ${paket.payload}")
            }

            branch<SubIDS>(IDS.Second) {
                ignore(SubIDS.One)

                reply { paket: TwoPaket ->
                    log("reply: ${paket.data}")
                    paket.data = "ha-ha"
                    paket
                }

                node(SubIDS.Three) {
                    // do nothing
                }
            }

            receiveDefault {
                log("default action executed ${it.idOrdinal}")
            }
        }
    }
}
