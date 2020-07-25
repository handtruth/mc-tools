package com.handtruth.mc.mojang.test

import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.mojang.Mojang
import com.handtruth.mc.mojang.model.*
import com.handtruth.mc.mojang.util.decodeBase64AsString
import com.handtruth.mc.mojang.util.json
import io.ktor.test.dispatcher.testSuspend
import kotlinx.serialization.Serializable
import kotlin.test.*

class MojangAPITest {

    @BeforeTest
    fun clearContext() {
        ProfileContext.use(ProfileContext.empty)
    }

    val mojang = Mojang()

    @Test
    fun ktloGetUUIDAndProfile() = testSuspend {
        val data = mojang.getUUIDbyName("Ktlo")
        println("Got ID of Ktlo")
        assertFalse(data.demo)
        assertFalse(data.legacy)
        assertEquals(UUID("7bd9e814-d23f-483c-bb4a-91c192ff5351"), data.id)
        val profile = mojang.getProfile(data.id)
        assertEquals("Ktlo", profile.name)
        assertEquals(data.id, profile.id)
        assertFalse(profile.legacy)
        val textures = profile.textures
        assertNotNull(textures)
        assertEquals(data.id, textures.profileId)
        assertEquals(data.name, textures.profileName)
        assertNull(textures.cape)
        assertNotNull(textures.skin)
        assertFalse(textures.isAlex)
        TexturesProperty(textures.timestamp, textures.profileId,
            textures.profileName, textures.textures, textures.signatureRequired)
    }

    @Serializable
    data class ExampleProperty(val field: String, val integer: Int) : BaseProperty {
        companion object : JsonPropertyFactory<ExampleProperty>
    }

    @Test
    fun quantProfileParse() {
        val exampleDecoded = """{"field": "Русский текст, как всегда","integer": 13}"""
        val exampleEncoded = "eyJmaWVsZCI6ICLQoNGD0YHRgdC60LjQuSDRgtC10LrRgdGCLCDQutCw0Log0LLRgdC10LPQtNCwIiwiaW50ZWdlciI6IDEzfQ=="
        val example = """{"name":"example","value":"eyJmaWVsZCI6ICLQoNGD0YHRgdC60LjQuSDRgtC10LrRgdGCLCDQutCw0Log0LLRgdC10LPQtNCwIiwiaW50ZWdlciI6IDEzfQ=="}"""
        val textures = """{"name":"textures","value":"eyJ0aW1lc3RhbXAiOjE1Nzc2NTE0OTg3MTMsInByb2ZpbGVJZCI6ImMxNGEyMjdhMWI5NTQxZWZiMjIzYWQxYWViMjk5MDUwIiwicHJvZmlsZU5hbWUiOiJJUXVhbnQiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzk4MzFhOGM3OWQxYWVjMzhiN2FkNWEzZjRlYTU3ZTU3NDhhMTEwZjg5Y2I2OTQwYjMyZjgyYmUzYTViMzRkMCJ9fX0="}"""
        val somethingElse = """{"name":"kotlin","value":"Lol Kek Kotlin Berg"}"""
        val str = """{"id":"c14a227a1b9541efb223ad1aeb299050","name":"IQuant","properties":[$example,$somethingElse,$textures]}"""
        val context = ProfileContext.default + ("example" to ExampleProperty)
        ProfileContext.use(context)
        assertEquals(exampleDecoded, decodeBase64AsString(exampleEncoded))
        val profile = json.parse(Profile.serializer(), str)
        assertEquals("IQuant", profile.name)
        assertEquals(UUID("c14a227a1b9541efb223ad1aeb299050"), profile.id)
        assertEquals(
            ExampleProperty(
                "Русский текст, как всегда",
                13
            ), profile["example"])
        assertEquals(UnknownProperty("Lol Kek Kotlin Berg"), profile["kotlin"])
        assertNotNull(profile.textures)
        json.stringify(Profile.serializer(), profile)
    }

    @Test
    fun checkContextes() {
        val context = ProfileContext.default + ("example" to ExampleProperty)
        assertEquals(2, context.factories.size)
        assertEquals(ProfileContext.empty + mapOf("textures" to TexturesProperty, "example" to ExampleProperty),
            context)
        assertEquals(ProfileContext.empty +
                sequenceOf("textures" to TexturesProperty, "example" to ExampleProperty).asIterable(), context)
        assertEquals(ProfileContext.empty +
                sequenceOf("textures" to TexturesProperty, "example" to ExampleProperty), context)
        assertEquals(ProfileContext.default + mapOf("example" to ExampleProperty).entries.first(), context)
        assertEquals(ProfileContext.default + mapOf("example" to ExampleProperty).entries.asIterable(), context)
        assertEquals(ProfileContext.default, ProfileContext.default + ProfileContext.empty)
        assertEquals(ProfileContext.default + mapOf("example" to ExampleProperty).entries.asSequence(), context)
        assertEquals(ProfileContext.default.hashCode(), (ProfileContext.empty + ProfileContext.default).hashCode())
        println(context)
    }

    @Test
    fun playersVariant() {
        val playerA = PlayerByNameResponse("Ktlo", UUID.empty, legacy = true, demo = true)
        val string = json.stringify(PlayerByNameResponse.serializer(), playerA)
        val playerB = json.parse(PlayerByNameResponse.serializer(), string)
        assertEquals(playerA, playerB)
    }

}
