@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import com.handtruth.mc.paket.fields.*
import kotlinx.serialization.serializer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

interface Codecs {
    fun <T : Any> getOrNull(`class`: KClass<T>): Codec<T>?
    fun getOrNull(name: String): Codec<*>?
    fun <T : Any> codecOrNull(type: KClass<out Codec<T>>): Codec<T>?
    operator fun plus(codecs: Codecs): Codecs

    val all: Collection<CodecInfo<*>>

    data class CodecInfo<T : Any>(
        val codec: Codec<T>,
        val `class`: KClass<T>,
        val name: String
    )

    companion object {
        val Default = Codecs {
            register(CharCodec)
            register(BoolCodec)
            register(Int8Codec)
            register(Int16Codec)
            register(SZInt32Codec)
            register("kotlin.UInt32", UZInt32Codec)
            register(SZInt64Codec)
            register(FloatCodec)
            register(DoubleCodec)
            register(StringCodec)
        }
    }
}

operator fun <T : Any> Codecs.get(`class`: KClass<T>): Codec<T> = getOrNull(`class`)
    ?: error("there are no codec for $`class`")

operator fun Codecs.get(name: String): Codec<*> = getOrNull(name)
    ?: error("there are no codec for \"$name\"")

inline fun <reified T : Any> Codecs.getOrNull(): Codec<T>? = getOrNull(T::class)

inline fun <reified T : Any> Codecs.get(): Codec<T> = get(T::class)

fun <T : Any> Codecs.codec(type: KClass<out Codec<T>>): Codec<T> = codecOrNull(type)
    ?: error("there are no codec $type")

interface MutableCodecs : Codecs {
    override fun plus(codecs: Codecs): MutableCodecs
    operator fun plusAssign(codecs: Codecs)
    operator fun <T : Any> set(`class`: KClass<T>, name: String, codec: Codec<T>)
}

inline fun <reified T : Any> MutableCodecs.register(name: String, codec: Codec<T>) {
    set(T::class, name, codec)
}

inline fun <reified T : Any> MutableCodecs.register(codec: Codec<T>) {
    set(T::class, serializer<T>().descriptor.serialName, codec)
}

fun Codecs.toMutableCodecs(): MutableCodecs = MutableCodecs().also { it += this }

inline fun Codecs.transform(builder: MutableCodecs.() -> Unit): Codecs {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return toMutableCodecs().apply(builder)
}

fun MutableCodecs(): MutableCodecs = MutableCodecsImpl()

inline fun MutableCodecs(builder: MutableCodecs.() -> Unit): MutableCodecs {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return MutableCodecs().apply(builder)
}

inline fun Codecs(builder: MutableCodecs.() -> Unit): Codecs {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return MutableCodecs(builder)
}

fun Codecs(): Codecs = EmptyCodecs

internal object EmptyCodecs : Codecs {
    override val all: Collection<Codecs.CodecInfo<*>> = emptyList()
    override fun <T : Any> getOrNull(`class`: KClass<T>): Nothing? = null
    override fun getOrNull(name: String): Nothing? = null
    override fun <T : Any> codecOrNull(type: KClass<out Codec<T>>): Nothing? = null
    override fun plus(codecs: Codecs): Codecs = codecs
}

internal class MutableCodecsImpl : MutableCodecs {
    private val codecsByClass: MutableMap<KClass<*>, Codecs.CodecInfo<*>> = hashMapOf()
    private val codecsByName: MutableMap<String, Codecs.CodecInfo<*>> = hashMapOf()
    private val codecsByCodecs: MutableMap<KClass<out Codec<*>>, Codecs.CodecInfo<*>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getOrNull(`class`: KClass<T>) = codecsByClass[`class`]?.codec as Codec<T>?
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> codecOrNull(type: KClass<out Codec<T>>): Codec<T>? =
        codecsByCodecs[type]?.codec as Codec<T>?

    override fun getOrNull(name: String): Codec<*>? = codecsByName[name]?.codec

    override operator fun <T : Any> set(`class`: KClass<T>, name: String, codec: Codec<T>) {
        val info = Codecs.CodecInfo(codec, `class`, name)
        codecsByClass[`class`] = info
        codecsByName[name] = info
    }

    override val all get() = codecsByClass.values

    override fun plus(codecs: Codecs): MutableCodecs {
        val result = MutableCodecsImpl()
        result.codecsByClass += codecsByClass
        result.codecsByName += codecsByName
        codecs.all.associateByTo(result.codecsByClass) { it.`class` }
        codecs.all.associateByTo(result.codecsByName) { it.name }
        return result
    }

    override fun plusAssign(codecs: Codecs) {
        codecs.all.associateByTo(codecsByClass) { it.`class` }
        codecs.all.associateByTo(codecsByName) { it.name }
    }
}
