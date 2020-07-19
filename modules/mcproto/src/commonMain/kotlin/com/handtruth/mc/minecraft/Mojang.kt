package com.handtruth.mc.minecraft

import com.handtruth.mc.minecraft.model.PlayerByNameResponse
import com.handtruth.mc.minecraft.model.Profile
import com.handtruth.mc.minecraft.model.ProfileContext
import com.handtruth.mc.minecraft.util.json
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
