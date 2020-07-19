Minecraft Tools
======================================

This library brings some tools Minecraft. It can get status of a
Minecraft server and player profile from Mojang server.

Usage
--------------------------------------

### Add Dependency

This library is not finished yet. That's because it depends on
[kotlinx.io]. Currently [kotlinx.io] is in active development. I have
builds of [kotlinx.io] in my local Maven repository. So, you need to
add https://mvn.handtruth.com Maven repository. I do NOT guarantee that
this repository will persist. Also, this library is meant to be multi
platform, but currently only JVM target is supported.

In Gradle you can add dependency this way.

```kotlin
// Gradle Kotlin DSL

repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:mctools:$paketVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:mctools-jvm:$paketVersion")
}
```

### Get Server Status

Example code below shows how to do it.

```kotlin
val client = MinecraftClient("mc.example.com", 25565)
client.use { c ->
    // Get parsed status object
    val status: ServerStatus = c.getStatus()
    // After that you can ping the server if you need to
    val ping = client.ping()
        .take(3)
        .map { it.inMilliseconds }
        .reduce { a, b -> a + b } / 3
    println("Status: $status")
    println("Ping:   ${ping}ms")
}
```

### Mojang API

Get player UUID by its name

```kotlin
val id = Mojang.getUUIDbyName("Ktlo").id
println("ID: $id")
```

Get player profile and skin

```kotlin
val profile = Mojang.getProfile(data.id)
val textures = profile.textures
println("Cape: ${textures.cape}")
println("Skin: ${textures.skin}")
println("Is Alex model?: ${textures.isAlex}")
```

That's all. If you need more, you can create an issue in this repository
or even create a pull request ;)

### Chat Message

There are some DSL and algorithms for chat object. Minecraft server
sends this object in description field of the server status.

You can get an actual length of this object in characters with property
`ChatMessage::length`

You can simplify its structure with `ChatMessage::flatten` method.

A lot of Minecraft servers use control sequences in their description
to decorate text. You can parse this sequences with
`fun parseControlSequences(value: String): ChatMessage` function. Also,
there is `ChatMessage::resolveControlSequences` method that returns a
new `ChatMessage` with parsed control sequences.

`ChatObject::toString` clears all the decoration on the ChatMessage
object and returns a simple `String`.

`ChatObject::toChatString` creates JSON string that represents Chat
message. You can use it to generate `tellraw` command, for an instance.

If you need to create your own `ChatMessage` object you can either
use its constructor or `ChatMessage` builder.

```kotlin
val chat = buildChat {
    text("One")
    text("Two")
    italic {
        text("Three")
        bold {
            text("Four")
            color(ChatMessage.Color.Gold) {
                underlined {
                    text("Five")
                }
                italic(false) {
                    text("Six")
                }
            }
        }
    }
    obfuscated {
        text("Seven")
    }
}
```

`buildChat` is a very smart function. It tries to make the result object
simple as possible.

`ChatMessage` object was designed to be simple enough to be used in your
own code not only for reading but also for construction.

For example, you can create a valid tellraw command like this.

```kotlin
val chat = buildChat {
    color(ChatMessage.Color.Gold) {
        bold {
            text("Hello")
        }
        text(" ")
        italic {
            text("World!!!")
        }
    }
}
println("/tellraw @a ${chat.toChatString()}")
/* Output
/tellraw @a ["",{"text":"Hello","bold":true,"color":"gold"},{"text":" ","color":"gold"},{"text":"World!!!","italic":true,"color":"gold"}]
*/
```

[kotlinx.io]: https://github.com/Kotlin/kotlinx-io
