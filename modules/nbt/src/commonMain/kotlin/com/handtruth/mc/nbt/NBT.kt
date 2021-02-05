package com.handtruth.mc.nbt

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface NBT : NBTCodec, NBTBinaryFormat, NBTStringFormat

@Suppress("FunctionName")
fun NBT(
    tags: TagsModule = TagsModule.Default,
    binary: NBTBinaryConfig = NBTBinaryConfig.Default,
    string: NBTStringConfig = NBTStringConfig.Default,
    serial: NBTSerialConfig = NBTSerialConfig.Default,
    module: SerializersModule = EmptySerializersModule
): NBT = NBTBinaryCodec(tags, binary) + NBTStringCodec(tags, string) + NBTSerialFormat(tags, serial, module)
