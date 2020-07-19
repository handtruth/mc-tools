package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBTListType
import com.handtruth.mc.nbt.TagID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val errorValue = 1e-4

@Serializable
data class BigNBTObject(@SerialName("Level") val level: Level)

@Serializable
data class Level(
    val shortTest: Short,
    val longTest: Long,
    val byteTest: Byte,
    @SerialName("byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))")
    val byteArrayTest: ByteArray,
    @SerialName("listTest (long)") @NBTListType(TagID.Long)
    val listTest: List<Long>,
    val floatTest: Float,
    val doubleTest: Double,
    val intTest: Int,
    @SerialName("listTest (compound)")
    val listTestCompound: List<Compound>,
    @SerialName("nested compound test")
    val compounds: Map<String, NamedProperty>,
    val stringTest: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Level) return false

        if (shortTest != other.shortTest) return false
        if (longTest != other.longTest) return false
        if (byteTest != other.byteTest) return false
        if (!byteArrayTest.contentEquals(other.byteArrayTest)) return false
        if (listTest != other.listTest) return false
        if (floatTest - other.floatTest > errorValue) return false
        if (doubleTest - other.doubleTest > errorValue) return false
        if (intTest != other.intTest) return false
        if (listTestCompound != other.listTestCompound) return false
        if (compounds != other.compounds) return false
        if (stringTest != other.stringTest) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shortTest.hashCode()
        result = 31 * result + longTest.hashCode()
        result = 31 * result + byteTest
        result = 31 * result + byteArrayTest.contentHashCode()
        result = 31 * result + listTest.hashCode()
        result = 31 * result + floatTest.hashCode()
        result = 31 * result + doubleTest.hashCode()
        result = 31 * result + intTest
        result = 31 * result + listTestCompound.hashCode()
        result = 31 * result + compounds.hashCode()
        result = 31 * result + stringTest.hashCode()
        return result
    }
}

@Serializable
data class Compound(
    @SerialName("created-on")
    val createdOn: Long,
    val name: String
)

@Serializable
data class NamedProperty(val name: String, val value: Float)

val bigNBTObject = BigNBTObject(
    Level(
        shortTest = 32767,
        longTest = 9223372036854775807L,
        byteTest = 127,
        byteArrayTest = ByteArray(1000) { n -> ((n*n*255+n*7)%100).toByte() },
        listTest = listOf(11L, 12L, 13L, 14L, 15L),
        floatTest = 0.49823147f,
        doubleTest = 0.4931287132182315,
        intTest = 2147483647,
        listTestCompound = listOf(
            Compound(
                createdOn = 1264099775885L,
                name = "Compound tag #0"
            ),
            Compound(
                createdOn = 1264099775885L,
                name = "Compound tag #1"
            )
        ),
        compounds = mapOf(
            "egg" to NamedProperty(
                name = "Eggbert",
                value = 0.5f
            ),
            "ham" to NamedProperty(
                name = "Hampus",
                value = 0.75f
            )
        ),
        stringTest = "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!"
    ))
