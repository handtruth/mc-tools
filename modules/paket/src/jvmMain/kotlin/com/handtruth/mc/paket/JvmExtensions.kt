package com.handtruth.mc.paket

import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

inline fun <reified P : Paket> paketSource(): PaketSource<*>? =
    P::class.companionObjectInstance.let {
        if (it is PaketSource<*>) it else null
    }
inline fun <reified P : Paket> producePaket() = paketSource<P>()?.let { it.produce() as P }
    ?: P::class.primaryConstructor!!.callBy(emptyMap())
suspend inline fun <reified P : Paket> PaketReceiver.receive(): P = producePaket<P>().also { receive(it) }
inline fun <reified P : Paket> PaketPeeking.peek(): P = producePaket<P>().also { peek(it) }
