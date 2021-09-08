@file:UseSerializers(UUID.Serializer.Default::class, ChatMessageJsonSerializer::class)

package com.handtruth.mc.client.model

import com.handtruth.mc.chat.ChatMessage
import com.handtruth.mc.chat.ChatMessageJsonSerializer
import com.handtruth.mc.minecraft.model.Player
import com.handtruth.mc.types.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ServerStatus(
    val version: Version = Version("", -1),
    val players: Players = Players(0, 0, null),
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
