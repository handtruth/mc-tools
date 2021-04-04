package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.BYTE_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.ByteField
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object ByteCodec : Codec<Byte> {
    override fun measure(value: Byte): Int = BYTE_SIZE
    override fun read(input: Input): Byte = input.readByte()
    override fun write(output: Output, value: Byte): Unit = output.writeByte(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): ByteField {
        return paket.field(ByteField())
    }
}
