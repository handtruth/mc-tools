package com.handtruth.mc.types

import com.handtruth.mc.util.ReferenceSet

public interface Dynamic : Iterable<Map.Entry<String, Any>> {
    public val fields: Map<String, Any>
    public fun getOrNull(name: String): Any?
}

public operator fun Dynamic.get(name: String): Any = getOrNull(name) ?: error("field \"$name\" not found")

public interface MutableDynamic : Dynamic, MutableIterable<Map.Entry<String, Any>> {
    override val fields: MutableMap<String, Any>
    public operator fun set(name: String, value: Any?)
    public infix fun String.assign(value: Any?): Unit = set(this, value)

    public infix fun String.byte(value: Byte): Unit = set(this, value)
    public infix fun String.short(value: Short): Unit = set(this, value)
    public infix fun String.int(value: Int): Unit = set(this, value)
    public infix fun String.long(value: Long): Unit = set(this, value)
    public infix fun String.ubyte(value: UByte): Unit = set(this, value)
    public infix fun String.ushort(value: UShort): Unit = set(this, value)
    public infix fun String.uint(value: UInt): Unit = set(this, value)
    public infix fun String.ulong(value: ULong): Unit = set(this, value)
    public infix fun String.float(value: Float): Unit = set(this, value)
    public infix fun String.double(value: Double): Unit = set(this, value)
    public infix fun String.list(value: List<*>): Unit = set(this, value)
    public infix fun <T> String.list(@BuilderInference block: MutableList<T>.() -> Unit): Unit =
        set(this, buildList(block))

    public fun String.dynamic(block: MutableDynamic.() -> Unit): MutableDynamic {
        val result = buildMutableDynamic(block)
        assign(result)
        return result
    }

    public operator fun String.invoke(block: MutableDynamic.() -> Unit): MutableDynamic = dynamic(block)
}

public inline fun MutableList<Dynamic>.dynamic(block: MutableDynamic.() -> Unit): Dynamic {
    val result = buildDynamic(block)
    add(result)
    return result
}

public fun MutableDynamic(): MutableDynamic = MutableDynamicImpl()

public fun Dynamic(): Dynamic = EmptyDynamic

public inline fun buildMutableDynamic(block: MutableDynamic.() -> Unit): MutableDynamic {
    return MutableDynamic().apply(block)
}

public inline fun buildDynamic(block: MutableDynamic.() -> Unit): Dynamic = buildMutableDynamic(block)

private fun listEquals(a: List<*>, b: List<*>): Boolean {
    if (a === b) {
        return true
    }
    if (a.size != b.size) {
        return false
    }
    a.zip(b) { one, two ->
        if (!anyDeepEquals(one, two)) {
            return false
        }
    }
    return true
}

private fun arraysEquals(a: Any, b: Any): Boolean = when {
    a is BooleanArray && b is BooleanArray -> a.contentEquals(b)
    a is CharArray && b is CharArray -> a.contentEquals(b)
    a is ByteArray && b is ByteArray -> a.contentEquals(b)
    a is ShortArray && b is ShortArray -> a.contentEquals(b)
    a is IntArray && b is IntArray -> a.contentEquals(b)
    a is LongArray && b is LongArray -> a.contentEquals(b)
    a is UByteArray && b is UByteArray -> a.contentEquals(b)
    a is UShortArray && b is UShortArray -> a.contentEquals(b)
    a is UIntArray && b is UIntArray -> a.contentEquals(b)
    a is ULongArray && b is ULongArray -> a.contentEquals(b)
    a is FloatArray && b is FloatArray -> a.contentEquals(b)
    a is DoubleArray && b is DoubleArray -> a.contentEquals(b)
    a is Array<*> && b is Array<*> -> listEquals(a.asList(), b.asList())
    else -> false
}

private fun anyDeepEquals(a: Any?, b: Any?) = if (a != b) {
    when {
        a is List<*> && b is List<*> -> listEquals(a, b)
        a is Dynamic && b is Dynamic -> a.contentDeepEquals(b)
        else -> a != null && b != null && arraysEquals(a, b)
    }
} else {
    true
}

public infix fun Any?.contentDeepEquals(other: Any?): Boolean = anyDeepEquals(this, other)

public infix fun Dynamic?.contentDeepEquals(other: Dynamic?): Boolean {
    if (this === other) {
        return true
    }
    if (this == null || other == null) {
        return false
    }
    if (this.fields.size != other.fields.size) {
        return false
    }
    for ((field, a) in this) {
        val b = other.getOrNull(field) ?: return false
        if (!anyDeepEquals(a, b)) {
            return false
        }
    }
    return true
}

private class ContentToStringContext(val pretty: Boolean, appendable: Appendable) : Appendable by appendable {
    var level = 0
    val references = ReferenceSet<Any>()
}

public fun Dynamic?.contentToString(pretty: Boolean = false): String {
    this ?: return "null"
    return buildString {
        with(ContentToStringContext(pretty, this)) {
            notice(this@contentToString) {
                appendMap(this@contentToString.fields)
            }
        }
    }
}

