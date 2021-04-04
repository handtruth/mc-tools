package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.INT_SIZE
import com.handtruth.mc.paket.codec.IntCodec
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class IntField(public var int: Int = 0) : AbstractField<Int>() {
    override val size: Int get() = INT_SIZE

    override var value: Int by this::int

    override val codec: IntCodec get() = IntCodec

    override fun read(input: Input) {
        int = input.readInt()
    }

    override fun write(output: Output) {
        output.writeInt(int)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return int
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.int = value
    }
}
