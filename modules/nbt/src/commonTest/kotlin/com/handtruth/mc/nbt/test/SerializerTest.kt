package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.nbt.add
import com.handtruth.mc.nbt.buildCompoundTag
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializerTest {
    @Test
    fun serializeTest() {
        val player = Player(
            33, "Ktlo", Inventory(
                listOf(
                    Item("minecraft:stone", 34, Short.MIN_VALUE, mapOf("lol" to "kek", "popka" to "zopka")),
                    Item("minecraft:air", 0, 33, emptyMap())
                ),
                byteArrayOf(56, -35, 0, 98)
            ), floatArrayOf(33.5f, 89.654f, -85.0f)
        )
        val expected = buildCompoundTag {
            "id"(33)
            "name"("Ktlo")
            "inventory" {
                "items".compounds {
                    add {
                        "id"("minecraft:stone")
                        "count" byte 34
                        "durability"(Short.MIN_VALUE)
                        "pages" {
                            "lol"  string "kek"
                            "popka" string "zopka"
                        }
                    }
                    add {
                        "id"("minecraft:air")
                        "count" byte 0
                        "durability" short 33
                        "pages" {
                            // Empty
                        }
                    }
                }
                "metadata".byteArray(56, -35, 0, 98)
            }
            "moment".listOf(33.5f, 89.654f, -85.0f)
        }
        val actual = player2nbt(player)
        assertEquals(expected, actual)
        assertEquals(expected.toString(), actual.toString())
        assertEquals(expected.hashCode(), actual.hashCode())
        println(actual)

        // Deserialize
        val actualPlayer = nbt2player(actual)
        assertEquals(player, actualPlayer)
        println(actualPlayer)
    }

    @Test
    fun notchianBigObject() {
        val expected = bigNBTObject
        val bytes = NBT.Default.dump(BigNBTObject.serializer(), expected)
        val actual = NBT.Default.load(BigNBTObject.serializer(), bytes)
        assertEquals(expected, actual)
    }

    @Serializable
    data class MyObject(val valueA: String, val valueB: String?)

    @Test
    fun nullableField() {
        val expected = MyObject("one", null)
        val data = NBT.Default.dump(MyObject.serializer(), expected)
        val actual = NBT.Default.load(MyObject.serializer(), data)
        assertEquals(expected, actual)
    }
}
