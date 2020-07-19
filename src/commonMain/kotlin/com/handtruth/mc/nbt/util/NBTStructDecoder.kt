package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.modules.SerialModule

internal class NBTStructDecoder(
    private val tag: CompoundTag,
    context: SerialModule,
    updateMode: UpdateMode
) : NBTCompositeDecoder(context, updateMode) {
    override val size = tag.value.size

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Tag<*> {
        val name = descriptor.getElementName(index)
        return tag.value[name] ?: Tag.empty
    }
}
