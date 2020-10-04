package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.ListTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTListDecoder(
    private val tag: ListTag<*>,
    serializersModule: SerializersModule
) : NBTCompositeDecoder(serializersModule) {

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return tag.value.size
    }

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Tag<*> {
        return tag.value[index]
    }
}
