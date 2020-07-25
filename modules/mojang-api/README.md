MC:Tools Mojang API
============================================================

This module contains a simple client to [Mojang API](https://wiki.vg/Mojang_API).

Usage
------------------------------------------------------------

### Declare dependency

Firstly, you need to declare a dependency in your project. This module uses
[Ktor client]. This client needs an engine, so you need to declare preferred
Ktor engine in dependencies.

#### Gradle

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:tools-mojang-api:$toolsVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:tools-mojang-api-jvm:$toolsVersion")

    // If you use JVM, you can specify this Ktor engine
    implementation("io.ktor:ktor-client-cio")
}
```

### Get Player UUID by Its Name

```kotlin
val id = Mojang().getUUIDbyName("Ktlo").id
println("ID: $id")
```

### Get Player Profile and Skin

```kotlin
val profile = Mojang().getProfile(data.id)
val textures = profile.textures
println("Cape: ${textures.cape}")
println("Skin: ${textures.skin}")
println("Is Alex model?: ${textures.isAlex}")
```

That's all. If you need more, you can create an issue in this repository
or even create a pull request ;)

[Ktor client]: https://ktor.io/clients/index.html
