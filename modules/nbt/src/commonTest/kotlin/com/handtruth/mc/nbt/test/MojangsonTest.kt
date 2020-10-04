package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.*
import com.handtruth.mc.nbt.util.Position
import com.handtruth.mc.nbt.util.readJsonString
import com.handtruth.mc.nbt.util.writeJsonString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MojangsonTest {
    private val format =
        NBTStringCodec(NBTStringConfig.Default.copy(pretty = true, quoteValues = false)) + NBTSerialFormat()

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
                "inner":{
                    key: "some-key",
                    value: -56890984387l,
                }}
        """.trimIndent()
        val tag = format.read(string)
        val expected = buildCompoundTag {
            "byte" byte 8
            "short-short.short_short" short -56
            "int" int 11
            "long" long 70
            "float" float 0.125f
            "Float2" float 5e-2f
            "double" double 666.0
            "double2" double 23.0
            "double3" double -23e4
            "string \n string" string "actually-string"
            "bytes".byteArray(33, 23, 65, 22)
            "integers".intArray(23234, 34398, 333)
            "longs".longArray(7654324567, -78654324567, 67543, -2389088965453)
            "listOfCompound" compounds {
                add {}
                add {}
                add {}
            }
            "emptyList".list(TagID.End.resolver) {}
            "listOfInt".listOf(23456, -876543, 68568)
            "listOfDouble".listOf(2365.0, 1e-2)
            "listOfString".listOf("L", "K", "M")
            "inner" {
                "key" string "some-key"
                "value" long -56890984387
            }
        }
        assertEquals(expected, tag)
        val tag2 = format.read(format.write(tag))
        assertEquals(expected, tag2)
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
            format.read("")
        }
        assertEquals(Position(1, 1, 0), e.position)
    }
}
