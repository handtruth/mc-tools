package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.StringTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTMapEncoder(
    serializersModule: SerializersModule,
    private val parent: NBTEncoder
) : NBTCompositeEncoder(serializersModule) {
    private val map: MutableMap<String, Tag<*>> = hashMapOf()

    override fun endStructure(descriptor: SerialDescriptor) {
        parent.tag = CompoundTag(map)
    }

    private var key = ""
    private var current: CurrentElement = CurrentElement.Key

    override fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, tag: Tag<T>) {
        when (current) {
            CurrentElement.Key -> {
                key = (tag as StringTag).value
                current = CurrentElement.Value
            }
            CurrentElement.Value -> {
                map[key] = tag
                current = CurrentElement.Key
            }
        }
    }
}
