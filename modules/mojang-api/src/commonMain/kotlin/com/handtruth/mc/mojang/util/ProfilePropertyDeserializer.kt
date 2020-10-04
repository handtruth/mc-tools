package com.handtruth.mc.mojang.util

import com.handtruth.mc.mojang.model.Profile
import com.handtruth.mc.mojang.model.ProfileContext
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.native.concurrent.ThreadLocal

@Serializable
private data class ActualProperty(val name: String, val value: String)

internal object ProfilePropertyDeserializer : KSerializer<Profile.Property> {
    @ThreadLocal var context = ProfileContext.empty

    override val descriptor = buildClassSerialDescriptor("com.handtruth.mc.minecraft.model.Profile.Property") {
        element(
            "name",
            PrimitiveSerialDescriptor("com.handtruth.mc.minecraft.model.BasePropertyName", PrimitiveKind.STRING)
        )
        element(
            "value",
            PrimitiveSerialDescriptor("com.handtruth.mc.minecraft.model.BaseProperty", PrimitiveKind.STRING)
        )
    }

    override fun deserialize(decoder: Decoder): Profile.Property {
        val property = decoder.decodeSerializableValue(ActualProperty.serializer())
        val value = context[property.name].create(property.value)
        return Profile.Property(property.name, value)
    }

    override fun serialize(encoder: Encoder, value: Profile.Property) {
        // This object not intended to be serializable, but coverage index made me do it...
        val property = ActualProperty(value.name, value.value.encode())
        encoder.encodeSerializableValue(ActualProperty.serializer(), property)
    }
}
