package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.nbt.add
import com.handtruth.mc.nbt.buildCompoundTag
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class TagBuilderTest {
    @Test
    fun buildRootTag() {
        val tag = buildCompoundTag {
            "group"("Administrator")
            "id"(568)
            "members".compounds {
                add {
                    "name"("Ktlo")
                    "id"(398.toShort())
                }
                add {
                    "name"("Xydgiz")
                    "id"((-3).toShort())
                }
            }
            "metadata".array(3, 5, 8, 9, 16, -15)
            "byteArray".byteArray(-3, 5, 76, 81)
            "intArray".intArray(58, -98, 334)
            "longArray".longArray(4842, -6496462, 24554679784123)
        }
        println(tag)
        val output = ByteArrayOutput()
        NBT.Default.write(output, tag)
        val bytes = output.toByteArray()
        val input = ByteArrayInput(bytes)
        val actual = NBT.Default.read(input)
        assertEquals(tag, actual)
    }
}
