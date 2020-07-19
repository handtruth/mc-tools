package com.handtruth.mc.mojang.model

import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.minecraft.UUIDSerializer
import com.handtruth.mc.minecraft.model.Player
import kotlinx.serialization.Serializable

interface LegacyPlayer : Player {
    val legacy: Boolean
}

@Serializable
data class PlayerByNameResponse(
    override val name: String,
    @Serializable(UUIDSerializer.Mojang::class)
    override val id: UUID,
    override val legacy: Boolean = false,
    val demo: Boolean = false
) : LegacyPlayer
