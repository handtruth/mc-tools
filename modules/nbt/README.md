# Module tools-nbt

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
            "id" short 398
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
val strCodec = NBTStringCodec(NBTStringConfig.Default.copy(pretty = true))
println(strCodec.write(tag))
//Or Just println(tag.toString(NBTStringConfig.Default.copy(pretty = true)))
/* Output
{
    metadata: [I;3,5,8,9,16,-15],
    longArray: [L;4842l,-6496462l,24554679784123l],
    members: [
        {
            "name": "Ktlo",
            "id": 398s
        },
        {
            "name": "Xydgiz",
            "id": -3s
        }
    ],
    byteArray: [B;-3b,5b,76b,81b],
    id: 568,
    intArray: [I;58,-98,334],
    group: "Them"
}
*/
```

You can also read Mojangson text with `NBTStringCodec::read`

There are several NBT formats implemented:
- **Java** - Minecraft Java Edition NBT
- **BedrockDisk** - Minecraft Bedrock Edition world data storage format
- **BedrockNet** - Minecraft Bedrock Edition network protocol format
- **KBT** - Internal HandTruth NBT-like format.

To use any of them one should instantiate `NBTBinaryCodec`

```kotlin
val codec = NBTBinaryCodec(NBTBinaryConfig.Java)
val root: CompoundTag = codec.read(byteArray)
```

### Serialization / Deserialization

This library can serialize and deserialize objects as NBT tags. Class `NBTBinaryFormat`
implements `BinaryFormat` from **kotlinx-serialization** library. `NBT`
instances also know how to transform a tag to ByteArray.

There are some examples in [SerializerTest.kt].

### Composition

Starting from version 0.1.0 NBT module of this library slits its functionality between
3 types: `NBTSerialFormat`, `NBTBinaryCodec` and `NBTStringCodec`. You can combine them
to get types with more facilities.

```kotlin
val binaryFormat: NBTBinaryFormat = NBTBinaryCodec() + NBTSerialFormat()
val stringFormat: NBTStringFormat = NBTStringCodec() + NBTSerialFormat()
val codec: NBTCodec = NBTBinaryCodec() + NBTStringCodec()
val nbt: NBT = codec + NBTSerialFormat()
```

[CompoundTag.kt]: src/commonMain/kotlin/com/handtruth/mc/nbt/tags/CompoundTag.kt
[ListTag.kt]: src/commonMain/kotlin/com/handtruth/mc/nbt/tags/ListTag.kt
[TagBuilder.kt]: src/commonMain/kotlin/com/handtruth/mc/nbt/TagBuilder.kt
[SerializerTest.kt]: src/commonTest/kotlin/com/handtruth/mc/nbt/test/SerializerTest.kt
