package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.ListTag
import com.handtruth.mc.nbt.tags.Tag
import com.handtruth.mc.nbt.tags.TagResolver

inline infix fun ListTag<Map<String, Tag<*>>>.add(block: CompoundTag.() -> Unit): CompoundTag {
    val tag = buildCompoundTag(false, block)
    add(tag)
    return tag
}

inline fun buildCompoundTag(isRoot: Boolean = true, block: CompoundTag.() -> Unit) =
    CompoundTag(hashMapOf(), isRoot).apply(block)

inline fun <T: Any> buildListTag(resolver: TagResolver<T>, block: ListTag<T>.() -> Unit) =
    ListTag(mutableListOf(), resolver).apply(block)
inline fun buildListOfCompoundTags(block: ListTag<Map<String, Tag<*>>>.() -> Unit): ListTag<Map<String, Tag<*>>> {
    return buildListTag(CompoundTag, block)
}
