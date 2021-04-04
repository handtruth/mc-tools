package com.handtruth.mc.paket.ctest

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.string
import com.handtruth.mc.paket.field.field
import com.handtruth.mc.paket.transmitter.Sender
import com.handtruth.mc.paket.transmitter.send
import com.handtruth.mc.paket.transmitter.synchronised
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.jetbrains.kotlinx.lincheck.verifier.linearizability.LinearizabilityVerifier

@StressCTest(verifier = LinearizabilityVerifier::class)
class SynchronizedSenderTest : VerifierState() {
    val channel = ByteChannel()
    val sender = Sender(channel).synchronised()

    class StringMessage(message: String) : Paket() {
        init {
            field(string, message)
        }
    }

    @Operation
    fun send(string: String) {
        runBlocking {
            sender.send {
                it.insert(StringMessage(string))
            }
        }
    }

    data class Bytes(val byteArray: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Bytes
            if (!byteArray.contentEquals(other.byteArray)) return false
            return true
        }

        override fun hashCode(): Int {
            return byteArray.contentHashCode()
        }
    }

    override fun extractState(): Any {
        val result = ByteArray(channel.availableForRead)
        runBlocking {
            channel.readAvailable(result)
        }
        return Bytes(result)
    }
}
