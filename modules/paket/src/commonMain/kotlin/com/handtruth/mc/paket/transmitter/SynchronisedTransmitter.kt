package com.handtruth.mc.paket.transmitter

import kotlinx.coroutines.sync.Mutex

internal class SynchronisedReceiver(private val receiver: Receiver) : Receiver by receiver {
    private val mutex = Mutex()

    override suspend fun catch() {
        mutex.lock()
        try {
            receiver.catch()
        } catch (thr: Throwable) {
            mutex.unlock()
            throw thr
        }
    }

    override suspend fun drop() {
        receiver.drop()
        mutex.unlock()
    }

    override fun close() {
        receiver.close()
        if (mutex.isLocked) {
            try {
                mutex.unlock()
            } catch (e: IllegalStateException) {
                // do nothing
            }
        }
    }
}

internal class SynchronisedSender(private val sender: Sender) : Sender by sender {
    private val mutex = Mutex()

    override suspend fun prepare() {
        mutex.lock()
        try {
            sender.prepare()
        } catch (thr: Throwable) {
            mutex.unlock()
            throw thr
        }
    }

    override suspend fun commit() {
        sender.commit()
        mutex.unlock()
    }

    override fun rollback() {
        sender.rollback()
        mutex.unlock()
    }

    override fun close() {
        sender.close()
        if (mutex.isLocked) {
            try {
                mutex.unlock()
            } catch (e: IllegalStateException) {
                // do nothing
            }
        }
    }
}
