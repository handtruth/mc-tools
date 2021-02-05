package com.handtruth.mc.types.test

import com.handtruth.mc.types.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class UUIDTest {
    @Test
    fun parseTest() {
        with(UUID.parseDefault("01234567-89aB-CdEF-0000-000000000000")) {
            assertEquals(UUID(0x01234567_89ABCDEF, 0), this)
            assertEquals("01234567-89ab-cdef-0000-000000000000", this.toString())
            assertEquals("0123456789abcdef0000000000000000", this.toMojangUUID())
            assertEquals("{01234567-89ab-cdef-0000-000000000000}", this.toGUID())
        }
        with(UUID.parseAny("{Fedcb9a-876504321---10589-985--12216559}")) {
            assertEquals(UUID(0xFedcb9a_876504321u.toLong(), 0x10589_985__12216559), this)
            assertEquals(0xFedcb9a_876504321u.toLong(), most)
            assertEquals(0x10589_985__12216559, least)
            assertEquals("fedcb9a8-7650-4321-1058-998512216559", this.toString())
            assertEquals("fedcb9a8765043211058998512216559", this.toMojangUUID())
            assertEquals("{fedcb9a8-7650-4321-1058-998512216559}", this.toGUID())
        }
        assertFails {
            UUID.parseAny("{Fedcb9a-876504321---10589-985--12216559")
        }
        assertFails {
            UUID.parseAny("Fedcb9a-876504321---10589-985--12216559}")
        }
    }

    @Serializable
    data class WithDefaultUUID(@Serializable(with = UUID.Serializer.Default::class) val uuid: UUID)

    @Serializable
    data class WithGUIDUUID(@Serializable(with = UUID.Serializer.GUID::class) val uuid: UUID)

    @Serializable
    data class WithMojangUUID(@Serializable(with = UUID.Serializer.Mojang::class) val uuid: UUID)

    @Serializable
    data class WithStringUUID(val uuid: String)

    private val json = Json {}

    @Test
    fun defaultSerializerTest() {
        val string = "fedcb9a8-7650-4321-1058-998512216559"

        val obj = WithDefaultUUID(UUID.parseDefault(string))
        val serial = json.encodeToString(WithDefaultUUID.serializer(), obj)
        val str = json.decodeFromString(WithStringUUID.serializer(), serial)
        assertEquals(string, str.uuid)
    }

    @Test
    fun guidSerializerTest() {
        val string = "{fedcb9a8-7650-4321-1058-998512216559}"

        val obj = WithGUIDUUID(UUID.parseGUID(string))
        val serial = json.encodeToString(WithGUIDUUID.serializer(), obj)
        val str = json.decodeFromString(WithStringUUID.serializer(), serial)
        assertEquals(string, str.uuid)
    }

    @Test
    fun mojangSerializerTest() {
        val string = "fedcb9a8765043211058998512216559"

        val obj = WithMojangUUID(UUID.parseMojangUUID(string))
        val serial = json.encodeToString(WithMojangUUID.serializer(), obj)
        val str = json.decodeFromString(WithStringUUID.serializer(), serial)
        assertEquals(string, str.uuid)
    }
}
