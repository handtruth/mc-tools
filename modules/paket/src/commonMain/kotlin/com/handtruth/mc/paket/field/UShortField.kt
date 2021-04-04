package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.UShortCodec
import com.handtruth.mc.paket.util.SHORT_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class UShortField(public var ushort: UShort = 0u) : AbstractField<UShort>() {
    override val size: Int get() = SHORT_SIZE

    override var value: UShort by this::ushort

    override val codec: UShortCodec get() = UShortCodec

    override fun read(input: Input) {
        ushort = input.readUShort()
    }

    override fun write(output: Output) {
        output.writeUShort(ushort)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): UShort {
        return ushort
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UShort) {
        this.ushort = value
    }
}
