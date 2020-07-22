package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.ListTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.modules.SerialModule

internal class NBTListDecoder(
    private val tag: ListTag<*>,
    context: SerialModule,
    updateMode: UpdateMode
) : NBTCompositeDecoder(context, updateMode) {

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return tag.value.size
    }

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Tag<*> {
        return tag.value[index]
    }
}
