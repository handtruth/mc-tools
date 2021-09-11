package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.*
import com.handtruth.mc.nbt.util.Position
import com.handtruth.mc.nbt.util.readJsonString
import com.handtruth.mc.nbt.util.writeJsonString
import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.UUID
import com.handtruth.mc.types.buildDynamic
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MojangsonTest {
    private val format =
        NBTStringCodec(
            stringConfig = NBTStringConfig.Default.copy(
                pretty = true,
                quoteValues = false
            )
        ) + NBTSerialFormat()

    @Test
    fun eachType() {
        val string = """
            {
                "byte" : 8b,
                short-short.short_short: -56s ,
                int:11,long :70l ,float     :   0.125f,
                Float2: +5e-2f,
                double: 666d,
                double2: 23.0,
                double3: -23e4,
                "string \n string" : actually-string,
                bytes: [B;33, 23, 65b , 22b],
                integers:[I; 23234 , 34398, 333 ],
                longs : [L; 7654324567l , -78654324567, 67543, -2389088965453l, ],
                listOfCompound: [{},{},{}],
                emptyList:[],
                listOfInt : [ 23456, -876543, 68568 ],
                listOfDouble : [ 2365d, 1e-2 ],
                listOfString : [L,K,M],
                boolean1: true,
                boolean2: false,
                "inner":{
                    key: "some-key",
                    value: -56890984387l,
                }}
        """.trimIndent()
        val tag = format.readText(string)
        val expected = buildDynamic {
            "byte" assign 8.toByte()
            "short-short.short_short" assign (-56).toShort()
            "int" assign 11
            "long" assign 70L
            "float" assign 0.125f
            "Float2" assign 5e-2f
            "double" assign 666.0
            "double2" assign 23.0
            "double3" assign -23e4
            "string \n string" assign "actually-string"
            "bytes" assign byteArrayOf(33, 23, 65, 22)
            "integers" assign intArrayOf(23234, 34398, 333)
            "longs" assign longArrayOf(7654324567L, -78654324567L, 67543L, -2389088965453L)
            "listOfCompound" assign buildList<Dynamic> {
                repeat(3) {
                    this += Dynamic()
                }
            }
            "emptyList" assign emptyList<Nothing>()
            "listOfInt" assign listOf(23456, -876543, 68568)
            "listOfDouble" assign listOf(2365.0, 1e-2)
            "listOfString" assign listOf("L", "K", "M")
            "boolean1" assign "true"
            "boolean2" assign "false"
            "inner" {
                "key" assign "some-key"
                "value" assign -56890984387L
            }
        }
        assertDynamicEquals(expected, tag as Dynamic)
        val tag2 = format.readText(format.writeText(tag))
        assertDynamicEquals(expected, tag2 as Dynamic)
    }

    @Test
    fun customFormat() {
        val format = NBT(tags = TagsModule.HandTruth, binary = NBTBinaryConfig.KBT, NBTStringConfig.Handtruth)
        val string = """
            {
                hello: 'S',
                world: S,
                sss1: '\t' ,
                sss2: '\u3191',
                shorts: [S; 3s, 5s],
                "true": true,
                "false": false,
                booleans: [b; true, false, false, true]
            }
        """.trimIndent()
        val tag = format.readText(string) as Dynamic
        val expected = buildDynamic {
            "hello" assign 'S'
            "world" assign "S"
            "sss1" assign '\t'
            "sss2" assign '㆑'
            "shorts" assign shortArrayOf(3, 5)
            "true" assign true
            "false" assign false
            "booleans" assign booleanArrayOf(true, false, false, true)
        }
        assertDynamicEquals(expected, tag)
        val tag2 = format.readText(format.writeText(tag))
        assertDynamicEquals(expected, tag2 as Dynamic)
    }

    @Test
    fun MCSDBTags() {
        val format = NBT(tags = TagsModule.MCSDB, binary = NBTBinaryConfig.KBT, NBTStringConfig.Handtruth)
        val string = """
            {
                booleans: [true, false, false, false, true],
                instant1: 2010-02-13,
                instant2: 2010-02-14T13:10:42,
                instant3: 2010-06-01T22:19:44.475Z,
                ubyte: 23ub,
                ushort: 2345us,
                uint: 668u,
                ulong: 359639547ul,
                uuid: {d03cf771-6d96-4fb3-84de-26f7c832238a}
            }
        """.trimIndent()
        val tag = format.readText(string) as Dynamic
        val expected = buildDynamic {
            "booleans" assign listOf(true, false, false, false, true)
            "instant1" assign LocalDate.parse("2010-02-13")
                .atStartOfDayIn(format.stringConfig.timeZone)
            "instant2" assign LocalDateTime.parse("2010-02-14T13:10:42")
                .toInstant(format.stringConfig.timeZone)
            "instant3" assign Instant.parse("2010-06-01T22:19:44.475Z")
            "ubyte" assign 23u.toUByte()
            "ushort" assign 2345u.toUShort()
            "uint" assign 668u
            "ulong" assign 359639547uL
            "uuid" assign UUID.parse("d03cf771-6d96-4fb3-84de-26f7c832238a")
        }
        assertDynamicEquals(expected, tag)
        val tag2 = format.readText(format.writeText(tag).also(::println))
        assertDynamicEquals(expected, tag2 as Dynamic)
    }

    @Test
    fun stringLiteralTest() {
        val cases = listOf(
            "",
            "regular",
            "with   whitespace ",
            "\b\n\"\r\\" + 12.toChar(),
            "a \b c"
        )
        val expectations = listOf(
            "\"\"",
            "\"regular\"",
            "\"with   whitespace \"",
            """"\b\n\"\r\\\f""" + '"',
            """"a \b c""" + '"'
        )
        for ((expected, case) in expectations zip cases) {
            val actual = buildString { writeJsonString(this, case) }
            assertEquals(expected, actual)
            assertEquals(case, readJsonString(actual))
        }
    }

    @Test
    fun stringLiteralReadTest() {
        val cases = listOf(
            "\"\\\"en\\d\\f\\t\\\""
        )
        val expectations = listOf(
            "\"en\\d" + 12.toChar() + "\t\\"
        )
        for ((expected, case) in expectations zip cases) {
            val actual = readJsonString(case)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun unicodeTest() {
        val cases = listOf(
            "\"\\u3191T\"",
            "\"\\u319F\"",
            "\"a\\uaz\"",
            "\"\\u0\""
        )
        val expectations = listOf(
            "㆑T",
            "㆟",
            "a\nz",
            0.toChar().toString()
        )
        for ((expected, case) in expectations zip cases) {
            val actual = readJsonString(case)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun errorCheck() {
        val e = assertFailsWith<NBTParseException> {
            format.readText("")
        }
        assertEquals(Position(1, 0, 0), e.position)
    }
}
