package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.UZLongCodec
import com.handtruth.mc.util.measureUZLong
import com.handtruth.mc.util.readUZLong
import com.handtruth.mc.util.writeUZLong
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class UZLongField(public var ulong: ULong = 0uL) : AbstractField<ULong>() {
    override val size: Int get() = measureUZLong(ulong)

    override var value: ULong by this::ulong

    override val codec: UZLongCodec get() = UZLongCodec

    override fun read(input: Input) {
        ulong = input.readUZLong()
    }

    override fun write(output: Output) {
        output.writeUZLong(ulong)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): ULong {
        return ulong
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: ULong) {
        this.ulong = value
    }
}
