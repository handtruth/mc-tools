package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.int64
import com.handtruth.mc.paket.fields.string
import com.soywiz.korio.lang.printStackTrace
import io.ktor.test.dispatcher.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.io.Bytes
import kotlin.coroutines.coroutineContext
import kotlin.test.Test

class ServerTest {

    @OptIn(InternalCoroutinesApi::class)
    private val handler = CoroutineExceptionHandler { context, thr ->
        val name = context[CoroutineName]!!.name
        println("$name ERROR: $thr")
        thr.printStackTrace()
        handleCoroutineException(context, thr)
    }

    @Test
    fun serverTest() = testSuspend(handler + Dispatchers.Default) {
        val leftStream = Channel<Bytes>()
        val rightStream = Channel<Bytes>()
        val serverTs = PaketTransmitter(leftStream, rightStream)
        val clientTs = PaketTransmitter(rightStream, leftStream)

        val job = launch {
            execServer(serverTs)
        }

        repeat(1000) {
            execClient(clientTs)
        }
        job.cancel()
    }

    enum class IDS {
        First, Second, Third
    }

    class FirstPaket : Paket() {
        override val id = IDS.First

        var payload by int64()

        companion object : PaketCreator<FirstPaket> {
            override fun produce() = FirstPaket()
        }
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

        companion object : PaketCreator<TwoPaket> {
            override fun produce() = TwoPaket()
        }
    }

    object ThreePaket : Paket() {
        override val id = SubIDS.Three
    }

    object ThirdPaket : Paket() {
        override val id = IDS.Third
    }

    private suspend fun log(message: String) {
        val name = coroutineContext[CoroutineName]!!.name
        println("$name: $message")
    }

    private suspend fun execClient(ts: PaketTransmitter) {
        val c = corespondent<IDS>(ts)
        c.send(FirstPaket().apply { payload = 232323232323 })
        c.branch<SubIDS>(IDS.Second) send OnePaket
        val data = c.branch<SubIDS>(IDS.Second).request(TwoPaket().apply { data = "hi lol" }) { peek(TwoPaket).data }
        println("response: $data")
        c.branch<SubIDS>(IDS.Second) send ThreePaket
        c send ThirdPaket
    }

    private suspend fun execServer(ts: PaketTransmitter) {
        try {
            respondent<IDS>(ts) {
                var rid = 0
                receive(FirstPaket) { paket ->
                    log("received ${rid++}: ${paket.payload}")
                }

                branch<SubIDS>(IDS.Second) {
                    ignore(SubIDS.One)

                    var kid = 0
                    reply(TwoPaket) { paket ->
                        log("reply ${kid++}: ${paket.data}")
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("OHH: $e")
            e.printStackTrace()
            throw e
        }
    }
}
