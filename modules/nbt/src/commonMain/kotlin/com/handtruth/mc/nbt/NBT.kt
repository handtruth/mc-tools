package com.handtruth.mc.nbt

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface NBT : NBTCodec, NBTBinaryFormat, NBTStringFormat

@Suppress("FunctionName")
fun NBT(binary: NBTBinaryCodec, string: NBTStringCodec, serial: NBTSerialFormat): NBT =
    object : NBT, NBTBinaryCodec by binary, NBTStringCodec by string, NBTSerialFormat by serial {}

@Suppress("FunctionName")
fun NBT(
    binary: NBTBinaryConfig = NBTBinaryConfig.Default,
    string: NBTStringConfig = NBTStringConfig.Default,
    serial: NBTSerialConfig = NBTSerialConfig.Default,
    module: SerializersModule = EmptySerializersModule
): NBT = NBT(NBTBinaryCodec(binary), NBTStringCodec(string), NBTSerialFormat(serial, module))
