package com.handtruth.mc.mojang

import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.mojang.model.PlayerByNameResponse
import com.handtruth.mc.mojang.model.Profile
import com.handtruth.mc.mojang.model.ProfileContext
import com.handtruth.mc.mojang.util.json
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.DeserializationStrategy

object Mojang {

    private val client = HttpClient()

    private suspend inline fun <T> invokeGetJsonRequest(url: String, deserializer: DeserializationStrategy<T>): T {
        return json.parse(deserializer, client.get(url))
    }

    private const val profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/"

    suspend fun getProfile(uuid: UUID, context: ProfileContext = ProfileContext.default): Profile {
        val content = client.get<String>(profileUrl + uuid.toMojangUUID())
        ProfileContext.use(context)
        return json.parse(Profile.serializer(), content)
    }

    private const val uuidByNameUrl = "https://api.mojang.com/users/profiles/minecraft/"

    suspend fun getUUIDbyName(name: String): PlayerByNameResponse {
        return invokeGetJsonRequest(uuidByNameUrl + name, PlayerByNameResponse.serializer())
    }

}
