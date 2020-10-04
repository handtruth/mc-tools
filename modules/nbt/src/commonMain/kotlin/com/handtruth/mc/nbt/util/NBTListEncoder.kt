package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.tags.EndTag
import com.handtruth.mc.nbt.tags.ListTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.tags.TagResolver
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTListEncoder(
    serializersModule: SerializersModule,
    private val parent: NBTEncoder,
    resolver: TagResolver<*>
) : NBTCompositeEncoder(serializersModule) {
    var root: ListTag<*> = ListTag(mutableListOf(), resolver)
        private set

    override fun endStructure(descriptor: SerialDescriptor) {
        parent.tag = root
    }

    private fun checkTaggedAs(resolver: TagResolver<*>) {
        if (root.resolver !== resolver) {
            check(root.resolver == EndTag)
            root = ListTag(mutableListOf(), resolver)
        }
    }

    override fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, tag: Tag<T>) {
        checkTaggedAs(tag.id.resolver)
        @Suppress("UNCHECKED_CAST")
        (root.value as MutableList<Tag<*>>).add(tag)
    }
}
