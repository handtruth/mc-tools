package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTListEncoder(
    conf: NBTSerialFormat,
    serializersModule: SerializersModule,
    private val parent: NBTEncoder
) : NBTCompositeEncoder(conf, serializersModule) {
    private val value = mutableListOf<Any>()

    override fun endStructure(descriptor: SerialDescriptor) {
        parent.tag = value
    }

    override fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, value: T) {
        this.value.add(value)
    }
}
