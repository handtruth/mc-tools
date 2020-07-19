@file:UseSerializers(UUIDSerializer.Default::class, ChatMessageJsonSerializer::class)

package com.handtruth.mc.minecraft.model

import com.handtruth.mc.minecraft.UUIDSerializer
import com.handtruth.mc.minecraft.util.ChatMessageJsonSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ServerStatus(
    val version: Version,
    val players: Players,
    val favicon: String? = null,
    val description: ChatMessage = ChatMessage.empty
) {
    @Serializable
    data class Version(
        val name: String,
        val protocol: Int
    )
    @Serializable
    data class Players(
        val max: Int,
        val online: Int,
        val sample: List<Player>?
    )
}
