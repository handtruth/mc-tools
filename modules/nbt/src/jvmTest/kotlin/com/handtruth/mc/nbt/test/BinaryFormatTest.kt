package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.nbt.asNBTInput
import com.handtruth.mc.nbt.plus
import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.buildDynamic
import com.handtruth.mc.types.dynamic
import io.ktor.utils.io.core.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryFormatTest {

    private fun open(file: String) =
        javaClass.getResourceAsStream(file)!!.asNBTInput()

    val javaNBT = NBTBinaryCodec() + NBTSerialFormat()

    @Test
    fun readPlayerData() {
        // Real example
        val tag = javaNBT.readNamedBinary(open("66f3f777-edce-3c09-a5d2-6118f9b9e223.dat"))
        println(tag)
    }

    @Test
    fun readWriteBigNBT() {
        val actual = javaNBT.readNamedBinary(open("bigtest.nbt"))
        val expected = buildDynamic {
            "shortTest" assign 32767.toShort()
            "longTest" assign 9223372036854775807L
            "byteTest" assign 127.toByte()
            var keyName = "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, "
            keyName += "starting with n=0 (0, 62, 34, 16, 8, ...))"
            keyName assign ByteArray(1000) { n ->
                ((n * n * 255 + n * 7) % 100).toByte()
            }
            "listTest (long)" assign listOf(11L, 12L, 13L, 14L, 15L)
            "floatTest" assign 0.49823147f
            "doubleTest" assign 0.4931287132182315
            "intTest" assign 2147483647
            "listTest (compound)" assign buildList {
                dynamic {
                    "created-on" assign 1264099775885L
                    "name" assign "Compound tag #0"
                }
                dynamic {
                    "created-on" assign 1264099775885L
                    "name" assign "Compound tag #1"
                }
            }
            "nested compound test" {
                "egg" {
                    "name" assign "Eggbert"
                    "value" assign 0.5f
                }
                "ham" {
                    "name" assign "Hampus"
                    "value" assign 0.75f
                }
            }
            "stringTest" assign "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!"
        }
        assertEquals("Level", actual.first)
        assertDynamicEquals(expected, actual.second as Dynamic)
        val actualOutput = buildPacket {
            javaNBT.writeNamedBinary(this, "Level", actual.second)
        }
        assertDynamicEquals(expected, javaNBT.readNamedBinary(actualOutput).second as Dynamic)
    }

    @Test
    fun deserializeBigNBT() {
        val expected = bigNBTObject
        val tag = javaNBT.readNamedBinary(open("bigtest.nbt"))
        val actual = javaNBT.decodeFromNBT(Level.serializer(), tag.second)
        assertEquals(expected, actual)
        println(actual)
    }

    @Test
    fun russian() {
        val expected = NamedProperty("Русский", 0.5f)
        val actual = javaNBT.decodeFromByteArray(
            NamedProperty.serializer(),
            javaNBT.encodeToByteArray(NamedProperty.serializer(), expected)
        )
        assertEquals(expected, actual)
        println(actual)
    }

    @Test
    fun easy() {
        val input = open("easy.nbt")
        val root = buildDynamic {
            "name" assign "Bananrama"
        }
        buildPacket {
            javaNBT.writeNamedBinary(this, "hello world", root)
        }.use { decode ->
            val easy1 = javaNBT.readNamedBinary(decode)
            val easy2 = javaNBT.readNamedBinary(input)
            assertEquals(easy1, easy2)
        }
    }
}
