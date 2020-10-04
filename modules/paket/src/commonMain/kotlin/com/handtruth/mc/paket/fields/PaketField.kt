package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

class PaketCodec<P : Paket>(val source: PaketSource<P>) : Codec<P> {
    override fun measure(value: P) = value.size
    override fun read(input: Input, old: P?) = (old ?: source.produce()).apply { read(input) }
    override fun write(output: Output, value: P) = value.write(output)
}

fun <P : Paket> PaketListCodec(source: PaketSource<P>) = ListCodec(PaketCodec(source))
fun <P : Paket> NullablePaketCodec(source: PaketSource<P>) = NullableCodec(PaketCodec(source))

fun <P : Paket> Paket.paket(source: PaketSource<P>, initial: P = source.produce()) = field(PaketCodec(source), initial)
fun <P : Paket> Paket.paket(initial: P) = field(PaketCodec(emptyPaketSource()), initial)
fun <P : Paket> Paket.listOfPaket(source: PaketSource<P>, initial: MutableList<P>) = field(PaketListCodec(source), initial)
fun <P : Paket> Paket.nullablePaket(source: PaketSource<P>, initial: P = source.produce()) = field(NullablePaketCodec(source), initial)

@JvmName("listOfPaketRO")
fun <P : Paket> Paket.listOfPaket(source: PaketSource<P>, initial: List<P>) =
    listOfPaket(source, initial.toMutableList())