private fun Appendable.appendStringValue(value: String) {
    if (value != "true" && value != "false" && value != "null" &&
        value.isNotEmpty() && value[0].let { it in 'a'..'z' || it in 'A'..'Z' || it == '_' } &&
        value.all { it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' || it == '_' || it == '-' }
    ) {
        append(value)
    } else {
        append('"')
        val newValue = value
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        append(newValue)
        append('"')
    }
}

private fun ContentToStringContext.appendReturn() {
    if (pretty) {
        append('\n')
        repeat(level) {
            append('\t')
        }
    }
}

private fun ContentToStringContext.appendListItem(value: Any?) {
    appendReturn()
    appendValueAsString(value)
}

private fun ContentToStringContext.appendCollection(value: Collection<*>) {
    if (value.isEmpty()) {
        append("[]")
        return
    }
    append('[')
    val iter = value.iterator()
    ++level
    appendListItem(iter.next())
    for (entry in iter) {
        append(',')
        appendListItem(entry)
    }
    --level
    appendReturn()
    append(']')
}

private inline fun ContentToStringContext.notice(any: Any, block: () -> Unit) {
    if (any in references) {
        append("<recursion>")
    } else {
        references += any
        block()
        references -= any
    }
}

private fun ContentToStringContext.appendValueAsString(value: Any?) {
    when (value) {
        null -> append("null")
        is String -> appendStringValue(value)
        is Dynamic -> notice(value) { appendMap(value.fields) }
        is Collection<*> -> notice(value) { appendCollection(value) }
        is Map<*, *> -> notice(value) { appendMap(value) }
        is Array<*> -> notice(value) { appendCollection(value.asList()) }
        is BooleanArray -> append(value.contentToString())
        is CharArray -> append(value.contentToString())
        is ByteArray -> append(value.contentToString())
        is ShortArray -> append(value.contentToString())
        is IntArray -> append(value.contentToString())
        is LongArray -> append(value.contentToString())
        is UByteArray -> append(value.contentToString())
        is UShortArray -> append(value.contentToString())
        is UIntArray -> append(value.contentToString())
        is ULongArray -> append(value.contentToString())
        is FloatArray -> append(value.contentToString())
        is DoubleArray -> append(value.contentToString())
        is Char -> append("'$value'")
        else -> append(value.toString())
    }
}

private fun ContentToStringContext.appendEntry(entry: Map.Entry<Any?, Any?>) {
    appendReturn()
    appendValueAsString(entry.key)
    if (pretty) {
        append(": ")
    } else {
        append(':')
    }
    appendValueAsString(entry.value)
}

private fun ContentToStringContext.appendMap(value: Map<*, *>) {
    if (value.isEmpty()) {
        append("{}")
        return
    }
    append('{')
    val iter = value.iterator()
    ++level
    appendEntry(iter.next())
    for (entry in iter) {
        append(',')
        appendEntry(entry)
    }
    --level
    appendReturn()
    append('}')
}

public fun Dynamic?.contentDeepHashCode(): Int {
    this ?: return 0
    return mapHashCode(fields)
}

private fun valueHashCode(value: Any?): Int = when (value) {
    null -> 0
    is Dynamic -> mapHashCode(value.fields)
    is Collection<*> -> collectionHashCode(value)
    is Map<*, *> -> mapHashCode(value)
    is Array<*> -> collectionHashCode(value.asList())
    is BooleanArray -> value.contentHashCode()
    is CharArray -> value.contentHashCode()
    is ByteArray -> value.contentHashCode()
    is ShortArray -> value.contentHashCode()
    is IntArray -> value.contentHashCode()
    is LongArray -> value.contentHashCode()
    is UByteArray -> value.contentHashCode()
    is UShortArray -> value.contentHashCode()
    is UIntArray -> value.contentHashCode()
    is ULongArray -> value.contentHashCode()
    is FloatArray -> value.contentHashCode()
    is DoubleArray -> value.contentHashCode()
    else -> value.hashCode()
}

private fun mapHashCode(map: Map<*, *>): Int =
    map.entries.sumOf { (key, value) -> valueHashCode(key) xor valueHashCode(value) }

private fun collectionHashCode(collection: Collection<*>): Int = collection.sumOf { valueHashCode(it) }

private object EmptyDynamic : Dynamic {
    override val fields: Map<String, Any> = emptyMap()
    override fun getOrNull(name: String): Nothing? = null
    override fun iterator() = emptyList<Map.Entry<String, Any>>().iterator()
}

private abstract class AbstractDynamic : Dynamic {
    override fun getOrNull(name: String): Any? = fields[name]

    override fun equals(other: Any?) = this === other || other is Dynamic && fields == other.fields

    override fun hashCode() = fields.hashCode()

    override fun toString() = buildString {
        append('{')
        joinTo(this) { (key, value) ->
            append(key)
            append("=")
            if (value is String) {
                append('\"')
                append(value)
                append('\"')
            } else {
                append(value)
            }
            ""
        }
        append('}')
    }
}

private class DynamicImpl(
    override val fields: Map<String, Any>
) : AbstractDynamic() {
    override fun iterator() = fields.entries.iterator()
}

private class MutableDynamicImpl(
    override val fields: MutableMap<String, Any> = hashMapOf()
) : AbstractDynamic(), MutableDynamic {
    override fun set(name: String, value: Any?) {
        if (value == null) {
            fields.remove(name)
        } else {
            fields[name] = value
        }
    }

    override fun iterator() = fields.entries.iterator()
}

public fun Dynamic.toMutableDynamic(): MutableDynamic {
    val result = MutableDynamic()
    result.fields += fields
    return result
}

public fun MutableMap<String, Any>.asMutableDynamic(): MutableDynamic = MutableDynamicImpl(this)

public fun Map<String, Any>.asDynamic(): Dynamic = DynamicImpl(this)
