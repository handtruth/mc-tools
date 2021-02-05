package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.types.MutableDynamic
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTMapEncoder(
    conf: NBTSerialFormat,
    serializersModule: SerializersModule,
    private val parent: NBTEncoder
) : NBTCompositeEncoder(conf, serializersModule) {
    private val value = MutableDynamic()

    override fun endStructure(descriptor: SerialDescriptor) {
        parent.tag = value
    }

    private var key = ""
    private var current: CurrentElement = CurrentElement.Key

    override fun <T : Any> placeTag(descriptor: SerialDescriptor, index: Int, value: T) {
        when (current) {
            CurrentElement.Key -> {
                key = value as String
                current = CurrentElement.Value
            }
            CurrentElement.Value -> {
                this.value[key] = value
                current = CurrentElement.Key
            }
        }
    }
}
