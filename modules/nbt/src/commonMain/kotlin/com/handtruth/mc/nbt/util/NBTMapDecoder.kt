package com.handtruth.mc.nbt.util

import com.handtruth.mc.nbt.NBTSerialFormat
import com.handtruth.mc.types.Dynamic
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule

internal class NBTMapDecoder(
    private val value: Dynamic,
    conf: NBTSerialFormat,
    serializersModule: SerializersModule
) : NBTCompositeDecoder(conf, serializersModule) {

    override fun decodeCollectionSize(descriptor: SerialDescriptor) = value.fields.size * 2

    private var current: CurrentElement = CurrentElement.Key

    override fun retrieveTag(descriptor: SerialDescriptor, index: Int): Any {
        return when (current) {
            CurrentElement.Key -> {
                current = CurrentElement.Value
                value.fields.keys.elementAt(index / 2)
            }
            CurrentElement.Value -> {
                current = CurrentElement.Key
                value.fields.values.elementAt(index / 2)
            }
        }
    }
}
