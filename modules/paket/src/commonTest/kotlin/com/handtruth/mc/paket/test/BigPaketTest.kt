package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.*
import com.handtruth.mc.paket.transmitter.Transmitter
import com.handtruth.mc.paket.transmitter.receive
import com.handtruth.mc.paket.transmitter.send
import io.ktor.test.dispatcher.*
import io.ktor.utils.io.*
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals

enum class ExampleEnum {
    One, Two, Three, Four
}

@Serializable
data class ExampleData(
    val string: String = "lolkek",
    val integer: Int = 23,
    val map: Map<String, List<Long>> = mapOf("lol" to listOf(2343587364879222L, 23, -23)),
    val boolean: Boolean = false,
    val char: Char = 'C',
    val struct: InnerExampleData = InnerExampleData()
) {
    @Serializable
    data class InnerExampleData(
        val double: Double = 0.5,
        val float: Float = 0.125f,
        val short: Short = 255,
    )
}

class BigPaket : Paket() {
    var fBoolean by boolean
    var fByte by byte
    var fChar by char
    var fDouble by double
    var fEnum by enum<ExampleEnum>()
    var fFloat by float
    var fList by list(string)
    var fNullable by nullable(string)
    var fNullable2 by nullable(string)
    var fShort by short
    var fString by string
    var fStruct by struct<ExampleData>()
    var fSzint by szint
    var fSzlong by szlong
    var fUzint by uzint
    var fUzlong by uzlong
}

class CustomFormatPacket : Paket() {
    var fBinaryFormat by binary<ExampleData>(ProtoBuf { })
    var fStringFormat by string<ExampleData>(Json { })
}

class BigPaketTest {
    @Test
    fun bigPacketTest() = testSuspend {
        val transmitter = Transmitter(ByteChannel())
        val expected = BigPaket().apply {
            fBoolean = false
            fByte = -12
            fChar = '?'
            fDouble = .6564e-5
            fEnum = ExampleEnum.Three
            fFloat = .125f
            fList = listOf("lol", "kek", "chebureck")
            fNullable = "something"
            fNullable2 = null
            fShort = 25565
            fString = "ABCDEFGIHJKLMNOPQRSTUVWXYZ".repeat(120)
            fStruct = ExampleData(
                string = "0123456789".repeat(60),
                integer = -42,
                map = mapOf(
                    "1" to listOf(1, 2, 3),
                    "2" to listOf(4, 5, 6),
                    "3" to listOf(7, 8, 9)
                ),
                boolean = true,
                char = '*',
                struct = ExampleData.InnerExampleData(
                    double = 4867.87,
                    float = 1337f,
                    short = -978
                )
            )
            fSzint = -1488
            fSzlong = 789545
            fUzint = 878u
            fUzlong = 8853uL
        }
        val actual = async {
            val actual = BigPaket()
            transmitter.catch()
            assertEquals(transmitter.remaining, expected.size)
            transmitter.receive(actual)
            actual
        }
        transmitter.send(expected)

        assertEquals(expected, actual.await())
    }

    @Test
    fun customFormatTest() = testSuspend {
        val transmitter = Transmitter(ByteChannel())
        val expected = CustomFormatPacket().apply {
            fBinaryFormat = ExampleData(
                string = "0123456789".repeat(60),
                integer = -42,
                map = mapOf(
                    "1" to listOf(1, 2, 3),
                    "2" to listOf(4, 5, 6),
                    "3" to listOf(7, 8, 9)
                ),
                boolean = true,
                char = '*',
                struct = ExampleData.InnerExampleData(
                    double = 4867.87,
                    float = 1337f,
                    short = -978
                )
            )
            fStringFormat = fBinaryFormat
        }
        val actual = async {
            val actual = CustomFormatPacket()
            transmitter.catch()
            transmitter.receive(actual)
            actual
        }
        transmitter.send(expected)

        assertEquals(expected, actual.await())
    }
}
