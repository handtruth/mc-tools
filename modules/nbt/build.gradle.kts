plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"

    commonMainImplementation(project(":tools-zint"))
    commonMainImplementation(kotlinx("io"))
    commonMainApi(kotlinx("serialization-runtime-common"))
    "jvmMainApi"(kotlinx("serialization-runtime"))
}
