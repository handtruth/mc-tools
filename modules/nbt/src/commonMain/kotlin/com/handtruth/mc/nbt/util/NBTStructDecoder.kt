package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.types.Dynamic
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTStructDecoder(
    private val value: Dynamic,
    conf: NBTSerialFormat,
    serializersModule: SerializersModule
) : NBTCompositeDecoder(conf, serializersModule) {
    override fun decodeCollectionSize(descriptor: SerialDescriptor) = descriptor.elementsCount

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Any? {
        val name = descriptor.getElementName(index)
        return value.getOrNull(name)
    }
}
