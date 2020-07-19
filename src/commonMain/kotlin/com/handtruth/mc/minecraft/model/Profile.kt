@file:UseSerializers(UUIDSerializer.Mojang::class, ProfilePropertyDeserializer::class)

package com.handtruth.mc.minecraft.model

import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.minecraft.UUIDSerializer
import com.handtruth.mc.minecraft.util.ProfilePropertyDeserializer
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
