package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.*
import com.handtruth.mc.nbt.util.asMap
import com.handtruth.mc.types.Dynamic
import kotlin.reflect.KClass

interface TagsModule {
    val tags: Set<Tag<*>>
    fun tagById(id: Byte): Tag<*>
    fun <T : Any> tagOf(value: T): Tag<T>
    fun tagIdOf(tag: Tag<*>): Byte

    companion object {
        val Mojang: TagsModule = TagsModuleImplementation(
            EndTag, ByteTag, ShortTag, IntTag, LongTag, FloatTag, DoubleTag,
            ByteArrayTag, StringTag, ListTag, CompoundTag, IntArrayTag,
            LongArrayTag
        )

        inline val Default get() = Mojang

        val HandTruth: TagsModule = TagsModuleImplementation(
            EndTag, ByteTag, ShortTag, IntTag, LongTag, FloatTag, DoubleTag,
            ByteArrayTag, StringTag, ListTag, CompoundTag, IntArrayTag,
            LongArrayTag,

            CharTag, BooleanTag, ShortArrayTag, BooleanArrayTag
        )

        val MCSDB: TagsModule = TagsModuleImplementation(
            EndTag, BooleanTag, ByteTag, ShortTag, IntTag, LongTag, UByteTag, UShortTag, UIntTag, ULongTag,
            FloatTag, DoubleTag, CharTag, StringTag, UUIDTag, InstantTag, CompoundTag, ByteArrayTag, ListTag
        )
    }
}

operator fun TagsModule.contains(tag: Tag<*>): Boolean = tag in tags

interface TagsContainer {
    val tagsModule: TagsModule
}

internal class TagsModuleImplementation : TagsModule {
    private val tagById: Map<Int, Tag<*>>
    private val tagByClass: Map<KClass<*>, Tag<*>>
    private val idByTag: Map<Tag<*>, Int>
    override val tags: Set<Tag<*>>

    override fun tagById(id: Byte) = tagById[id.toInt()] ?: error("there are no tag type for id #$id")

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> tagOf(value: T): Tag<T> = when (value) {
        is Dynamic -> CompoundTag as Tag<T>
        is List<*> -> ListTag as Tag<T>
        else -> tagByClass(value::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> tagByClass(type: KClass<out T>) =
        (tagByClass[type] ?: error("there are no tag for $type")) as Tag<T>

    override fun tagIdOf(tag: Tag<*>) = (idByTag[tag] ?: error("this tag do not included in tags module")).toByte()

    constructor(vararg array: Tag<*>) {
        tagById = array.asMap()
        tagByClass = array.associateBy { it.type }
        idByTag = tagById.entries.associate { (key, value) -> value to key }
        tags = idByTag.keys
    }

    constructor(map: Map<Int, Tag<*>>) {
        tagById = map
        tagByClass = map.values.associateBy { it.type }
        idByTag = map.entries.associate { (key, value) -> value to key }
        tags = idByTag.keys
    }
}
