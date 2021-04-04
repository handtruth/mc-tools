package com.handtruth.mc.paket

import kotlinx.coroutines.CoroutineScope

@PaketTreeDsl
inline fun <reified P : Paket, E : Enum<E>> Respondent<E>.receive(
    noinline action: suspend CoroutineScope.(P) -> Unit
) {
    receive(producePaket<P>(), action)
}

@PaketTreeDsl
inline fun <reified P : Paket, E : Enum<E>> Respondent<E>.reply(
    noinline action: suspend CoroutineScope.(P) -> Paket
) {
    reply(producePaket<P>(), action)
}
