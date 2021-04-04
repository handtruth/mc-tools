package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.UByteField
import com.handtruth.mc.paket.util.BYTE_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object UByteCodec : Codec<UByte> {
    override fun measure(value: UByte): Int = BYTE_SIZE
    override fun read(input: Input): UByte = input.readUByte()
    override fun write(output: Output, value: UByte): Unit = output.writeUByte(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): UByteField {
        return paket.field(UByteField())
    }
}
