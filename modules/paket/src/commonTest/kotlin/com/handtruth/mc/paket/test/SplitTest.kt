package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.string
import com.handtruth.mc.paket.codec.szint
import com.handtruth.mc.paket.enumeratedDomain
import com.handtruth.mc.paket.util.splitBy
import com.handtruth.mc.paket.transmitter.Transmitter
import com.handtruth.mc.paket.transmitter.receive
import com.handtruth.mc.paket.transmitter.send
import io.ktor.test.dispatcher.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class SplitTest {

    enum class IDS {
        A, B, C
    }

    class A : Paket() {
        var message by string
    }

    class B : Paket() {
        var integer by szint
    }

    @Test
    fun splitTest() = testSuspend {
        val channel = ByteChannel()
        val ts = Transmitter(channel)
        val splitter = ts.splitBy<IDS>()
        val a = splitter.spill(IDS.A)
        val b = splitter.spill(IDS.B)

        b.send(B().apply { integer = 23 })
        b.close()
        a.send(A().apply { message = "Hello!" })

        val response = A()
        a.receive(response)
        assertEquals("Hello!", response.message)
    }

    @Serializable
    data class Request(val message: String = "", val actor: Int = 0)

    @Serializable
    data class Response(val code: Byte = 0, val list: List<String> = emptyList())

    fun domains() = testSuspend {
        val channel = ByteChannel()
        val ts = Transmitter(channel)
        enumeratedDomain<IDS>(ts) {
            receive<Request>(IDS.A) { request ->
            }

            reply<Request, Response>(IDS.B) { request ->
                Response(request.actor.toByte(), listOf(request.message))
            }

            barter<Request, Response>(IDS.C) { barter ->
                val response: Response = barter.exchange(Request("Lolka", 23))
                println(response)
            }

            default { ts ->
                ts.catch()
                error("ID #${ts.receivedKey} not supported")
            }
        }
    }
}
