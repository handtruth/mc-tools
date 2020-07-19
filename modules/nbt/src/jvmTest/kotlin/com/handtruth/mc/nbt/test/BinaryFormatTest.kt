package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.nbt.add
import com.handtruth.mc.nbt.asNBTInput
import com.handtruth.mc.nbt.buildCompoundTag
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryFormatTest {

    private fun open(file: String) =
        javaClass.getResourceAsStream(file)!!.asNBTInput()

    @Test
    fun readPlayerData() {
        // Real example
        val tag = NBT.Default.read(open("66f3f777-edce-3c09-a5d2-6118f9b9e223.dat"))
        println(tag)
    }

    @Test
    fun readWriteBigNBT() {
        val actual = NBT.Default.read(open("bigtest.nbt"))
        val expected = buildCompoundTag {
            "Level" {
                "shortTest" short 32767
                "longTest"(9223372036854775807L)
                "byteTest" byte 127
                ("byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, " +
                        "starting with n=0 (0, 62, 34, 16, 8, ...))")(ByteArray(1000) {
                        n -> ((n*n*255+n*7)%100).toByte()
                })
                "listTest (long)".listOf(11L, 12L, 13L, 14L, 15L)
                "floatTest"(0.49823147f)
                "doubleTest"(0.4931287132182315)
                "intTest"(2147483647)
                "listTest (compound)" compounds {
                    add {
                        "created-on"(1264099775885L)
                        "name"("Compound tag #0")
                    }
                    add {
                        "created-on" long (1264099775885L)
                        "name" string "Compound tag #1"
                    }
                }
                "nested compound test" {
                    "egg" {
                        "name" string "Eggbert"
                        "value" float 0.5f
                    }
                    "ham" {
                        "name"("Hampus")
                        "value"(0.75f)
                    }
                }
                "stringTest" string "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!"
            }
        }
        assertEquals(expected, actual)
        val expectedOutput = ByteArrayOutput()
        val actualOutput = ByteArrayOutput()
        NBT.Default.write(expectedOutput, expected)
        NBT.Default.write(actualOutput, actual)
        assertEquals(expectedOutput.toByteArray().toList(), actualOutput.toByteArray().toList())
    }

    @Test
    fun deserializeBigNBT() {
        val expected = bigNBTObject
        val tag = NBT.Default.read(open("bigtest.nbt"))
        val actual = NBT.Default.deserialize(BigNBTObject.serializer(), tag)
        assertEquals(expected, actual)
        println(actual)
    }

    @Test
    fun russian() {
        val expected = NamedProperty("Русский", 0.5f)
        val actual = NBT.Default.load(NamedProperty.serializer(), NBT.Default.dump(NamedProperty.serializer(), expected))
        assertEquals(expected, actual)
        println(actual)
    }

    @Test
    fun easy() {
        val input = open("easy.nbt")
        val root = buildCompoundTag {
            "hello world" {
                "name" ("Bananrama")
            }
        }
        val output = ByteArrayOutput()
        NBT.Default.write(output, root)
        val decode = ByteArrayInput(output.toByteArray())
        val easy1 = NBT.Default.read(decode)
        val easy2 = NBT.Default.read(input)
        assertEquals(easy1, easy2)
    }
}
