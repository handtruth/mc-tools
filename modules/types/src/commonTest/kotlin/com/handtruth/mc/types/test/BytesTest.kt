package com.handtruth.mc.types.test

import com.handtruth.mc.types.Bytes
import com.handtruth.mc.types.bytesOf
import kotlin.test.Test
import kotlin.test.assertEquals

class BytesTest {
    @Test
    fun bytesTest() {
        val bytes = Bytes(byteArrayOf(3, 4, 5))
        println(bytes)
    }

    @Test
    fun base64reminderDecode() {
        val str = listOf(
            "YQ==",
            "YTA=",
            "YTB6",
        )
        val expected = listOf(
            bytesOf('a'.code.toByte()),
            bytesOf('a'.code.toByte(), '0'.code.toByte()),
            bytesOf('a'.code.toByte(), '0'.code.toByte(), 'z'.code.toByte()),
        )
        str.zip(expected).withIndex().forEach { (i, pair) ->
            val (a, b) = pair
            assertEquals(b, Bytes.fromString(a), "#$i")
        }
        val str2 = listOf(
            "YQ",
            "YTA",
            "YTB6",
        )
        str2.zip(expected).withIndex().forEach { (i, pair) ->
            val (a, b) = pair
            assertEquals(b, Bytes.fromString(a), "#$i")
        }
    }

    val samples = listOf(
        """
            Mauris sodales volutpat faucibus. Ut nec mi sapien. In feugiat malesuada nulla, sit amet mattis dui tempor
            sit amet. Integer faucibus a lacus nec feugiat. Vestibulum maximus leo quis mi facilisis vestibulum. Donec
            tincidunt nisi in tristique convallis. Donec felis metus, sollicitudin nec felis nec, aliquet convallis
            dolor. Nam tempor, eros eu ultrices accumsan, augue lectus gravida velit, sed accumsan quam est quis orci.
            Etiam rhoncus, est ac vulputate semper, eros sapien eleifend erat, sit amet sodales elit velit vitae ex.
            Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum sed
            odio quam.
        """.trimIndent(),
        """
            Vivamus viverra libero id imperdiet ornare. Vivamus semper mauris sollicitudin arcu mollis ullamcorper.
            Donec cursus ex sit amet lobortis fermentum. Quisque aliquam porttitor tellus, sit amet convallis ligula
            dictum non. Praesent in tellus ex. Nulla id orci sit amet nulla elementum condimentum. Maecenas at enim
            massa. Morbi congue augue ut mi blandit, id tempor ipsum semper.
        """.trimIndent()
    )

    val encoded = listOf(
        """
            TWF1cmlzIHNvZGFsZXMgdm9sdXRwYXQgZmF1Y2lidXMuIFV0IG5lYyBtaSBzYXBpZW4uIEluIGZl
            dWdpYXQgbWFsZXN1YWRhIG51bGxhLCBzaXQgYW1ldCBtYXR0aXMgZHVpIHRlbXBvcgpzaXQgYW1l
            dC4gSW50ZWdlciBmYXVjaWJ1cyBhIGxhY3VzIG5lYyBmZXVnaWF0LiBWZXN0aWJ1bHVtIG1heGlt
            dXMgbGVvIHF1aXMgbWkgZmFjaWxpc2lzIHZlc3RpYnVsdW0uIERvbmVjCnRpbmNpZHVudCBuaXNp
            IGluIHRyaXN0aXF1ZSBjb252YWxsaXMuIERvbmVjIGZlbGlzIG1ldHVzLCBzb2xsaWNpdHVkaW4g
            bmVjIGZlbGlzIG5lYywgYWxpcXVldCBjb252YWxsaXMKZG9sb3IuIE5hbSB0ZW1wb3IsIGVyb3Mg
            ZXUgdWx0cmljZXMgYWNjdW1zYW4sIGF1Z3VlIGxlY3R1cyBncmF2aWRhIHZlbGl0LCBzZWQgYWNj
            dW1zYW4gcXVhbSBlc3QgcXVpcyBvcmNpLgpFdGlhbSByaG9uY3VzLCBlc3QgYWMgdnVscHV0YXRl
            IHNlbXBlciwgZXJvcyBzYXBpZW4gZWxlaWZlbmQgZXJhdCwgc2l0IGFtZXQgc29kYWxlcyBlbGl0
            IHZlbGl0IHZpdGFlIGV4LgpQZWxsZW50ZXNxdWUgaGFiaXRhbnQgbW9yYmkgdHJpc3RpcXVlIHNl
            bmVjdHVzIGV0IG5ldHVzIGV0IG1hbGVzdWFkYSBmYW1lcyBhYyB0dXJwaXMgZWdlc3Rhcy4gVmVz
            dGlidWx1bSBzZWQKb2RpbyBxdWFtLg==
        """.trimIndent(),
        """
            Vml2YW11cyB2aXZlcnJhIGxpYmVybyBpZCBpbXBlcmRpZXQgb3JuYXJlLiBWaXZhbXVzIHNlbXBl
            ciBtYXVyaXMgc29sbGljaXR1ZGluIGFyY3UgbW9sbGlzIHVsbGFtY29ycGVyLgpEb25lYyBjdXJz
            dXMgZXggc2l0IGFtZXQgbG9ib3J0aXMgZmVybWVudHVtLiBRdWlzcXVlIGFsaXF1YW0gcG9ydHRp
            dG9yIHRlbGx1cywgc2l0IGFtZXQgY29udmFsbGlzIGxpZ3VsYQpkaWN0dW0gbm9uLiBQcmFlc2Vu
            dCBpbiB0ZWxsdXMgZXguIE51bGxhIGlkIG9yY2kgc2l0IGFtZXQgbnVsbGEgZWxlbWVudHVtIGNv
            bmRpbWVudHVtLiBNYWVjZW5hcyBhdCBlbmltCm1hc3NhLiBNb3JiaSBjb25ndWUgYXVndWUgdXQg
            bWkgYmxhbmRpdCwgaWQgdGVtcG9yIGlwc3VtIHNlbXBlci4=
        """.trimIndent(),
    )

    @Test
    fun base64RFC4648() {
        val cases = samples zip encoded
        for ((i, case) in cases.withIndex()) {
            val (sample, encoded) = case
            val bytes = Bytes(sample.length) { sample[it].code.toByte() }
            run {
                val expected = Bytes.fromString(encoded)
                assertEquals(expected, bytes, "#$i")
            }
        }
    }
}
