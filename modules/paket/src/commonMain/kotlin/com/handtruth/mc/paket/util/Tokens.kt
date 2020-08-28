package com.handtruth.mc.paket.util

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.Channel

internal class Tokens {
    private val subscribers = atomic(0)
    private val round =
        Channel<Token>(Channel.UNLIMITED)

    inner class Token {
        init {
            subscribers.incrementAndGet()
            check(round.offer(this))
        }

        private val _valid = atomic(true)
        val valid: Boolean get() = _valid.value
        fun destroy() {
            subscribers.decrementAndGet()
            _valid.value = false
        }
    }

    fun get(): Token? {
        while (true) {
            val tok = round.poll() ?: return null
            if (tok.valid) {
                round.offer(tok)
                return tok
            }
        }
    }
}