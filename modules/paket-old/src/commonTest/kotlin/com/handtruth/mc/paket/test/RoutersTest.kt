package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.soywiz.korio.lang.printStackTrace
import io.ktor.test.dispatcher.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.io.Bytes
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlin.time.seconds

class RoutersTest {

    enum class IDS {
        First, Second, Third
    }

    object FirstPaket : SinglePaket<FirstPaket>() {
        override val id = IDS.First
    }

    object SecondPaket : SinglePaket<SecondPaket>() {
        override val id = IDS.Second
    }

    object ThirdPaket : SinglePaket<ThirdPaket>() {
        override val id = IDS.Third
    }

    private val PaketPeeking.id get() = enumValues<IDS>()[idOrdinal]

    @Test
    fun routerTest() = testTimeout(5.seconds) {
        val channel = Channel<Bytes>()
        val master = PaketTransmitter(channel)
        val (slave1, slave2) = master filter { it.id != IDS.Third } split { it.id }
        launch(CoroutineName("first")) {
            slave1.catchOrdinal()
            slave1.peek(FirstPaket)
            slave1.receive(FirstPaket)
            println("first received 1")
            slave1.receive(FirstPaket)
            println("first received 2")
        }
        launch(CoroutineName("second")) {
            slave2.receive(SecondPaket)
            println("second received 1")
        }
        launch(CoroutineName("sender")) {
            slave1.send(FirstPaket)
            println("first sent 1")
            slave1.send(ThirdPaket)
            slave1.send(ThirdPaket)
            slave1.send(ThirdPaket)
            slave2.send(SecondPaket)
            println("second sent 1")
            slave1.send(ThirdPaket)
            slave2.send(FirstPaket)
            println("first sent 2")
        }
    }

    @Test
    fun stressRouterTest() = testTimeout(5.seconds) {
        val channel = Channel<Bytes>()
        val master = PaketTransmitter(channel)
        val (slave1, slave2, slave3) = master filter { it.id != IDS.Third } split { it.id }
        val dispatcher = EmptyCoroutineContext + Dispatchers.Default
        val count = 10000
        val recv1 = launch(dispatcher + CoroutineName("recv 1")) {
            repeat(count) {
                slave1.catchOrdinal()
                slave1.peek(FirstPaket)
                slave1.receive(FirstPaket)
                // println("recv 1: $it")
            }
            println("done recv 1")
        }
        val recv2 = launch(dispatcher + CoroutineName("recv 2")) {
            repeat(count) {
                slave2.receive(SecondPaket)
                // println("recv 2: $it")
            }
            println("done recv 2")
        }
        val send1 = launch(dispatcher + CoroutineName("send 1")) {
            repeat(count) {
                slave1.send(FirstPaket)
                // println("sent 1: $it")
            }
            println("done sent 1")
        }
        val send2 = launch(dispatcher + CoroutineName("sent 2")) {
            repeat(count) {
                slave2.send(SecondPaket)
                // println("sent 2: $it")
            }
            println("done sent 2")
        }
        val send3 = launch(dispatcher + CoroutineName("sent 3")) {
            repeat(count - 1) {
                slave3.send(ThirdPaket)
                // println("sent 3: $it")
            }
            println("done sent 3")
        }
        joinAll(recv1, recv2, send1, send2)
        send3.cancel()
    }

    @Test
    fun partRouterTest() = testTimeout(5.seconds) {
        val channel = Channel<Bytes>()
        val receiver = PaketReceiver(channel)
        val sender = PaketSender(channel).asSynchronized()
        val (rx1, rx2) = receiver split { it.id }
        val context = Dispatchers.Default
        val rx = launch(context) {
            repeat(10) {
                rx1.receive {
                    println("first rx #$it: begin")
                    peek(FirstPaket)
                }
                println("first rx #$it: end")
            }
        }
        val job = launch(context) {
            rx2.receiveAll {
                println("second rx")
                peek(SecondPaket)
            }
        }
        val tx = launch(context) {
            repeat(10) {
                sender.send(FirstPaket)
                println("first tx #$it")
            }
        }
        joinAll(rx, tx)
        println("done")
        job.cancel()
    }

