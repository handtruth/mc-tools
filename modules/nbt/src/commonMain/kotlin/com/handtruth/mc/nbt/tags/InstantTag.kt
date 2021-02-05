package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.Input
import kotlinx.io.Output

object InstantTag : Tag<Instant> {
    override val type = Instant::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) =
        Instant.fromEpochSeconds(readFlatInt64(input, conf.binaryConfig), readFlatInt32(input, conf.binaryConfig))

    override fun readText(input: Reader, conf: NBTStringCodec): Instant {
        input.skipSpace()
        val time = readTime(input, conf.stringConfig, true)
        return checkNotNull(time) { "time token expected" }
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Instant) {
        writeFlatInt64(output, conf.binaryConfig, value.epochSeconds)
        writeFlatInt32(output, conf.binaryConfig, value.nanosecondsOfSecond)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Instant, level: Int) {
        output.append(value.toLocalDateTime(conf.stringConfig.timeZone).toString())
    }

    override fun toString() = "TAG_Instant"
}
