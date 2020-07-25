MC:Tools NBT
=======================================================

This module provides some utilities to work with Named Binary Tags format in
Kotlin.

Usage
-------------------------------------------------------

### Declare dependency

Firstly, you need to declare a dependency in your project.

#### Gradle

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:tools-nbt:$toolsVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:tools-nbt-jvm:$toolsVersion")
}
```

### Build a Compound Tag

You can create a compound tag using `buildCompoundTag` builder. By default, it
will be a root compound tag. (root compound tags has not End Tags at the end)

```kotlin
val tag = buildCompoundTag {
    "group"("Them") // group = "Them
    "id"(568) // id = 568
    "members" compounds { // list of compound tags
        add {
            "name"("Ktlo")
            "id"(398.toShort())
        }
        add {
            "name"("Xydgiz")
            "id"((-3).toShort())
        }
    }
    "metadata".array(3, 5, 8, 9, 16, -15) // array of integers
    "byteArray".byteArray(-3, 5, 76, 81)
    "intArray".intArray(58, -98, 334)
    "longArray".longArray(4842, -6496462, 24554679784123)
}
```

You can find other tag builders in [CompoundTag.kt], [ListTag.kt] and
[TagBuilder.kt] files.

### Stringify Compound Tag

There are text representation of NBT format that has an unofficial name
"Mojangson". You can invoke `Tag::toString` to get a Mojangson formatted string.

For example, the above tag has the following string representation.

```kotlin
println(tag.toString(pretty = true))
/* Output
{
    "metadata": [I;3,5,8,9,16,-15],
    "longArray": [L;4842l,-6496462l,24554679784123l],
    "members": [
        {
            "name": "Ktlo",
            "id": 398s
        },
        {
            "name": "Xydgiz",
            "id": -3s
        }
    ],
    "byteArray": [B;-3b,5b,76b,81b],
    "id": 568,
    "intArray": [I;58,-98,334],
    "group": "Them"
}
*/
```

Currently, there are no way to parse Mojangson, but this feature may arrive in
the future.

### Serialization / Deserialization

This library can serialize and deserialize objects as NBT tags. Class `NBT`
implements `BinaryFormat` from **kotlinx-serialization** library. `NBT`
instances also know how to transform a tag to ByteArray.

There are some examples in [SerializerTest.kt].

[CompoundTag.kt]: src/commonMain/kotlin/com/handtruth/mc/nbt/tags/CompoundTag.kt
[ListTag.kt]: src/commonMain/kotlin/com/handtruth/mc/nbt/tags/ListTag.kt
[TagBuilder.kt]: src/commonMain/kotlin/com/handtruth/mc/nbt/TagBuilder.kt
[SerializerTest.kt]: src/commonTest/kotlin/com/handtruth/mc/nbt/test/SerializerTest.kt
