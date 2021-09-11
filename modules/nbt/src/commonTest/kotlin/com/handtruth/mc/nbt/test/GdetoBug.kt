package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.nbt.TagsModule
import com.handtruth.mc.types.buildDynamic
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class BStructure(
    val list: List<Int>,
    val ubyte: UByte,
    val ushort: UShort,
    val uint: UInt,
    val ulong: ULong
)

@Serializable
data class AStructure(
    val b: BStructure?,
    val map: Map<String, Int>
)

class GdetoBugTest {
    @Test
    fun gdet1Bug() {
        val nbt = NBTSerialFormat(TagsModule.MCSDB)

        val structure = AStructure(null, mapOf("lol" to 23))

        val tag = buildDynamic {
            "map" {
                "lol" int 23
            }
        }

        val actual1 = nbt.encodeToNBT(AStructure.serializer(), structure)
        assertEquals(tag, actual1)

        val actual2 = nbt.decodeFromNBT(AStructure.serializer(), tag)
        assertEquals(structure, actual2)
    }

    @Test
    fun gdet2Bug() {
        val nbt = NBTSerialFormat(TagsModule.MCSDB)

        val structure = AStructure(
            BStructure(
                list = listOf(1, 2, 3, -23),
                ubyte = 13u,
                ushort = 4212u,
                uint = 3878932u,
                ulong = 2463754730498368u
            ),
            mapOf("lol" to 23)
        )

        val tag = buildDynamic {
            "b" {
                "list" list {
                    add(1)
                    add(2)
                    add(3)
                    add(-23)
                }
                "ubyte" ubyte 13u
                "ushort" ushort 4212u
                "uint" uint 3878932u
                "ulong" ulong 2463754730498368u
            }
            "map" {
                "lol" int 23
            }
        }

        val actual1 = nbt.encodeToNBT(AStructure.serializer(), structure)
        assertEquals(tag, actual1)

        val actual2 = nbt.decodeFromNBT(AStructure.serializer(), tag)
        assertEquals(structure, actual2)
    }
}
