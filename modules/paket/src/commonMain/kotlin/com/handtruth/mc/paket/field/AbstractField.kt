package com.handtruth.mc.paket.field

public abstract class AbstractField<T> : Field<T> {
    override fun equals(other: Any?): Boolean = other is Field<*> && value == other.value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "Field($value)"
}
