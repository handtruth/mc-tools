package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.types.MutableDynamic
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTStructEncoder(
    conf: NBTSerialFormat,
    serializersModule: SerializersModule,
    private val parent: NBTEncoder
) : NBTCompositeEncoder(conf, serializersModule) {
    private val result = MutableDynamic()

    override fun endStructure(descriptor: SerialDescriptor) {
        parent.tag = result
    }

    override fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, value: T) {
        val name = descriptor.getElementName(index)
        result[name] = value
    }
}
