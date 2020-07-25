MC:Tools Client
======================================

This module provides a simple client for Minecraft Java Edition server. It can
get status of a Minecraft server, but nothing more. There are no plans to
extend this module facilities.

Usage
--------------------------------------

### Add Dependency

#### Gradle

In Gradle, you can add dependency this way.

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:tools-client:$toolsVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:tools-client-jvm:$toolsVersion")
}
```

### Get Server Status

Example code below shows how to fetch server status.

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
