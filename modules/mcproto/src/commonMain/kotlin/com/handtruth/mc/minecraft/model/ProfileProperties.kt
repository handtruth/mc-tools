package com.handtruth.mc.minecraft.model

import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.minecraft.util.ProfilePropertyDeserializer
import com.handtruth.mc.minecraft.util.decodeBase64AsString
import com.handtruth.mc.minecraft.util.json
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

interface BaseProperty {
    fun encode(): String = toString()
}

interface PropertyFactory<out P: BaseProperty> {
    fun create(value: String): P
}

interface JsonPropertyFactory<P: BaseProperty> : PropertyFactory<P> {
    fun serializer(): DeserializationStrategy<P>
    override fun create(value: String): P = json.parse(serializer(), decodeBase64AsString(value))
}

class ProfileContext private constructor(val factories: Map<String, PropertyFactory<BaseProperty>>) {

    operator fun plus(other: ProfileContext) = ProfileContext(factories + other.factories)
    operator fun plus(pair: Pair<String, PropertyFactory<BaseProperty>>) = ProfileContext(factories + pair)
    operator fun plus(map: Map<String, PropertyFactory<BaseProperty>>) = ProfileContext(factories + map)
    operator fun plus(entry: Map.Entry<String, PropertyFactory<BaseProperty>>) =
        ProfileContext(factories.toMutableMap().apply { put(entry.key, entry.value) })
    @JvmName("plusEntries")
    operator fun plus(iterable: Iterable<Map.Entry<String, PropertyFactory<BaseProperty>>>): ProfileContext {
        val map = factories.toMutableMap()
        for (each in iterable)
            map[each.key] = each.value
        return ProfileContext(map)
    }
    operator fun plus(pairs: Iterable<Pair<String, PropertyFactory<BaseProperty>>>) =
        ProfileContext(factories + pairs)
    operator fun plus(pairs: Sequence<Pair<String, PropertyFactory<BaseProperty>>>) =
        ProfileContext(factories + pairs)
    @JvmName("plusEntries")
    operator fun plus(pairs: Sequence<Map.Entry<String, PropertyFactory<BaseProperty>>>) = this + pairs.asIterable()

    operator fun get(name: String): PropertyFactory<BaseProperty> = factories[name] ?: UnknownProperty

    companion object {
        val default = ProfileContext(mapOf("textures" to TexturesProperty))
        val empty = ProfileContext(emptyMap())

        fun use(context: ProfileContext) {
            ProfilePropertyDeserializer.context = context
        }
    }

    override fun toString() = factories.toString()
    override fun equals(other: Any?) = other is ProfileContext && factories == other.factories
    override fun hashCode() = factories.hashCode()
}

@Serializable
data class TexturesProperty(
    val timestamp: Long,
    val profileId: UUID,
    val profileName: String,
    val textures: MutableMap<String, Texture>,
    val signatureRequired: Boolean = false
) : BaseProperty {
    @Serializable
    data class Texture(
        val url: String,
        val metadata: Metadata? = null
    ) {
        @Serializable
        data class Metadata(val model: String)
    }
    companion object : JsonPropertyFactory<TexturesProperty>

    val skin: Texture? get() = textures["SKIN"]
    val cape: Texture? get() = textures["CAPE"]

    val isAlex: Boolean get() = skin?.let { it.metadata?.model == "slim" } ?: false
}

val Profile.textures: TexturesProperty? get() = this["textures"] as? TexturesProperty

data class UnknownProperty(val value: String) : BaseProperty {
    companion object : PropertyFactory<UnknownProperty> {
        override fun create(value: String) = UnknownProperty(value)
    }
}
