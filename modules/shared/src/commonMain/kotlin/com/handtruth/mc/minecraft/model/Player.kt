package com.handtruth.mc.minecraft.model

import com.handtruth.mc.types.UUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(PlayerSerializer::class)
interface Player {
    val name: String
    val id: UUID
}

@Suppress("FunctionName")
fun Player(name: String, id: UUID): Player =
    SimplePlayer(name, id)

internal object PlayerSerializer : KSerializer<Player> {
    override val descriptor = buildClassSerialDescriptor("com.handtruth.mc.minecraft.model.Player") {
        element("name", String.serializer().descriptor)
        element("id", String.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): Player = SimplePlayer.serializer().deserialize(decoder)

    override fun serialize(encoder: Encoder, value: Player) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.name)
        encodeStringElement(descriptor, 1, value.id.toString())
    }
}

@Serializable
private data class SimplePlayer(
    override val name: String,
    @Serializable(UUID.Serializer.Any::class)
    override val id: UUID
) : Player
