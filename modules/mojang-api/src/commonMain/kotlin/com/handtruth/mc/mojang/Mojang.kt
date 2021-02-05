package com.handtruth.mc.mojang

import com.handtruth.mc.mojang.model.PlayerByNameResponse
import com.handtruth.mc.mojang.model.Profile
import com.handtruth.mc.mojang.model.ProfileContext
import com.handtruth.mc.mojang.util.json
import com.handtruth.mc.types.UUID
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import kotlinx.serialization.DeserializationStrategy

class Mojang(private val client: HttpClient) {

    constructor() : this(HttpClient())
    constructor(engine: HttpClientEngine) : this(HttpClient(engine))

    private suspend inline fun <T> invokeGetJsonRequest(url: String, deserializer: DeserializationStrategy<T>): T {
        return json.decodeFromString(deserializer, client.get(url))
    }

    private inline val profileUrl get() = "https://sessionserver.mojang.com/session/minecraft/profile/"

    suspend fun getProfile(uuid: UUID, context: ProfileContext = ProfileContext.default): Profile {
        val content = client.get<String>(profileUrl + uuid.toMojangUUID())
        ProfileContext.use(context)
        return json.decodeFromString(Profile.serializer(), content)
    }

    private inline val uuidByNameUrl get() = "https://api.mojang.com/users/profiles/minecraft/"

    suspend fun getUUIDbyName(name: String): PlayerByNameResponse {
        return invokeGetJsonRequest(uuidByNameUrl + name, PlayerByNameResponse.serializer())
    }
}
