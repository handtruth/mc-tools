package com.handtruth.mc.paket

import com.handtruth.mc.paket.fields.Field
import com.handtruth.mc.paket.fields.NullableCodec
import io.ktor.utils.io.core.*

abstract class Paket {
    abstract val id: Enum<*>

    val fields = mutableListOf<Field<*>>()

    val size get() = sizeVarInt(id.ordinal) + fields.sumBy { it.size }

    override fun equals(other: Any?): Boolean {
        if (other is Paket && id == other.id && fields.size == other.fields.size) {
            for (i in 0 until fields.size)
                if (fields[i] != other.fields[i]) {
                    return false
                }
            return true
        }
        return false
    }
    override fun hashCode() = id.hashCode() + fields.hashCode()
    override fun toString(): String {
        val builder = StringBuilder(id.toString()).append(":{ ")
        for (i in 0 until fields.size - 1)
            builder.append(fields[i].toString()).append("; ")
        if (fields.isNotEmpty()) {
            builder.append(fields.last().toString()).append(' ')
        }
        builder.append('}')
        return builder.toString()
    }

    fun <F> field(field: Field<F>): Field<F> {
        fields += field
        return field
    }

    fun <F> field(codec: Codec<F>, initial: F): Field<F> {
        return field(Field(codec, initial))
    }

    fun <F> nullableField(codec: Codec<F>, initial: F? = null): Field<F?> {
        return field(NullableCodec(codec), initial)
    }

    fun <F> listField(codec: Codec<F>, initial: MutableList<F> = mutableListOf()): Field<MutableList<F>> {
        return field(ListCodec(codec), initial)
    }

    fun write(output: Output) {
        writeVarInt(output, id.ordinal)
        writeBody(output)
    }

    fun writeBody(output: Output) {
        for (field in fields)
            field.write(output)
    }

    fun read(input: Input) {
        val otherId = readVarInt(input)
        validate(id.ordinal == otherId) { "Wrong paket id (${id.ordinal} expected, got $otherId)" }
        readBody(input)
    }

    fun readBody(input: Input) {
        for (field in fields)
            field.read(input)
    }

    open fun clear() {}

    private var pool: PaketPool<in Paket>? = null

    fun attachToPool(pool: PaketPool<in Paket>) {
        check(this.pool == null) { "Paket pool already specified for this paket" }
        this.pool = pool
    }

    fun recycle() {
        pool?.recycle(this)
    }

    class Empty<E : Enum<E>>(override val id: E) : Paket() {
        companion object {
            inline operator fun <reified E : Enum<E>> invoke(): Empty<E> {
                return Empty(enumValues<E>()[0])
            }
        }
    }
}
