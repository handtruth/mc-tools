package com.handtruth.mc.types

import com.handtruth.mc.util.Base64Variants
import com.handtruth.mc.util.UnsafeBytes
import com.handtruth.mc.util.appendBase64
import com.handtruth.mc.util.decodeBase64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Bytes.Serializer::class)
public class Bytes private constructor(private val data: ByteArray) {
    public val size: Int by data::size

    public operator fun get(index: Int): Byte = data[index]

    override fun hashCode(): Int = data.contentHashCode()

    override fun equals(other: Any?): Boolean =
        this === other || other is Bytes && data contentEquals other.data

    public fun appendTo(
        appendable: Appendable,
        ident: String = "",
        variant: Base64Variants = Base64Variants.RFC4648,
        lineLength: Int = -1
    ): Unit = appendable.appendBase64(data, ident, variant, lineLength)

    public fun toString(
        ident: String = "",
        variant: Base64Variants = Base64Variants.RFC4648,
        lineLength: Int = -1
    ): String {
        return buildString { appendTo(this, ident, variant, lineLength) }
    }

    override fun toString(): String = buildString { appendTo(this) }

    public fun toByteArray(): ByteArray = data.copyOf()

    @UnsafeBytes
    public fun getByteArray(): ByteArray = data

    public companion object {
        public fun fromString(
            string: CharSequence,
            variant: Base64Variants = Base64Variants.RFC4648
        ): Bytes {
            val data = decodeBase64(string, variant)
            return Bytes(data)
        }

        public operator fun invoke(data: ByteArray): Bytes = Bytes(data.copyOf())

        public inline operator fun invoke(size: Int, block: (Int) -> Byte): Bytes {
            val data = ByteArray(size) { block(it) }
            @OptIn(UnsafeBytes::class)
            return wrap(data)
        }

        @UnsafeBytes
        public fun wrap(data: ByteArray): Bytes = Bytes(data)
    }

    internal object Serializer : KSerializer<Bytes> {
        override val descriptor =
            PrimitiveSerialDescriptor("com.handtruth.mc.mcsdb.types", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Bytes = fromString(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: Bytes): Unit = encoder.encodeString(value.toString())
    }
}

@OptIn(UnsafeBytes::class)
public fun bytesOf(vararg bytes: Byte): Bytes = Bytes.wrap(bytes)
