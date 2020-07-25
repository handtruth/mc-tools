MC:Tools BOM
==================================================

Bill of Materials project. This is not a library. You can use it to simplify
dependency resolution in your projects. Bill of Materials module contains a big
POM file with versions of each module in this library and its target
implementations. Basically, versions should be the same and equal the version
of **tools-bom** module itself.

Usage
-------------------------------------------------

You can use this module to inherit BOM file inside your BOM file, or you can use
it as platform dependency in your application or library. You may also find it
useful for build automation.

### Declare as dependency

#### Gradle

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    fun mctools(module: String) = "com.handtruth.mc:tools-$module"

    val toolsPlatform = platform(mctools("bom:$toolsVersion"))
    implementation(toolsPlatform)
    //compileOnly(toolsPlatform)
    //runtimeOnly(toolsPlatform)

    // Now you can use library modules without specifying any version
    implementation(mctools("chat"))
    api(mctools("nbt"))
}
```

### Inherit in Your BOM Project

#### Gradle

```kotlin
plugins {
    `java-platform`
}

group = "com.example.platform"
version = "0.1.0"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("com.handtruth.mc:tools-bom:$toolsVersion"))
}
```
