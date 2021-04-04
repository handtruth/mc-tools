package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.util.Path
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object PathCodec : Codec<Path> {
    override fun measure(value: Path) = sizePath(value)
    override fun read(input: Input, old: Path?) = readPath(input)
    override fun write(output: Output, value: Path) = writePath(output, value)
}

@ExperimentalPaketApi
val PathListCodec = ListCodec(PathCodec)

@ExperimentalPaketApi
val NullablePathCodec = NullableCodec(PathCodec)

@ExperimentalPaketApi
fun Paket.path(initial: Path = Path.empty) = field(PathCodec, initial)

@ExperimentalPaketApi
fun Paket.path(initial: String) = field(PathCodec, Path(initial))

@ExperimentalPaketApi
fun Paket.listOfPath(initial: MutableList<Path> = mutableListOf()) = field(PathListCodec, initial)

@ExperimentalPaketApi
fun Paket.nullablePath(initial: Path? = null) = field(NullablePathCodec, initial)

@ExperimentalPaketApi
fun Paket.nullablePath(initial: String? = null) = field(NullablePathCodec, initial?.let { Path(it) })

@ExperimentalPaketApi
@JvmName("listOfPathRO")
fun Paket.listOfPath(initial: List<Path>) = listOfPath(initial.toMutableList())
