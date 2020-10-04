package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTStructDecoder(
    private val tag: CompoundTag,
    serializersModule: SerializersModule
) : NBTCompositeDecoder(serializersModule) {
    override fun decodeCollectionSize(descriptor: SerialDescriptor) = descriptor.elementsCount

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Tag<*> {
        val name = descriptor.getElementName(index)
        return tag.value[name] ?: Tag.empty
    }
}
