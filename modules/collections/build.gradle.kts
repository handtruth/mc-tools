plugins {
    id("kotlinx-atomicfu")
}

kotlin {
    explicitApi()
}

dependencies {
    val atomicfuVersion: String by project
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
    jvmTestImplementation("org.jetbrains.kotlinx:lincheck:2.12")
}
