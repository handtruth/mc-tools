plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"

    commonMainImplementation(kotlinx("serialization-core"))
    commonTestImplementation(kotlinx("serialization-json"))
}
