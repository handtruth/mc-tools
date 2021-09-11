package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTBinaryConfig
import com.handtruth.mc.nbt.asNBTInput
import kotlin.test.Test

class AlternativeFormats {

    private fun open(file: String) =
        javaClass.getResourceAsStream(file)!!.asNBTInput()

    @Test
    fun bedrock() {
        val bedrock = NBTBinaryCodec(binaryConfig = NBTBinaryConfig.Bedrock)
        val tag = bedrock.readNamedBinary(open("bedrock_level.nbt"))
        println(tag)
    }

    @Test
    fun kbt() {
        val kbt = NBTBinaryCodec(binaryConfig = NBTBinaryConfig.KBT)
        val tag = kbt.readNamedBinary(open("kbt.nbt"))
        println(tag)
    }
}
