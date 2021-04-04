package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.BOOLEAN_SIZE
import com.handtruth.mc.paket.codec.BooleanCodec
import com.handtruth.mc.paket.util.readBoolean
import com.handtruth.mc.paket.util.writeBoolean
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class BooleanField(public var boolean: Boolean = false) : AbstractField<Boolean>() {
    override val size: Int get() = BOOLEAN_SIZE

    override var value: Boolean by this::boolean

    override val codec: BooleanCodec get() = BooleanCodec

    override fun read(input: Input) {
        boolean = input.readBoolean()
    }

    override fun write(output: Output) {
        output.writeBoolean(boolean)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return boolean
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        this.boolean = value
    }
}
