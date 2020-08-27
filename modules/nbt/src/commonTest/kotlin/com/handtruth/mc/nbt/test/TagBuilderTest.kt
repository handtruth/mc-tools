package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.*
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class TagBuilderTest {

    val javaNBT = NBTBinaryCodec(NBTBinaryConfig.Java) + NBTSerialFormat()

    @Test
    fun buildRootTag() {
        val tag = buildCompoundTag {
            "group"("Them")
            "id"(568)
            "members" compounds {
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
        println(tag.toString(NBTStringConfig.Default.copy(pretty = true)))
        val output = ByteArrayOutput()
        javaNBT.write(output, tag)
        val bytes = output.toByteArray()
        val input = ByteArrayInput(bytes)
        val actual = javaNBT.read(input)
        assertEquals(tag, actual)
    }
}
