package com.handtruth.mc.paket

import com.handtruth.mc.paket.field.Field
import io.ktor.utils.io.core.*

public open class Paket(public val fields: MutableList<Field<*>> = mutableListOf()) {
    public val size: Int get() = fields.sumOf { it.size }

    public fun read(input: Input) {
        for (field in fields) {
            field.read(input)
        }
    }

    public fun write(output: Output) {
        for (field in fields) {
            field.write(output)
        }
    }

    public inline fun <reified F : Field<*>> field(field: F): F {
        fields += field
        return field
    }

    override fun equals(other: Any?): Boolean {
        if (other is Paket && fields.size == other.fields.size) {
            for (i in 0 until fields.size)
                if (fields[i] != other.fields[i]) {
                    return false
                }
            return true
        }
        return false
    }

    override fun hashCode(): Int = fields.hashCode()

    override fun toString(): String = "Paket($fields)"
}
