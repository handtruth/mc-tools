package com.handtruth.mc.util.test

import com.handtruth.mc.util.*
import io.ktor.utils.io.core.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ZIntTest {

    class Case<T>(val value: T, val coded: ByteArray) {
        operator fun component1() = value
        operator fun component2() = coded
    }

    @Test
    fun s32() {
        listOf(
            Case(0, byteArrayOf(0)),
            Case(Int.MAX_VALUE, byteArrayOf(-2, -1, -1, -1, 15)),
            Case(Int.MIN_VALUE, byteArrayOf(-1, -1, -1, -1, 15)),
            Case(-456, byteArrayOf(-113, 7)),
            Case(-87895, byteArrayOf(-83, -35, 10)),
            Case(687, byteArrayOf(-34, 10)),
            Case(48677, byteArrayOf(-54, -8, 5)),
            Case(23, byteArrayOf(46)),
            Case(-10, byteArrayOf(19))
        ).forEachIndexed { i, (value, coded) ->
            val mark = "#$i"
            assertEquals(coded.size, measureSZInt(value), mark)
            val bytes = buildPacket {
                this.writeSZInt(value)
            }
            assertEquals(coded.size, bytes.remaining.toInt(), mark)
            val intermediate = bytes.copy().use { it.readBytes(coded.size) }
            assertEquals(coded.toList(), intermediate.toList(), mark)
            assertEquals(value, bytes.readSZInt(), mark)
        }
    }

    @Test
    fun s64() {
        listOf(
            Case(0L, byteArrayOf(0)),
            Case(Long.MAX_VALUE, byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, -1, 1)),
            Case(Long.MIN_VALUE, byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, 1)),
            Case(-456L, byteArrayOf(-113, 7)),
            Case(-87895L, byteArrayOf(-83, -35, 10)),
            Case(687L, byteArrayOf(-34, 10)),
            Case(48677L, byteArrayOf(-54, -8, 5)),
            Case(23L, byteArrayOf(46)),
            Case(-10L, byteArrayOf(19))
        ).forEachIndexed { i, (value, coded) ->
            val mark = "#$i"
            assertEquals(coded.size, measureSZLong(value), mark)
            val bytes = buildPacket {
                this.writeSZLong(value)
            }
            assertEquals(coded.size, bytes.remaining.toInt(), mark)
            val intermediate = bytes.copy().use { it.readBytes(coded.size) }
            assertEquals(coded.toList(), intermediate.toList(), mark)
            assertEquals(value, bytes.readSZLong(), mark)
        }
    }

    @Test
    fun u32() {
        listOf(
            Case(UInt.MIN_VALUE, byteArrayOf(0)),
            Case(UInt.MAX_VALUE, byteArrayOf(-1, -1, -1, -1, 15)),
            Case(687u, byteArrayOf(-81, 5)),
            Case(48677u, byteArrayOf(-91, -4, 2)),
            Case(23u, byteArrayOf(23))
        ).forEachIndexed { i, (value, coded) ->
            val mark = "#$i"
            assertEquals(coded.size, measureUZInt(value), mark)
            val bytes = buildPacket {
                this.writeUZInt(value)
            }
            assertEquals(coded.size, bytes.remaining.toInt(), mark)
            val intermediate = bytes.copy().use { it.readBytes(coded.size) }
            assertEquals(coded.toList(), intermediate.toList(), mark)
            assertEquals(value, bytes.readUZInt(), mark)
        }
    }

    @Test
    fun u64() {
        listOf(
            Case(ULong.MIN_VALUE, byteArrayOf(0)),
            Case(ULong.MAX_VALUE, byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, 1)),
            Case(687uL, byteArrayOf(-81, 5)),
            Case(48677uL, byteArrayOf(-91, -4, 2)),
            Case(726387615323uL, byteArrayOf(-37, -52, -51, -128, -110, 21)),
            Case(23uL, byteArrayOf(23))
        ).forEachIndexed { i, (value, coded) ->
            val mark = "#$i"
            assertEquals(coded.size, measureUZLong(value), mark)
            val bytes = buildPacket {
                this.writeUZLong(value)
            }
            assertEquals(coded.size, bytes.remaining.toInt(), mark)
            val intermediate = bytes.copy().use { it.readBytes(coded.size) }
            assertEquals(coded.toList(), intermediate.toList(), mark)
            assertEquals(value, bytes.readUZLong(), mark)
        }
    }

    @Test
    fun errors() {
        run {
            val src = buildPacket {
                repeat(5) {
                    writeUByte(255u)
                }
            }
            val message = assertFailsWith<RuntimeException> {
                src.readSZInt()
            }.message
            assertEquals("LEB128 is too big (bigger than 32 bit)", message)
        }
        run {
            val src = buildPacket {
                repeat(10) {
                    writeUByte(255u)
                }
            }
            val message = assertFailsWith<RuntimeException> {
                src.readSZLong()
            }.message
            assertEquals("LEB128 is too big (bigger than 64 bit)", message)
        }
        run {
            val src = buildPacket {
                repeat(5) {
                    writeUByte(255u)
                }
            }
            val message = assertFailsWith<RuntimeException> {
                src.readUZInt()
            }.message
            assertEquals("LEB128 is too big (bigger than 32 bit)", message)
        }
        run {
            val src = buildPacket {
                repeat(10) {
                    writeUByte(255u)
                }
            }
            val message = assertFailsWith<RuntimeException> {
                src.readUZLong()
            }.message
            assertEquals("LEB128 is too big (bigger than 64 bit)", message)
        }
    }
}
