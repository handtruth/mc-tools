package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTListDecoder(
    private val value: List<*>,
    conf: NBTSerialFormat,
    serializersModule: SerializersModule
) : NBTCompositeDecoder(conf, serializersModule) {

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return value.size
    }

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Any? {
        return value[index]
    }
}
