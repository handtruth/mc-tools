plugins {
    id("kotlinx-atomicfu")
    kotlin("plugin.serialization")
}

kotlin {
    explicitApi()
    sourceSets.all {
        languageSettings {
            optIn("io.ktor.utils.io.core.ExperimentalIoApi")
            optIn("io.ktor.utils.io.core.ExperimentalIoApi")
            optIn("kotlinx.coroutines.ObsoleteCoroutinesApi")
        }
    }
}

dependencies {
    commonMainImplementation(project(":tools-zint"))
    commonMainApi("io.ktor:ktor-io")
    commonMainApi("org.jetbrains.kotlinx:kotlinx-serialization-core")

    val atomicfuVersion: String by project
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
    commonTestImplementation("io.ktor:ktor-test-dispatcher")
    commonTestImplementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf")
    commonTestImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    jvmTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug")
    jvmTestImplementation("org.jetbrains.kotlinx:lincheck:2.12")
}
