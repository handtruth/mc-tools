plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"

    commonMainImplementation(kotlinx("io"))
    commonMainImplementation(kotlinx("serialization-runtime-common"))
    "jvmMainImplementation"(kotlinx("serialization-runtime"))
}
