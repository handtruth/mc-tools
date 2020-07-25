MC:Tools All
======================================================

All libraries as one dependency. Useful, when your code depends on all modules.

Usage
------------------------------------------------------

### Declare dependency

Firstly, you need to declare a dependency in your project.

#### Gradle

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:tools-all:$toolsVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:tools-all-jvm:$toolsVersion")
}
```

### What Next?

As this library depends on all the modules you already know what to do. If you
are not sure, then you gotta go to the specific module to find out, how to use
it.
