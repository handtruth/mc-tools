package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.UZIntCodec
import com.handtruth.mc.util.measureUZInt
import com.handtruth.mc.util.readUZInt
import com.handtruth.mc.util.writeUZInt
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class UZIntField(public var uint: UInt = 0u) : AbstractField<UInt>() {
    override val size: Int get() = measureUZInt(uint)

    override var value: UInt by this::uint

    override val codec: UZIntCodec get() = UZIntCodec

    override fun read(input: Input) {
        uint = input.readUZInt()
    }

    override fun write(output: Output) {
        output.writeUZInt(uint)
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