    @Test
    fun filterTest() = testTimeout(5.seconds) {
        val main = PaketTransmitter(Channel<Bytes>()).asSynchronized() filter { it.id == IDS.Second }
        val dispatcher = Dispatchers.Default
        val count = 10000
        val send1 = launch(dispatcher + CoroutineName("send 1")) {
            repeat(count) {
                main.send(FirstPaket)
                // println("send 1: $it")
            }
            println("done send 1")
        }
        val send2 = launch(dispatcher + CoroutineName("send 2")) {
            repeat(count) {
                main.send(SecondPaket)
                // println("send 2: $it")
            }
            println("done send 2")
        }
        val send3 = launch(dispatcher + CoroutineName("send 3")) {
            repeat(count) {
                main.send(ThirdPaket)
                // println("send 3: $it")
            }
            println("done send 3")
        }
        val recv2 = launch(dispatcher + CoroutineName("recv 2")) {
            repeat(count) {
                main.catchOrdinal()
                main.peek(SecondPaket)
                main.receive(SecondPaket)
                // println("recv 2: $it")
            }
            println("done recv 2")
        }
        joinAll(send2, recv2)
        send1.cancel()
        send3.cancel()
    }

    @Test
    fun broadcastTest() = testTimeout(5.seconds) {
        val main = PaketTransmitter(Channel<Bytes>())
        val master = main.broadcast()
        val ts1 = master.openSubscription() filter { it.id == IDS.First }
        val ts2 = master.openSubscription() filter { it.id == IDS.Second }
        val ts3 = master.openSubscription() filter { it.id == IDS.Third }
        val dispatcher = EmptyCoroutineContext // Dispatchers.Default
        val count = 10000
        val send1 = launch(dispatcher + CoroutineName("send 1")) {
            repeat(count) {
                ts1.send(FirstPaket)
                // println("send 1: $it")
            }
            println("done send 1")
        }
        val send2 = launch(dispatcher + CoroutineName("send 2")) {
            repeat(count) {
                ts2.send(SecondPaket)
                // println("send 2: $it")
            }
            println("done send 2")
        }
        launch(dispatcher + CoroutineName("send 3")) {
            repeat(count) {
                ts3.send(ThirdPaket)
                // println("send 3: $it")
            }
            println("done send 3")
        }
        val recv1 = launch(dispatcher + CoroutineName("recv 1")) {
            repeat(count) {
                ts1.receive(FirstPaket)
                // println("recv 1: $it")
            }
            ts1.close()
            println("done recv 1")
        }
        val recv2 = launch(dispatcher + CoroutineName("recv 2")) {
            repeat(count) {
                ts2.catchOrdinal()
                ts2.peek(SecondPaket)
                ts2.receive(SecondPaket)
                // println("recv 2: $it")
            }
            ts2.close()
            println("done recv 2")
        }
        launch(dispatcher + CoroutineName("recv 3")) {
            val thr = assertFails {
                ts3.dropAll()
            }
            thr.printStackTrace()
            assertTrue {
                thr is ClosedReceiveChannelException || thr is CancellationException || thr is BrokenObjectException
            }
        }
        joinAll(send1, send2, recv1, recv2)
        println("done all")
        master.close()
    }

    @Test
    fun dynamicRouter() = testSuspend {
        val main = PaketTransmitter(Channel<Bytes>())
        val router = main.asRouter()
        val ts1 = router.route { it.id == IDS.First }
        val ts2 = router.route { it.id == IDS.Second }
        val ts3 = router.route { it.id == IDS.Third }
        val default = router.default()
        val dispatcher = EmptyCoroutineContext + Dispatchers.Default
        val count = 10000
        val send1 = launch(dispatcher + CoroutineName("send 1")) {
            repeat(count) {
                ts1.send(FirstPaket)
                // println("send 1: $it")
            }
            println("done send 1")
        }
        val send2 = launch(dispatcher + CoroutineName("send 2")) {
            repeat(count) {
                router.send(SecondPaket)
                // println("send 2: $it")
            }
            println("done send 2")
        }
        launch(dispatcher + CoroutineName("send 3")) {
            repeat(count) {
                ts3.send(ThirdPaket)
                // println("send 3: $it")
            }
            println("done send 3")
        }
        val recv1 = launch(dispatcher + CoroutineName("recv 1")) {
            repeat(count) {
                ts1.receive(FirstPaket)
                // println("recv 1: $it")
            }
            ts1.close()
            println("done recv 1")
        }
        val recv2 = launch(dispatcher + CoroutineName("recv 2")) {
            repeat(count / 2) {
                ts2.catchOrdinal()
                ts2.peek(SecondPaket)
                ts2.receive(SecondPaket)
                // println("recv 2: $it")
            }
            ts2.close()
            println("done recv 2")
        }
        launch(dispatcher + CoroutineName("recv 3")) {
            val thr = assertFails {
                ts3.dropAll()
            }
            thr.printStackTrace()
            assertTrue {
                thr is ClosedReceiveChannelException || thr is CancellationException || thr is BrokenObjectException
            }
        }
        val def = launch {
            repeat(count / 4) {
                default.receive(SecondPaket)
            }
            default.close()
            println("done recv default")
        }
        joinAll(send1, send2, recv1, recv2, def)
        println("done all")
        router.close()
    }
}
