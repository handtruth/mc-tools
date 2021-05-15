package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.*
import com.handtruth.mc.types.*
import io.ktor.utils.io.core.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MCSDBTest {
    private val nbt = NBT(
        tags = TagsModule.MCSDB,
        binary = NBTBinaryConfig.KBT,
        string = NBTStringConfig.Handtruth.copy(pretty = true),
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
                empty-bytes: (),
                space-bytes: ( ),
                big-bytes: (U2VkIHV0IHBlcnNwaWNpYXRpcyB1bmRlIG9tbmlzIGlzdGUgbmF0dXMgZXJyb3Igc2l0IHZvbHVwdGF0ZW0gYWNjdXNhbnRpdW0gZG9sb3JlbXF1ZSBsYXVkYW50aXVtLCB0b3RhbSByZW0gYXBlcmlhbSwgZWFxdWUgaXBzYSBxdWFlIGFiIGlsbG8gaW52ZW50b3JlIHZlcml0YXRpcyBldCBxdWFzaSBhcmNoaXRlY3RvIGJlYXRhZSB2aXRhZSBkaWN0YSBzdW50IGV4cGxpY2Fiby4gTmVtbyBlbmltIGlwc2FtIHZvbHVwdGF0ZW0gcXVpYSB2b2x1cHRhcyBzaXQgYXNwZXJuYXR1ciBhdXQgb2RpdCBhdXQgZnVnaXQsIHNlZCBxdWlhIGNvbnNlcXV1bnR1ciBtYWduaSBkb2xvcmVzIGVvcyBxdWkgcmF0aW9uZSB2b2x1cHRhdGVtIHNlcXVpIG5lc2NpdW50LiBOZXF1ZSBwb3JybyBxdWlzcXVhbSBlc3QsIHF1aSBkb2xvcmVtIGlwc3VtIHF1aWEgZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyLCBhZGlwaXNjaSB2ZWxpdCwgc2VkIHF1aWEgbm9uIG51bXF1YW0gZWl1cyBtb2RpIHRlbXBvcmEgaW5jaWR1bnQgdXQgbGFib3JlIGV0IGRvbG9yZSBtYWduYW0gYWxpcXVhbSBxdWFlcmF0IHZvbHVwdGF0ZW0uIFV0IGVuaW0gYWQgbWluaW1hIHZlbmlhbSwgcXVpcyBub3N0cnVtIGV4ZXJjaXRhdGlvbmVtIHVsbGFtIGNvcnBvcmlzIHN1c2NpcGl0IGxhYm9yaW9zYW0sIG5pc2kgdXQgYWxpcXVpZCBleCBlYSBjb21tb2RpIGNvbnNlcXVhdHVyPyBRdWlzIGF1dGVtIHZlbCBldW0gaXVyZSByZXByZWhlbmRlcml0IHF1aSBpbiBlYSB2b2x1cHRhdGUgdmVsaXQgZXNzZSBxdWFtIG5paGlsIG1vbGVzdGlhZSBjb25zZXF1YXR1ciwgdmVsIGlsbHVtIHF1aSBkb2xvcmVtIGV1bSBmdWdpYXQgcXVvIHZvbHVwdGFzIG51bGxhIHBhcmlhdHVyPw==),
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
        val tag1 = nbt.read(string) as Dynamic
        println(nbt.write(tag1))
        checkTag(tag1)
        val bytes = nbt.write("MCSDB", tag1)
        val (name, tag3) = nbt.read(bytes)
        assertEquals("MCSDB", name)
        checkTag(tag3 as Dynamic)
    }

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

    private val expectedBigBytes = buildPacket {
        writeText(
            "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium " +
                "doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore " +
                "veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim " +
                "ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia " +
                "consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque " +
                "porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, " +
                "adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et " +
                "dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis " +
                "nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex " +
                "ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea " +
                "voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem " +
                "eum fugiat quo voluptas nulla pariatur?"
        )
    }.copy().use { it.readBytes() }

    private fun checkTag(tag: Dynamic) {
        val emptyBytes = tag.getOrNull("empty-bytes")
        assertTrue(emptyBytes is ByteArray)
        assertEquals(0, emptyBytes.size)
        val spaceBytes = tag.getOrNull("space-bytes")
        assertTrue(spaceBytes is ByteArray)
        assertEquals(0, spaceBytes.size)
        val bigBytes = tag.getOrNull("big-bytes")
        assertTrue(bigBytes is ByteArray)
        assertTrue(expectedBigBytes.contentEquals(bigBytes))
        val tag2 = tag.toMutableDynamic()
        tag2["empty-bytes"] = null
        tag2["space-bytes"] = null
        tag2["big-bytes"] = null
        assertDynamicEquals(expected, tag2)
    }
}
