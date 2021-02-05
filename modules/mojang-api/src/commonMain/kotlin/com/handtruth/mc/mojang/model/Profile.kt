@file:UseSerializers(UUID.Serializer.Mojang::class, ProfilePropertyDeserializer::class)

package com.handtruth.mc.mojang.model

import com.handtruth.mc.minecraft.model.Player
import com.handtruth.mc.mojang.util.ProfilePropertyDeserializer
import com.handtruth.mc.types.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Profile(
    override val id: UUID,
    override val name: String,
    val properties: List<Property>,
    val legacy: Boolean = false
) : Player {
    data class Property(
        val name: String,
        val value: BaseProperty
    )

    operator fun get(name: String) = properties.find { it.name == name }?.value
}
