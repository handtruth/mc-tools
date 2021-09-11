package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.tags.*
import com.handtruth.mc.nbt.writeText
import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.buildDynamic
import com.handtruth.mc.types.dynamic
import io.ktor.utils.io.core.*
import kotlin.test.Test
import kotlin.test.assertTrue

class TagBuilderTest {

    val javaNBT = NBT(binary = NBTBinaryConfig.Java)

    @Test
    fun buildRootTag() {
        val tag = buildDynamic {
            "group" assign "Them"
            "id" assign 568
            "members" assign buildList {
                dynamic {
                    "name" assign "Ktlo"
                    "id" assign 398.toShort()
                }
                dynamic {
                    "name" assign "Xydgiz"
                    "id" assign (-3).toShort()
                }
            }
            "metadata" assign intArrayOf(3, 5, 8, 9, 16, -15)
            "byteArray" assign byteArrayOf(-3, 5, 76, 81)
            "intArray" assign intArrayOf(58, -98, 334)
            "longArray" assign longArrayOf(4842, -6496462, 24554679784123)
        }
        println(javaNBT.writeText(tag))
        buildPacket {
            javaNBT.writeNamedBinary(this, "", tag)
        }.use { input ->
            val actual = javaNBT.readNamedBinary(input)
            assertDynamicEquals(tag, actual.second as Dynamic)
        }
    }

    @Test
    fun printAllTags() {
        val tags = listOf(
            BooleanArrayTag, BooleanTag, ByteArrayTag, BytesTag, ByteTag, CharTag,
            CompoundTag, DoubleTag, EndTag, FloatTag, InstantTag, IntArrayTag,
            IntTag, ListTag, LongArrayTag, LongTag, ShortArrayTag, ShortTag,
            StringTag, UByteTag, UIntTag, ULongTag, UShortTag, UUIDTag
        )
        for (tag in tags) {
            assertTrue { tag.toString().startsWith("TAG_") }
        }
    }
}
