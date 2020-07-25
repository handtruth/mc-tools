plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun mc(name: String) = "$group:$name"
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun ktor(name: String) = "io.ktor:ktor-$name"

    commonMainApi(project(":tools-shared"))
    commonMainImplementation(kotlinx("io"))
    commonMainImplementation(kotlinx("serialization-runtime-common"))
    commonMainImplementation("io.ktor:ktor-client-core")

    "jvmMainImplementation"(kotlinx("serialization-runtime"))
    "jvmMainImplementation"(ktor("io-jvm"))
    "jvmMainImplementation"(ktor("client-core-jvm"))
    "jvmTestImplementation"(ktor("client-cio"))
}
