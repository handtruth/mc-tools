package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.FLOAT_SIZE
import com.handtruth.mc.paket.codec.FloatCodec
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class FloatField(public var float: Float = .0f) : AbstractField<Float>() {
    override val size: Int get() = FLOAT_SIZE

    override var value: Float by this::float

    override val codec: FloatCodec get() = FloatCodec

    override fun read(input: Input) {
        float = input.readFloat()
    }

    override fun write(output: Output) {
        output.writeFloat(float)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return float
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        this.float = value
    }
}
