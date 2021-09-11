package com.handtruth.mc.types

import com.handtruth.mc.util.ReferenceSet
import kotlinx.collections.immutable.*
import kotlin.reflect.KProperty

public interface Dynamic : Iterable<Map.Entry<String, Any>> {
    public val fields: Map<String, Any>
    public fun getOrNull(name: String): Any?
}

public operator fun Dynamic.get(name: String): Any = getOrNull(name) ?: error("field \"$name\" not found")

public inline operator fun <reified T> Dynamic.getValue(thisRef: Any?, property: KProperty<*>): T {
    return get(property.name) as T
}

public operator fun Dynamic.contains(name: String): Boolean = name in fields

public interface ImmutableDynamic : Dynamic {
    override val fields: ImmutableMap<String, Any>
}

public interface PersistentDynamic : ImmutableDynamic {
    override val fields: PersistentMap<String, Any>
    public /* no operator */ fun set(name: String, value: Any?): PersistentDynamic

    public interface Builder : MutableDynamic {
        override val fields: MutableMap<String, Any>
        public fun build(): PersistentDynamic
    }

    public fun builder(): Builder
}

public inline fun PersistentDynamic.mutate(block: (MutableDynamic) -> Unit): PersistentDynamic =
    builder().apply(block).build()

public inline operator fun PersistentDynamic.invoke(block: MutableDynamic.() -> Unit): PersistentDynamic =
    mutate { it.block() }

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

    public infix fun <T> String.list(@BuilderInference block: MutableList<T>.() -> Unit): PersistentList<T> {
        val list = persistentListOf<T>().mutate(block)
        set(this, list)
        return list
    }

    public infix fun <T> String.mutableList(@BuilderInference block: MutableList<T>.() -> Unit): MutableList<T> {
        val list = mutableListOf<T>()
        list.block()
        set(this, list)
        return list
    }

    public fun String.dynamic(block: MutableDynamic.() -> Unit): PersistentDynamic {
        val result = buildDynamic(block)
        assign(result)
        return result
    }

    public infix fun String.mutableDynamic(block: MutableDynamic.() -> Unit): MutableDynamic {
        val result = buildMutableDynamic(block)
        assign(result)
        return result
    }

    public operator fun String.invoke(block: MutableDynamic.() -> Unit): PersistentDynamic {
        val old = getOrNull(this)
        val result = if (old == null || old !is Dynamic) {
            buildDynamic(block)
        } else {
            when (old) {
                is PersistentDynamic -> old(block)
                else -> buildDynamic { fields += old.fields; block() }
            }
        }
        assign(result)
        return result
    }
}

public inline fun MutableList<Dynamic>.dynamic(block: MutableDynamic.() -> Unit): PersistentDynamic {
    val result = buildDynamic(block)
    add(result)
    return result
}

public inline operator fun <reified T> MutableDynamic.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    set(property.name, value)
}

public fun MutableDynamic(): MutableDynamic = MutableDynamicImpl()

public fun Dynamic(): PersistentDynamic = EmptyDynamic

public inline fun buildMutableDynamic(block: MutableDynamic.() -> Unit): MutableDynamic {
    return MutableDynamic().apply(block)
}

public inline fun buildDynamic(block: MutableDynamic.() -> Unit): PersistentDynamic {
    return persistentMapOf<String, Any>().mutate { it.asMutableDynamic().apply(block) }.asPersistentDynamic()
}

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
        a is Dynamic && b is Dynamic -> dynamicDeepEquals(a, b)
        else -> a != null && b != null && arraysEquals(a, b)
    }
} else {
    true
}

public infix fun Any?.contentDeepEquals(other: Any?): Boolean = anyDeepEquals(this, other)

private fun dynamicDeepEquals(a: Dynamic, b: Dynamic): Boolean {
    if (a.fields.size != b.fields.size) {
        return false
    }
    for ((field, value) in a) {
        val other = b.getOrNull(field) ?: return false
        if (!anyDeepEquals(value, other)) {
            return false
        }
    }
    return true
}

private class ContentToStringContext(val pretty: Boolean, appendable: Appendable) : Appendable by appendable {
    var level = 0
    val references = ReferenceSet<Any>()
}

public fun Any?.contentDeepToString(pretty: Boolean = false): String {
    return buildString {
        with(ContentToStringContext(pretty, this)) {
            appendValueAsString(this@contentDeepToString)
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

public fun Any?.contentDeepHashCode(): Int = valueHashCode(this)

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

private object EmptyDynamic : PersistentDynamic {
    override val fields: PersistentMap<String, Any> = persistentMapOf()
    override fun getOrNull(name: String): Nothing? = null
    override fun iterator() = emptyList<Map.Entry<String, Any>>().iterator()

    override fun set(name: String, value: Any?): PersistentDynamic {
        return if (value != null) {
            PersistentDynamicImpl(persistentMapOf(name to value))
        } else {
            this
        }
    }

    private class Builder : PersistentDynamic.Builder, RWDynamicImpl() {
        override val fields = persistentMapOf<String, Any>().builder()

        override fun build() = PersistentDynamicImpl(fields.build())
    }

    override fun builder(): PersistentDynamic.Builder = Builder()
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

private abstract class RODynamic : AbstractDynamic() {
    final override fun iterator() = fields.entries.iterator()
}

private class DynamicImpl(
    override val fields: Map<String, Any>
) : RODynamic()

private abstract class RWDynamicImpl : AbstractDynamic(), MutableDynamic {
    override fun set(name: String, value: Any?) {
        if (value == null) {
            fields.remove(name)
        } else {
            fields[name] = value
        }
    }

    override fun iterator() = fields.entries.iterator()
}

private class MutableDynamicImpl(
    override val fields: MutableMap<String, Any> = hashMapOf()
) : RWDynamicImpl()

private class ImmutableDynamicImpl(
    override val fields: ImmutableMap<String, Any>
) : RODynamic(), ImmutableDynamic

private class PersistentDynamicImpl(
    override val fields: PersistentMap<String, Any>
) : RODynamic(), PersistentDynamic {
    override fun set(name: String, value: Any?): PersistentDynamic {
        val newMap = if (value == null) {
            fields.remove(name)
        } else {
            fields.put(name, value)
        }
        return PersistentDynamicImpl(newMap)
    }

    private class Builder(
        override val fields: PersistentMap.Builder<String, Any>
    ) : RWDynamicImpl(), PersistentDynamic.Builder {

        override fun build(): PersistentDynamic {
            return PersistentDynamicImpl(fields.build())
        }
    }

    override fun builder(): PersistentDynamic.Builder = Builder(fields.builder())
}

public fun Dynamic.toMutableDynamic(): MutableDynamic {
    val result = MutableDynamic()
    result.fields += fields
    return result
}

public fun Dynamic.toPersistentDynamic(): PersistentDynamic = when (this) {
    is PersistentDynamic -> this
    else -> PersistentDynamicImpl(persistentMapOf<String, Any>().putAll(fields))
}

public fun MutableMap<String, Any>.asMutableDynamic(): MutableDynamic = MutableDynamicImpl(this)

public fun Map<String, Any>.asDynamic(): Dynamic = DynamicImpl(this)

public fun ImmutableMap<String, Any>.asImmutableDynamic(): ImmutableDynamic = ImmutableDynamicImpl(this)

public fun PersistentMap<String, Any>.asPersistentDynamic(): PersistentDynamic = PersistentDynamicImpl(this)
