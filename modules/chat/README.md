MC:Tools Chat
===========================================================

There are some DSL and algorithms for chat object. This object represents a chat
message, also Minecraft server sends this object in description field of the
server status.

Usage
-----------------------------------------------------------

### Declare dependency

Firstly, you need to declare a dependency in your project.

#### Gradle

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:tools-chat:$toolsVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:tools-chat-jvm:$toolsVersion")
}
```

### Functions and DSL

You can get an actual length of this object in characters with property
`ChatMessage::length`

You can simplify its structure with `ChatMessage::flatten` method.

A lot of Minecraft servers use control sequences in their description to
decorate text. You can parse this sequences with
`fun parseControlSequences(value: String): ChatMessage` function. Also, there
is `ChatMessage::resolveControlSequences` method that returns a new
`ChatMessage` with parsed control sequences.

`ChatMessage::toString` clears all the decoration on the ChatMessage object and
returns a simple `String`.

`ChatMessage::toChatString` creates JSON string that represents Chat
message. You can use it to generate `tellraw` command, for an instance.

`ChatMessage.Companion::parse` reads JSON string and returns `ChatMessage`.

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
