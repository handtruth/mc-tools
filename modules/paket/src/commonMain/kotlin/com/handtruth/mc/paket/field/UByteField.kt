package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.UByteCodec
import com.handtruth.mc.paket.util.BYTE_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class UByteField(public var ubyte: UByte = 0u) : AbstractField<UByte>() {
    override val size: Int get() = BYTE_SIZE

    override var value: UByte by this::ubyte

    override val codec: UByteCodec get() = UByteCodec

    override fun read(input: Input) {
        ubyte = input.readUByte()
    }

    override fun write(output: Output) {
        output.writeUByte(ubyte)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): UByte {
        return ubyte
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UByte) {
        this.ubyte = value
    }
}
