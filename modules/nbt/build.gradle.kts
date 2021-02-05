plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"

    commonMainImplementation(project(":tools-zint"))
    commonMainImplementation(project(":tools-types"))
    commonMainImplementation(kotlinx("io"))
    commonMainApi(kotlinx("serialization-core"))
    commonMainApi(kotlinx("datetime:0.1.1"))
}
