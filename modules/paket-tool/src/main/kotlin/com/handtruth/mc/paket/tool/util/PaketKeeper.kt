package com.handtruth.mc.paket.tool.util

import com.handtruth.mc.paket.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

sealed class PaketKeeper(val type: KClass<out Paket>) {
    abstract fun take(): Paket
    open fun give(paket: Paket) {}

    sealed class Source(type: KClass<out Paket>) : PaketKeeper(type) {
        abstract val source: PaketSource<Paket>

        final override fun take(): Paket = source.produce()

        class Unknown(override val source: PaketSource<Paket>, type: KClass<out Paket>) : Source(type)

        class Creator(override val source: PaketCreator<Paket>, type: KClass<out Paket>) : Source(type)

        class Pool(override val source: PaketPool<Paket>, type: KClass<out Paket>) : Source(type) {
            override fun give(paket: Paket) = source.recycle(paket)
        }

        class Singleton(override val source: PaketSingleton<Paket>, type: KClass<out Paket>) : Source(type)
    }

    class Constructable(type: KClass<out Paket>) : PaketKeeper(type) {
        override fun take() = type.primaryConstructor!!.callBy(emptyMap())
        override fun give(paket: Paket) {}
    }

    companion object {
        operator fun invoke(source: PaketSource<Paket>, type: KClass<out Paket>): PaketKeeper = when (source) {
            is PaketCreator<Paket> -> Source.Creator(source, type)
            is PaketPool<Paket> -> Source.Pool(source, type)
            is PaketSingleton<Paket> -> Source.Singleton(source, type)
            else -> Source.Unknown(source, type)
        }
        operator fun invoke(type: KClass<out Paket>): PaketKeeper = Constructable(type)
    }
}
