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
        val bedrock = NBTBinaryCodec(NBTBinaryConfig.Bedrock)
        val tag = bedrock.read(open("bedrock_level.nbt"))
        println(tag)
    }

    @Test
    fun kbt() {
        val kbt = NBTBinaryCodec(NBTBinaryConfig.KBT)
        val tag = kbt.read(open("kbt.nbt"))
        println(tag)
    }

}
