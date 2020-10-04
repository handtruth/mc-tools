package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.StringTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTMapDecoder(
    private val tag: CompoundTag,
    serializersModule: SerializersModule
) : NBTCompositeDecoder(serializersModule) {

    override fun decodeCollectionSize(descriptor: SerialDescriptor) = tag.value.size * 2

    private var current: CurrentElement = CurrentElement.Key

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Tag<*> {
        return when (current) {
            CurrentElement.Key -> {
                current = CurrentElement.Value
                StringTag(tag.value.keys.elementAt(index / 2))
            }
            CurrentElement.Value -> {
                current = CurrentElement.Key
                tag.value.values.elementAt(index / 2)
            }
        }
    }
}
