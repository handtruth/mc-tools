package com.handtruth.mc.minecraft.model

import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.minecraft.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable(PlayerSerializer::class)
interface Player {
    val name: String
    val id: UUID
}

@Suppress("FunctionName")
fun Player(name: String, id: UUID): Player =
    SimplePlayer(name, id)

@Serializer(SimplePlayer::class)
internal object PlayerSerializer

@Serializable
private data class SimplePlayer(
    override val name: String,
    @Serializable(UUIDSerializer.Default::class)
    override val id: UUID
) : Player
