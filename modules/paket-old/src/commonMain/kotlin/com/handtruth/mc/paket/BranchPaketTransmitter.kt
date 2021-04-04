package com.handtruth.mc.paket

import com.handtruth.mc.paket.fields.varInt

@OptIn(ObsoletePaketApi::class)
internal class BranchPaketTransmitter<E : Enum<E>>(
    private val bid: E,
    private val parent: PaketTransmitter
) : PaketTransmitter {
    private fun spawn(next: Int): Paket {
        val paket = Paket.Empty(bid)
        paket.varInt(next)
        return paket
    }

    private val body = Paket.Empty(bid)

    private val idField = body.varInt()
    override val idOrdinal by idField

    override suspend fun send(paket: Paket) {
        val toSend = spawn(paket.id.ordinal)
        toSend.fields += paket.fields
        parent.send(toSend)
    }

    override val broken: Boolean get() = parent.broken

    override fun close() = parent.close()

    override var isCaught = false

    override suspend fun catchOrdinal(): Int {
        if (isCaught || !parent.isCaught) {
            parent.catchOrdinal()
        }
        isCaught = true
        parent.peek(body)
        return idOrdinal
    }

    override suspend fun drop() {
        isCaught = false
        parent.drop()
    }

    override val size get() = parent.size - idField.size

    override fun peek(paket: Paket) {
        if (!isCaught) {
            if (parent.isCaught) {
                isCaught = true
                parent.peek(body)
            } else {
                throw IllegalStateException()
            }
        }
        validate(paket.id.ordinal == idOrdinal)
        body.fields += paket.fields
        parent.peek(body)
        body.fields.clear()
        body.fields += idField
    }
}
