package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.nbt.plus
import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.buildDynamic
import com.handtruth.mc.types.contentDeepHashCode
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializerTest {
    @Test
    fun serializeTest() {
        val player = Player(
            33,
            "Ktlo",
            Inventory(
                listOf(
                    Item("minecraft:stone", 34, Short.MIN_VALUE, mapOf("lol" to "kek", "popka" to "zopka")),
                    Item("minecraft:air", 0, 33, emptyMap())
                ),
                byteArrayOf(56, -35, 0, 98)
            ),
            floatArrayOf(33.5f, 89.654f, -85.0f)
        )
        val expected = buildDynamic {
            "id" assign 33
            "name" assign "Ktlo"
            "inventory" {
                "items" assign buildList<Dynamic> {
                    this += buildDynamic {
                        "id" assign "minecraft:stone"
                        "count" assign 34.toByte()
                        "durability" assign Short.MIN_VALUE
                        "pages" {
                            "lol" assign "kek"
                            "popka" assign "zopka"
                        }
                    }
                    this += buildDynamic {
                        "id" assign "minecraft:air"
                        "count" assign 0.toByte()
                        "durability" assign 33.toShort()
                        "pages" {
                            // Empty
                        }
                    }
                }
                "metadata" assign byteArrayOf(56, -35, 0, 98)
            }
            "moment" assign listOf(33.5f, 89.654f, -85.0f)
        }
        val actual = player2nbt(player)
        assertDynamicEquals(expected, actual)
        assertEquals(expected.contentDeepHashCode(), actual.contentDeepHashCode())
        println(actual)

        // Deserialize
        val actualPlayer = nbt2player(actual)
        assertEquals(player, actualPlayer)
        println(actualPlayer)
    }

    val javaNBT = NBTBinaryCodec(binaryConfig = NBTBinaryConfig.Java) + NBTSerialFormat()

    @Test
    fun notchianBigObject() {
        val expected = bigNBTObject
        val bytes = javaNBT.encodeToByteArray(Level.serializer(), expected)
        val actual = javaNBT.decodeFromByteArray(Level.serializer(), bytes)
        assertEquals(expected, actual)
    }

    @Serializable
    data class MyObject(val valueA: String, val valueB: String?)

    @Test
    fun nullableField() {
        val expected = MyObject("one", null)
        val data = javaNBT.encodeToByteArray(MyObject.serializer(), expected)
        val actual = javaNBT.decodeFromByteArray(MyObject.serializer(), data)
        assertEquals(expected, actual)
    }
}
