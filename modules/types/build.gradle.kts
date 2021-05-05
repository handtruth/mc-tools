plugins {
    kotlin("plugin.serialization")
}

kotlin {
    explicitApi()
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"

    commonMainImplementation(kotlinx("serialization-core"))
    commonTestImplementation(kotlinx("serialization-json"))
}
