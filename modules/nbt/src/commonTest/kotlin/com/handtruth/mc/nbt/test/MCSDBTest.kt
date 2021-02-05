package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.*
import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.UUID
import com.handtruth.mc.types.buildDynamic
import com.handtruth.mc.types.dynamic
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

class MCSDBTest {
    private val nbt = NBT(
        tags = TagsModule.MCSDB,
        binary = NBTBinaryConfig.KBT,
        string = NBTStringConfig.Handtruth,
        serial = NBTSerialConfig.Default,
        module = EmptySerializersModule
    )

    @Test
    fun allTypes() {
        val string = """
            {
                "true": true,
                "false": false,
                string1: true_,
                byte: -23b,
                short: -237s,
                int: -1111333,
                long: -7638209798l,
                ubyte: 23ub,
                ushort: 237us,
                uint: 1111333u,
                ulong: 7638209798ul,
                float: .125f,
                double: -1488.5,
                char: '\u41',
                string2: "some string",
                uuid: {bf810f1c-ca49-41c8-9141-871452464ebb},
                instant: 1337-12-06T23:42:13,
                empty-compound: {},
                empty-bytes: [B;],
                empty-list: [],
                list-of-compounds: [
                    {
                        name: object,
                        age: 30u,
                        list-of-bools1: [true, true, false, false, true, false, false, true, true, true,],
                        list-of-bools2: [
                            true, true, false, false, true, false, false, true,
                            true, true, true, false, false, false, true, true
                        ]
                    }
                ]
            }
        """.trimIndent()
        val expected = buildDynamic {
            "true" assign true
            "false" assign false
            "string1" assign "true_"
            "byte" byte -23
            "short" short -237
            "int" int -1111333
            "long" long -7638209798
            "ubyte" ubyte 23u
            "ushort" ushort 237u
            "uint" uint 1111333u
            "ulong" ulong 7638209798u
            "float" float .125f
            "double" double -1488.5
            "char" assign 'A'
            "string2" assign "some string"
            "string3" assign null
            "uuid" assign UUID.parseDefault("bf810f1c-ca49-41c8-9141-871452464ebb")
            "instant" assign LocalDateTime.parse("1337-12-06T23:42:13").toInstant(nbt.stringConfig.timeZone)
            "empty-compound" {}
            "empty-bytes" assign byteArrayOf()
            "empty-list" list emptyList<Nothing>()
            "list-of-compounds" list {
                dynamic {
                    "name" assign "object"
                    "age" assign 30u
                    "list-of-bools1" assign listOf(true, true, false, false, true, false, false, true, true, true)
                    "list-of-bools2" assign listOf(
                        true, true, false, false, true, false, false, true,
                        true, true, true, false, false, false, true, true
                    )
                }
            }
        }
        val tag1 = nbt.read(string) as Dynamic
        assertDynamicEquals(expected, tag1)
        val bytes = nbt.write("MCSDB", tag1)
        val (name, tag2) = nbt.read(bytes)
        assertEquals("MCSDB", name)
        assertDynamicEquals(expected, tag2 as Dynamic)
    }
}
