package com.handtruth.mc.nbt

import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

interface NBT : NBTCodec, NBTBinaryFormat, NBTStringFormat

fun NBT(binary: NBTBinaryCodec, string: NBTStringCodec, serial: NBTSerialFormat): NBT =
    object : NBT, NBTBinaryCodec by binary, NBTStringCodec by string, NBTSerialFormat by serial {}

fun NBT(
    binary: NBTBinaryConfig = NBTBinaryConfig.Default,
    string: NBTStringConfig = NBTStringConfig.Default,
    serial: NBTSerialConfig = NBTSerialConfig.Default,
    module: SerialModule = EmptyModule
): NBT = NBT(NBTBinaryCodec(binary), NBTStringCodec(string), NBTSerialFormat(serial, module))
