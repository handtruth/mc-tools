package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.UIntCodec
import com.handtruth.mc.paket.util.INT_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class UIntField(public var uint: UInt = 0u) : AbstractField<UInt>() {
    override val size: Int get() = INT_SIZE

    override var value: UInt by this::uint

    override val codec: UIntCodec get() = UIntCodec

    override fun read(input: Input) {
        uint = input.readUInt()
    }

    override fun write(output: Output) {
        output.writeUInt(uint)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): UInt {
        return uint
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UInt) {
        this.uint = value
    }
}
