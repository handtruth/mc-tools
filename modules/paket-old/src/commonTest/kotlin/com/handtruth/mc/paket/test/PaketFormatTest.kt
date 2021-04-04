package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.Codecs
import com.handtruth.mc.paket.PaketFormat
import com.handtruth.mc.paket.fields.StringCodec
import com.handtruth.mc.paket.register
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaketFormatTest {

    @Serializable
    data class Something(
        val boolean: Boolean,
        val char: Char,
        val byte: Byte,
        val short: Short,
        val int: Int,
        val long: Long,
        val float: Float,
        val double: Double,
        val string: String,
        val list: List<Int>,
        val map: Map<String, Double>,
        val my: MyType,
        val nullValue: MyType?,
        val notNullValue: MyType?
    )

    @Serializable
    data class WithList(
        val list: List<String>
    )

    @Serializable
    data class MyType(
        val string: String
    )

    object MyCodec : Codec<MyType> {
        var written = false
        var read = false
        override fun measure(value: MyType): Int = StringCodec.measure(value.string)
        override fun read(input: Input, old: MyType?): MyType {
            read = true
            return MyType(StringCodec.read(input, old?.string))
        }
        override fun write(output: Output, value: MyType) {
            written = true
            StringCodec.write(output, value.string)
        }
    }

    private inline fun <reified T : Any> performTest(format: PaketFormat, expected: T) {
        val data = format.encodeToByteArray(serializer(), expected)
        val actual: T = format.decodeFromByteArray(serializer(), data)

        assertEquals(expected, actual)
    }

    @Test
    fun paketFormatBigTest() {
        val format = PaketFormat(
            Codecs.Default + Codecs { register(MyType::class.qualifiedName!!, MyCodec) }
        )

        performTest(
            format,
            Something(
                true, 'A', 23, -2445, 989832846, -4677293450893476095,
                3.4f, -46.23324e-5, "Kotlinbergh", listOf(3, 4, 1335, 8 - 2324, 23, -42),
                mapOf("Hello" to 23.42, "World" to -42.23, "Else" to 0.5), MyType("one, two, D, three"),
                null, MyType("value")
            )
        )

        assertTrue(MyCodec.written)
        assertTrue(MyCodec.read)
    }

    @Test
    fun listFormat() {
        val format = PaketFormat()

        performTest(format, WithList(listOf("A", "list", "Kotlinbergh")))
    }
}
