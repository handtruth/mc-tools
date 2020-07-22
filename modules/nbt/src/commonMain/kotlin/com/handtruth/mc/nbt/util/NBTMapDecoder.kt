package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.StringTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.modules.SerialModule

internal class NBTMapDecoder(
    private val tag: CompoundTag,
    context: SerialModule,
    updateMode: UpdateMode
) : NBTCompositeDecoder(context, updateMode) {

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
